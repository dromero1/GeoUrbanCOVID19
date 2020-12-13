package simulation;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import config.SourcePaths;
import datasource.Reader;
import gis.GISCommune;
import gis.GISNeighborhood;
import gis.GISNeighborhoodDetail;
import gis.GISPolygon;
import gis.GISPolygonType;
import model.Citizen;
import model.Compartment;
import model.Heuristics;
import model.SODMatrix;
import output.OutputManager;
import output.OutputManagerObserver;
import policy.Policy;
import policy.PolicyEnforcer;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.util.collections.Pair;

public class SimulationBuilder
		implements ContextBuilder<Object>, OutputManagerObserver {

	/**
	 * End tick (unit: hours)
	 */
	public static final double END_TICK = 5808;

	/**
	 * Geography projection id
	 */
	public static final String GEOGRAPHY_PROJECTION_ID = "city";

	/**
	 * City polygon id attribute
	 */
	public static final String CITY_POLYGON_ID_ATTRIBUTE = "Id";

	/**
	 * Neighborhood polygon id attribute
	 */
	private static final String NEIGHBORHOOD_POLYGON_ID_ATTRIBUTE = "SIT_2017";

	/**
	 * Reference to geography projection
	 */
	public Geography<Object> geography;

	/**
	 * City
	 */
	public Map<String, GISPolygon> city;

	/**
	 * Communes
	 */
	public Map<String, GISCommune> communes;

	/**
	 * Neighborhoods
	 */
	public Map<String, GISPolygon> neighborhoods;

	/**
	 * Policy compliance
	 */
	public Map<Integer, Double> policyCompliance;

	/**
	 * Mask usage
	 */
	public Map<Integer, Double> maskUsage;

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
		this.city = readPolygons(SourcePaths.CITY_GEOMETRY_SHAPEFILE,
				GISPolygonType.SIMPLE, CITY_POLYGON_ID_ATTRIBUTE);
		for (GISPolygon cityElement : this.city.values()) {
			context.add(cityElement);
		}
		// Initialize communes
		this.communes = Reader
				.readCommunesDatabase(SourcePaths.COMMUNES_DATABASE);
		// Add communes to the simulation
		for (GISCommune commune : this.communes.values()) {
			context.add(commune);
		}
		// Initialize neighborhoods
		this.neighborhoods = readPolygons(
				SourcePaths.NEIGHBORHOODS_GEOMETRY_SHAPEFILE,
				GISPolygonType.NEIGHBORHOOD, NEIGHBORHOOD_POLYGON_ID_ATTRIBUTE);
		// Fill neighborhoods
		fillNeighborhoods();
		// Add neighborhoods to the simulation
		for (GISPolygon neighborhood : this.neighborhoods.values()) {
			context.add(neighborhood);
		}
		// Initialize output manager
		this.outputManager = new OutputManager();
		this.outputManager.registerObserver(this);
		context.add(this.outputManager);
		// Add students to the simulation
		List<Citizen> citizens = createCitizens();
		for (Citizen citizen : citizens) {
			context.add(citizen);
		}
		// Assign families
		List<Citizen> familyProxies = new ArrayList<>();
		for (Citizen citizen : citizens) {
			if (citizen.getFamily().isEmpty()) {
				Heuristics.assignFamily(citizen, citizens);
				familyProxies.add(citizen);
			}
		}
		// Assign a house to each family
		for (Citizen proxy : familyProxies) {
			Heuristics.assignHouse(proxy, communes.values());
		}
		// Initialize SOD matrix
		SODMatrix sod = Reader.readSODMatrix(SourcePaths.SOD_MATRIX);
		// Assign workplaces
		for (Citizen citizen : citizens) {
			GISPolygon livingNeighborhood = citizen.getLivingNeighborhood();
			Pair<NdPoint, GISNeighborhood> workplace = Heuristics
					.getSODBasedWorkplace(sod, livingNeighborhood,
							this.neighborhoods);
			citizen.setWorkplace(workplace.getFirst());
			citizen.setWorkingNeighborhood(workplace.getSecond());
		}
		// Initialize stratum-based policy compliance
		this.policyCompliance = Reader.readPolicyComplianceDatabase(
				SourcePaths.POLICY_COMPLIANCE_DATABASE);
		// Initialize stratum-based mask usage
		this.maskUsage = Reader
				.readMaskUsageDatabase(SourcePaths.MASK_USAGE_DATABASE);
		// Schedule policies
		this.policyEnforcer = new PolicyEnforcer();
		schedulePolicies(SourcePaths.POLICIES_DATABASE);
		context.add(this.policyEnforcer);
		// Set end tick
		RunEnvironment.getInstance().endAt(END_TICK);
		return context;
	}

	/**
	 * Handle the 'onZeroActiveCases' event
	 */
	@Override
	public void onZeroActiveCases() {
		stopSimulation();
	}

	/**
	 * Handle the 'onCumulativeCasesThresholdReached' event
	 */
	@Override
	public void onCumulativeCasesThresholdReached() {
		if (ParametersAdapter.isCalibrationModeOn()) {
			stopSimulation();
		}
	}

	/**
	 * Create geography projection
	 * 
	 * @param context Simulation context
	 */
	private Geography<Object> createGeographyProjection(
			Context<Object> context) {
		GeographyParameters<Object> params = new GeographyParameters<>();
		GeographyFactory geographyFactory = GeographyFactoryFinder
				.createGeographyFactory(null);
		return geographyFactory.createGeography(GEOGRAPHY_PROJECTION_ID,
				context, params);
	}

	/**
	 * Read polygons
	 * 
	 * @param geometryPath Path to geometry file
	 * @param polygonType  Polygon type
	 * @param attribute    Attribute
	 */
	private Map<String, GISPolygon> readPolygons(String geometryPath,
			GISPolygonType polygonType, String attribute) {
		Map<String, GISPolygon> polygons = new HashMap<>();
		List<SimpleFeature> features = Reader
				.loadGeometryFromShapefile(geometryPath);
		for (SimpleFeature feature : features) {
			MultiPolygon multiPolygon = (MultiPolygon) feature
					.getDefaultGeometry();
			Geometry geometry = multiPolygon.getGeometryN(0);
			String id = "" + feature.getAttribute(attribute);
			GISPolygon polygon = null;
			if (polygonType == GISPolygonType.NEIGHBORHOOD) {
				polygon = new GISNeighborhood(id);
			} else {
				polygon = new GISPolygon(id);
			}
			polygon.setGeometryInGeography(this.geography, geometry);
			polygons.put(id, polygon);
		}
		return polygons;
	}

	/**
	 * Fill neighborhoods
	 */
	private void fillNeighborhoods() {
		Map<String, GISNeighborhoodDetail> details = Reader
				.readNeighborhoodsDatabase(SourcePaths.NEIGHBORHOODS_DATABASE);
		for (Map.Entry<String, GISNeighborhoodDetail> detailEntry : details
				.entrySet()) {
			String neighborhoodId = detailEntry.getKey();
			GISNeighborhoodDetail detail = detailEntry.getValue();
			GISNeighborhood neighborhood = (GISNeighborhood) this.neighborhoods
					.get(neighborhoodId);
			if (neighborhood == null) {
				continue;
			}
			String communeId = detail.getCommuneId();
			GISCommune commune = this.communes.get(communeId);
			neighborhood.setCommune(commune);
			neighborhood.setDetail(detailEntry.getValue());
		}
	}

	/**
	 * Create citizens
	 */
	private List<Citizen> createCitizens() {
		int susceptibleCount = ParametersAdapter.getSusceptibleCount();
		int exposedCount = ParametersAdapter.getExposedCount();
		List<Citizen> citizens = new ArrayList<>();
		for (int i = 0; i < exposedCount; i++) {
			Citizen citizen = new Citizen(this, Compartment.EXPOSED);
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
	private void schedulePolicies(String policiesPath) {
		List<Policy> policies = Reader.readPoliciesDatabase(policiesPath);
		for (Policy policy : policies) {
			this.policyEnforcer.schedulePolicy(policy);
		}
	}

	/**
	 * Stop simulation
	 */
	private void stopSimulation() {
		RunEnvironment.getInstance().endRun();
	}

}