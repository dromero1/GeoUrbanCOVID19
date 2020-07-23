package util;

import model.PolicyType;

public class PolicyUtil {

	/**
	 * Convert policy type to inner format
	 * 
	 * @param policyType Policy type
	 */
	public static PolicyType convertToInnerFormat(String policyType) {
		switch (policyType) {
		case "full-quarantine":
			return PolicyType.FULL_QUARANTINE;
		case "id-based-curfew":
			return PolicyType.ID_BASED_CURFEW;
		default:
			break;
		}
		return null;
	}

}