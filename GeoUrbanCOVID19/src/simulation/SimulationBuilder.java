package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import config.Paths;
import datasource.Reader;
import gis.GISPolygon;
import model.Citizen;
import model.Compartment;
import model.Heuristics;
import model.Policy;
import model.PolicyEnforcer;
import output.OutputManager;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;

public class SimulationBuilder implements ContextBuilder<Object> {

	/**
	 * Geography projection id
	 */
	public static final String GEOGRAPHY_PROJECTION_ID = "city";

	/**
	 * Reference to geography projection
	 */
	public Geography<Object> geography;

	/**
	 * City
	 */
	public HashMap<String, GISPolygon> city;

	/**
	 * Neighborhoods
	 */
	public HashMap<String, GISPolygon> neighborhoods;

	/**
	 * Policy enforcer
	 */
	public PolicyEnforcer policyEnforcer;

	/**
	 * Build simulation
	 * 
	 * @param context Simulation context
	 */
	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("GeoUrbanCOVID19");
		// Create geography projection
		this.geography = createGeographyProjection(context);
		// Initialize city
		this.city = readPolygons(Paths.CITY_GEOMETRY_SHAPEFILE);
		for (GISPolygon cityElement : this.city.values()) {
			context.add(cityElement);
		}
		// Initialize neighborhoods
		this.neighborhoods = readPolygons(Paths.NEIGHBORHOODS_FACILITIES_GEOMETRY_SHAPEFILE);
		for (GISPolygon neighborhood : this.neighborhoods.values()) {
			context.add(neighborhood);
		}
		// Add students to simulation
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		ArrayList<Citizen> citizens = createCitizens(simParams.getInteger("susceptibleCount"),
				simParams.getInteger("infectedCount"));
		for (Citizen citizen : citizens) {
			context.add(citizen);
		}
		// Assign families
		ArrayList<Citizen> familyProxies = new ArrayList<Citizen>();
		for (Citizen citizen : citizens) {
			if (citizen.getFamily().isEmpty()) {
				Heuristics.assignFamily(citizen, citizens);
				familyProxies.add(citizen);
			}
		}
		// Assign a house to each family
		for (Citizen proxy : familyProxies) {
			Heuristics.assignHouse(proxy, this.neighborhoods);
		}
		// Assign workplaces
		for (Citizen citizen : citizens) {
			GISPolygon livingNeighborhood = citizen.getLivingNeighborhood();
			NdPoint workplace = Heuristics.getSODBasedWorkplace(null, livingNeighborhood, this.neighborhoods);
			citizen.setWorkplace(workplace);
		}
		// Initialize output manager
		OutputManager outputManager = new OutputManager();
		context.add(outputManager);
		// Schedule policies
		this.policyEnforcer = schedulePolicies(Paths.POLICIES_DATABASE);
		context.add(policyEnforcer);
		return context;
	}

	/**
	 * Create geography projection
	 * 
	 * @param context Simulation context
	 */
	private Geography<Object> createGeographyProjection(Context<Object> context) {
		GeographyParameters<Object> params = new GeographyParameters<Object>();
		GeographyFactory geographyFactory = GeographyFactoryFinder.createGeographyFactory(null);
		Geography<Object> geography = geographyFactory.createGeography(GEOGRAPHY_PROJECTION_ID, context, params);
		return geography;
	}

	/**
	 * Read polygons
	 * 
	 * @param geometryPath Path to geometry file
	 */
	private HashMap<String, GISPolygon> readPolygons(String geometryPath) {
		HashMap<String, GISPolygon> polygons = new HashMap<>();
		List<SimpleFeature> features = Reader.loadGeometryFromShapefile(geometryPath);
		for (SimpleFeature feature : features) {
			MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
			Geometry geometry = multiPolygon.getGeometryN(0);
			String id = (String) feature.getAttribute(1);
			GISPolygon polygon = new GISPolygon(id);
			polygon.setGeometryInGeography(this.geography, geometry);
			polygons.put(id, polygon);
		}
		return polygons;
	}

	/**
	 * Create citizens
	 * 
	 * @param susceptibleCount Number of susceptible citizens
	 * @param infectedCount    Number of infected citizens
	 */
	private ArrayList<Citizen> createCitizens(int susceptibleCount, int infectedCount) {
		ArrayList<Citizen> citizens = new ArrayList<>();
		for (int i = 0; i < infectedCount; i++) {
			Citizen citizen = new Citizen(this, Compartment.INFECTED);
			citizens.add(citizen);
		}
		for (int i = 0; i < susceptibleCount; i++) {
			Citizen citizen = new Citizen(this, Compartment.SUSCEPTIBLE);
			citizens.add(citizen);
		}
		return citizens;
	}

	/**
	 * Schedule policies
	 * 
	 * @param policiesPath Path to policies file
	 */
	private PolicyEnforcer schedulePolicies(String policiesPath) {
		PolicyEnforcer policyEnforcer = new PolicyEnforcer();
		ArrayList<Policy> policies = Reader.readPoliciesDatabase(policiesPath);
		for (Policy policy : policies) {
			policyEnforcer.schedulePolicy(policy);
		}
		return policyEnforcer;
	}

}