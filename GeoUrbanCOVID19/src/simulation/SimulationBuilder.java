package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import config.Paths;
import datasource.Reader;
import gis.GISNeighborhood;
import gis.GISPolygon;
import gis.GISPolygonType;
import model.Citizen;
import model.Compartment;
import model.Heuristics;
import model.SODMatrix;
import output.OutputManager;
import policy.Policy;
import policy.PolicyEnforcer;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.util.collections.Pair;

public class SimulationBuilder implements ContextBuilder<Object> {

	/**
	 * End tick (unit: hours)
	 */
	public static final double END_TICK = 1460;

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
	 * Output manager
	 */
	public OutputManager outputManager;

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
		this.city = readPolygons(Paths.CITY_GEOMETRY_SHAPEFILE, GISPolygonType.SIMPLE, "Id");
		for (GISPolygon cityElement : this.city.values()) {
			context.add(cityElement);
		}
		// Initialize neighborhoods
		this.neighborhoods = readPolygons(Paths.NEIGHBORHOODS_GEOMETRY_SHAPEFILE, GISPolygonType.NEIGHBORHOOD,
				"SIT_2017");
		ArrayList<GISNeighborhood> neighborhoodsList = new ArrayList<>();
		for (GISPolygon neighborhood : this.neighborhoods.values()) {
			neighborhoodsList.add((GISNeighborhood) neighborhood);
			context.add(neighborhood);
		}
		// Initialize output manager
		this.outputManager = new OutputManager();
		context.add(this.outputManager);
		// Add students to simulation
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		ArrayList<Citizen> citizens = createCitizens(simParams.getInteger("susceptibleCount"),
				simParams.getInteger("infectedCount"));
		for (Citizen citizen : citizens) {
			context.add(citizen);
		}
		// Assign families
		ArrayList<Citizen> familyProxies = new ArrayList<>();
		for (Citizen citizen : citizens) {
			if (citizen.getFamily().isEmpty()) {
				Heuristics.assignFamily(citizen, citizens);
				familyProxies.add(citizen);
			}
		}
		// Assign a house to each family
		for (Citizen proxy : familyProxies) {
			Heuristics.assignHouse(proxy, neighborhoodsList);
		}
		// Initialize SOD matrix
		SODMatrix sod = Reader.readSODMatrix(Paths.SOD_MATRIX);
		// Assign workplaces
		for (Citizen citizen : citizens) {
			GISPolygon livingNeighborhood = citizen.getLivingNeighborhood();
			Pair<NdPoint, GISNeighborhood> workplace = Heuristics.getSODBasedWorkplace(sod, livingNeighborhood,
					this.neighborhoods);
			citizen.setWorkplace(workplace.getFirst());
			citizen.setWorkingNeighborhood(workplace.getSecond());
		}
		// Schedule policies
		this.policyEnforcer = schedulePolicies(Paths.POLICIES_DATABASE);
		context.add(this.policyEnforcer);
		// Set end tick
		RunEnvironment.getInstance().endAt(END_TICK);
		return context;
	}

	/**
	 * Create geography projection
	 * 
	 * @param context Simulation context
	 */
	private Geography<Object> createGeographyProjection(Context<Object> context) {
		GeographyParameters<Object> params = new GeographyParameters<>();
		GeographyFactory geographyFactory = GeographyFactoryFinder.createGeographyFactory(null);
		Geography<Object> geography = geographyFactory.createGeography(GEOGRAPHY_PROJECTION_ID, context, params);
		return geography;
	}

	/**
	 * Read polygons
	 * 
	 * @param geometryPath Path to geometry file
	 * @param polygonType  Polygon type
	 * @param attribute    Attribute
	 */
	private HashMap<String, GISPolygon> readPolygons(String geometryPath, GISPolygonType polygonType,
			String attribute) {
		HashMap<String, GISPolygon> polygons = new HashMap<>();
		List<SimpleFeature> features = Reader.loadGeometryFromShapefile(geometryPath);
		for (SimpleFeature feature : features) {
			MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
			Geometry geometry = multiPolygon.getGeometryN(0);
			String id = "" + feature.getAttribute(attribute);
			GISPolygon polygon = null;
			switch (polygonType) {
			case NEIGHBORHOOD:
				polygon = new GISNeighborhood(id);
				break;
			default:
				polygon = new GISPolygon(id);
				break;
			}
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