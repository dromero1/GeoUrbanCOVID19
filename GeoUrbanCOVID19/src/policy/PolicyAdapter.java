package policy;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PolicyAdapter {

	/**
	 * Policy type parameter id
	 */
	private static final String POLICY_TYPE_PARAM_ID = "type";

	/**
	 * Start day parameter id
	 */
	private static final String START_DAY_PARAM_ID = "start";

	/**
	 * End day parameter id
	 */
	private static final String END_DAY_PARAM_ID = "end";

	/**
	 * Minimum age allowed parameter id
	 */
	private static final String MIN_AGE_PARAM_ID = "ageMin";

	/**
	 * Maximum age allowed parameter id
	 */
	private static final String MAX_AGE_PARAM_ID = "ageMax";

	/**
	 * Mandatory mask flag parameter id
	 */
	private static final String MANDATORY_MASK_FLAG_PARAM_ID = "mask";

	/**
	 * Effective departures share parameter id
	 */
	private static final String DEPARTURES_SHARE_PARAM_ID = "factor";

	/**
	 * Curfew parameter id
	 */
	private static final String CURFEW_PARAM_ID = "curfew";

	/**
	 * Private constructor
	 */
	private PolicyAdapter() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Adapt policy
	 * 
	 * @param rawPolicy Raw policy
	 */
	public static Policy adapt(JSONObject rawPolicy) {
		// Parse policy type
		int rawType = Math
				.toIntExact((long) rawPolicy.get(POLICY_TYPE_PARAM_ID));
		PolicyType type = parsePolicyType(rawType);
		// Parse start day
		int startDay = Math
				.toIntExact((long) rawPolicy.get(START_DAY_PARAM_ID));
		// Parse end day
		int endDay = Math.toIntExact((long) rawPolicy.get(END_DAY_PARAM_ID));
		// Parse minimum age allowed
		int minimumAgeAllowed = 0;
		if (rawPolicy.containsKey(MIN_AGE_PARAM_ID)) {
			minimumAgeAllowed = Math
					.toIntExact((long) rawPolicy.get(MIN_AGE_PARAM_ID));
		}
		// Parse maximum age allowed
		int maximumAgeAllowed = Integer.MAX_VALUE;
		if (rawPolicy.containsKey(MAX_AGE_PARAM_ID)) {
			maximumAgeAllowed = Math
					.toIntExact((long) rawPolicy.get(MAX_AGE_PARAM_ID));
		}
		// Parse effective departures share
		double effectiveDeparturesShare = (double) rawPolicy
				.get(DEPARTURES_SHARE_PARAM_ID);
		// Parse raw mask mandatory flag
		int rawMaskMandatoryFlag = Math
				.toIntExact((long) rawPolicy.get(MANDATORY_MASK_FLAG_PARAM_ID));
		boolean isMaskMandatory = parseMaskMandatoryFlag(rawMaskMandatoryFlag);
		// Build policy
		Policy policy = new Policy(type, startDay, endDay, minimumAgeAllowed,
				maximumAgeAllowed, effectiveDeparturesShare, isMaskMandatory);
		// Parse curfew
		if (rawPolicy.containsKey(CURFEW_PARAM_ID)) {
			JSONArray rawCurfew = (JSONArray) rawPolicy.get(CURFEW_PARAM_ID);
			List<List<Integer>> curfew = parseCurfew(rawCurfew);
			policy.setCurfew(curfew);
		}
		return policy;
	}

	/**
	 * Parse policy type
	 * 
	 * @param rawType Raw policy type
	 */
	public static PolicyType parsePolicyType(int rawType) {
		if (rawType == 1) {
			return PolicyType.QUARANTINE;
		} else if (rawType == 2) {
			return PolicyType.CURFEW;
		} else {
			return PolicyType.NONE;
		}
	}

	/**
	 * Parse mask mandatory flag
	 * 
	 * @param rawMaskMandatoryFlag Raw mask mandatory flag
	 */
	public static boolean parseMaskMandatoryFlag(int rawIsMaskMandatory) {
		return rawIsMaskMandatory == 1;
	}

	/**
	 * Parse curfew
	 * 
	 * @param rawCurfew Raw curfew
	 */
	public static List<List<Integer>> parseCurfew(JSONArray rawCurfew) {
		List<List<Integer>> curfew = new ArrayList<>();
		for (int i = 0; i < rawCurfew.size(); i++) {
			List<Integer> dayIds = new ArrayList<>();
			JSONArray rawIds = (JSONArray) rawCurfew.get(i);
			for (int j = 0; j < rawIds.size(); j++) {
				int id = Math.toIntExact((long) rawIds.get(j));
				dayIds.add(id);
			}
			curfew.add(dayIds);
		}
		return curfew;
	}

}