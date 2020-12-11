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
	 * Mask factor parameter id
	 */
	private static final String MASK_FACTOR_PARAM_ID = "maskFactor";

	/**
	 * Compliance deviation percentage parameter id
	 */
	private static final String COMPLIANCE_DEVIATION_PERCENTAGE_PARAM_ID = "complianceDeviationPercentage";

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
	 * Get mask factor
	 */
	public static double getMaskFactor() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(MASK_FACTOR_PARAM_ID);
	}

	/**
	 * Get compliance deviation percentage
	 */
	public static double getComplianceDeviationPercentage() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		return simParams.getDouble(COMPLIANCE_DEVIATION_PERCENTAGE_PARAM_ID);
	}

	/**
	 * Private constructor
	 */
	private ParametersAdapter() {
		throw new UnsupportedOperationException("Utility class");
	}

}