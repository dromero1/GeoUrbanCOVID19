package policy;

public class PolicyFactory {

	/**
	 * Instantiate a new policy
	 * 
	 * @param policyType Policy type
	 * @param beginDay   Begin day
	 * @param endDay     End day
	 */
	public static Policy makePolicy(String policyType, int beginDay, int endDay) {
		switch (policyType) {
		case "full-quarantine":
			return new FullQuarantinePolicy(beginDay, endDay);
		case "id-based-curfew":
			return new IdBasedCurfewPolicy(beginDay, endDay);
		default:
			break;
		}
		return null;
	}

}
