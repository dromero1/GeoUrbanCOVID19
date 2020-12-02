package simulation;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class ParametersAdapter {

	/**
	 * Exposed count parameter id
	 */
	private static final String EXPOSED_COUNT_PARAM_ID = "exposedCount";

	/**
	 * Susceptible count parameter id
	 */
	private static final String SUSCEPTIBLE_COUNT_PARAM_ID = "susceptibleCount";

	/**
	 * Infection radius parameter id
	 */
	private static final String INFECTION_RADIUS_PARAM_ID = "infectionRadius";

	/**
	 * Particle expulsion interval parameter id
	 */
	private static final String PARTICLE_EXPULSION_INTERVAL_PARAM_ID = "particleExpulsionInterval";

	/**
	 * Ticks to sleep parameter id
	 */
	private static final String TICKS_TO_SLEEP_PARAM_ID = "ticksToSleep";

	/**
	 * Walker share parameter id
	 */
	private static final String WALKER_SHARE_PARAM_ID = "walkerShare";

	/**
	 * Effective departures share parameter id
	 */
	private static final String EFFECTIVE_DEPARTURES_SHARE_PARAM_ID = "effectiveDeparturesShare";

	/**
	 * Get exposed count
	 */
	public static int getExposedCount() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getInteger(EXPOSED_COUNT_PARAM_ID);
	}

	/**
	 * Get susceptible count
	 */
	public static int getSusceptibleCount() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getInteger(SUSCEPTIBLE_COUNT_PARAM_ID);
	}

	/**
	 * Get infection radius
	 */
	public static double getInfectionRadius() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(INFECTION_RADIUS_PARAM_ID);
	}

	/**
	 * Get particle expulsion interval
	 */
	public static double getParticleExpulsionInterval() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(PARTICLE_EXPULSION_INTERVAL_PARAM_ID);
	}

	/**
	 * Get ticks to sleep
	 */
	public static double getTicksToSleep() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(TICKS_TO_SLEEP_PARAM_ID);
	}

	/**
	 * Get walker share
	 */
	public static double getWalkerShare() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(WALKER_SHARE_PARAM_ID);
	}

	/**
	 * Get effective departures share
	 */
	public static double getEffectiveDeparturesShare() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(EFFECTIVE_DEPARTURES_SHARE_PARAM_ID);
	}

	/**
	 * Private constructor
	 */
	private ParametersAdapter() {
		throw new UnsupportedOperationException("Utility class");
	}

}