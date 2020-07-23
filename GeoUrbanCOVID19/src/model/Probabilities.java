package model;

import cern.jet.random.Gamma;
import cern.jet.random.Normal;
import repast.simphony.random.RandomHelper;
import util.TickConverter;

public abstract class Probabilities {

	/**
	 * Age ranges (unit: age). Reference: <pending>
	 */
	public static final int AGE_RANGES[][] = { { 0, 9 }, { 10, 19 }, { 20, 29 }, { 30, 39 }, { 40, 49 }, { 50, 59 },
			{ 60, 69 }, { 70, 79 }, { 80, 121 } };

	/**
	 * Age probabilities (unit: probability). Reference: <pending>
	 */
	public static final double AGE_PROBABILITIES[] = { 0.1443, 0.169, 0.1728, 0.1487, 0.1221, 0.1104, 0.0728, 0.0393,
			0.0206 };

	/**
	 * Minimum wake up time (unit: hours). Reference: <pending>
	 */
	public static final double MIN_WAKE_UP_TIME = 4;

	/**
	 * Maximum wake up time (unit: hours). Reference: <pending>
	 */
	public static final double MAX_WAKE_UP_TIME = 10;

	/**
	 * Minimum returning home time (unit: hours). Reference: <pending>
	 */
	public static final double MIN_RETURN_HOME_TIME = 13;

	/**
	 * Maximum returning home time (unit: hours). Reference: <pending>
	 */
	public static final double MAX_RETURN_HOME_TIME = 22;

	/**
	 * Infection alpha parameter. Reference: <pending>
	 */
	public static final double INFECTION_ALPHA = 2.11;

	/**
	 * Infection beta parameter. Reference: <pending>
	 */
	public static final double INFECTION_BETA = 1.3;

	/**
	 * Infection minimum parameter. Reference: <pending>
	 */
	public static final double INFECTION_MIN = -2.4;

	/**
	 * Discharge alpha parameter. Reference: <pending>
	 */
	public static final double DISCHARGE_ALPHA = 1.99;

	/**
	 * Discharge beta parameter. Reference: <pending>
	 */
	public static final double DISCHARGE_BETA = 7.77;

	/**
	 * Incubation period mean parameter (unit: days). Reference: <pending>
	 */
	public static final double MEAN_INCUBATION_PERIOD = 5.52;

	/**
	 * Incubation period standard deviation parameter (unit: days). Reference:
	 * <pending>
	 */
	public static final double STD_INCUBATION_PERIOD = 2.41;

	/**
	 * Get random number based on a triangular distribution
	 * 
	 * @param min  Minimum
	 * @param mode Mode
	 * @param max  Maximum
	 */
	public static double getRandomTriangular(double min, double mode, double max) {
		double r = RandomHelper.nextDoubleFromTo(0, 1);
		double beta = (mode - min) / (max - min);
		double t = 0.0;
		if (r < beta) {
			t = Math.sqrt(beta * r);
		} else {
			t = Math.sqrt((1 - beta) * (1 - r));
		}
		return min + (max - min) * t;
	}

	/**
	 * Get random id. Reference: <pending>
	 */
	public static int getRandomId() {
		return RandomHelper.nextIntFromTo(0, 9);
	}

	/**
	 * Get random age (unit: age). Reference: <pending>
	 */
	public static int getRandomAge() {
		double r = RandomHelper.nextDoubleFromTo(0, 1);
		double cummulativeProbability = 0;
		for (int i = 0; i < AGE_PROBABILITIES.length; i++) {
			cummulativeProbability += AGE_PROBABILITIES[i];
			if (r < cummulativeProbability) {
				return RandomHelper.nextIntFromTo(AGE_RANGES[i][0], AGE_RANGES[i][1]);
			}
		}
		return -1;
	}

	/**
	 * Get random incubation period (unit: days). Reference: <pending>
	 */
	public static double getRandomIncubationPeriod() {
		double t = Math.pow(MEAN_INCUBATION_PERIOD, 2) + Math.pow(STD_INCUBATION_PERIOD, 2);
		double mu = Math.log(Math.pow(MEAN_INCUBATION_PERIOD, 2) / Math.sqrt(t));
		double sigma = Math.log(t / Math.pow(MEAN_INCUBATION_PERIOD, 2));
		Normal normalDistribution = RandomHelper.createNormal(mu, sigma);
		double y = normalDistribution.nextDouble();
		return Math.exp(y);
	}

	/**
	 * Get random patient type. Reference: <pending>
	 */
	public static PatientType getRandomPatientType() {
		double r1 = RandomHelper.nextDoubleFromTo(0, 1);
		if (r1 < 0.111) {
			return PatientType.NO_SYMPTOMS;
		} else {
			double r2 = RandomHelper.nextDoubleFromTo(0, 1);
			if (r2 < 0.814) {
				return PatientType.MODERATE_SYMPTOMS;
			} else if (r2 < 0.953) {
				return PatientType.SEVERE_SYMPTOMS;
			} else {
				return PatientType.CRITICAL_SYMPTOMS;
			}
		}
	}

	/**
	 * Is the patient going to die? Reference: <pending>
	 * 
	 * @param patientType Patient type
	 */
	public static boolean isGoingToDie(PatientType patientType) {
		double r = RandomHelper.nextDoubleFromTo(0, 1);
		switch (patientType) {
		case SEVERE_SYMPTOMS:
			return r < 0.15;
		case CRITICAL_SYMPTOMS:
			return r < 0.5;
		default:
			return false;
		}
	}

	/**
	 * Is the citizen getting exposed? Reference: <pending>
	 * 
	 * @param incubationDiff Incubation difference
	 */
	public static boolean isGettingExposed(double incubationDiff) {
		double r = RandomHelper.nextDoubleFromTo(0, 1);
		Gamma gamma = RandomHelper.createGamma(INFECTION_ALPHA, 1.0 / INFECTION_BETA);
		double days = TickConverter.ticksToDays(incubationDiff);
		if (days < INFECTION_MIN) {
			return false;
		}
		double p = gamma.pdf(days - INFECTION_MIN);
		return r < p;
	}

	/**
	 * Get random time to discharge (unit: days). Reference: <pending>
	 */
	public static double getRandomTimeToDischarge() {
		Gamma gamma = RandomHelper.createGamma(DISCHARGE_ALPHA, 1.0 / DISCHARGE_BETA);
		return gamma.nextDouble();
	}

	/**
	 * Get random wake up time (unit: hours). Reference: <pending>
	 */
	public static double getRandomWakeUpTime() {
		return RandomHelper.nextDoubleFromTo(MIN_WAKE_UP_TIME, MAX_WAKE_UP_TIME);
	}

	/**
	 * Get random returning home time (unit: hours). Reference: <pending>
	 */
	public static double getRandomReturningHomeTime() {
		return RandomHelper.nextDoubleFromTo(MIN_RETURN_HOME_TIME, MAX_RETURN_HOME_TIME);
	}

}