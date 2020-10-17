package policy;

public class PolicyFactory {

	/**
	 * Private constructor
	 */
	private PolicyFactory() {
		throw new UnsupportedOperationException("Utility class");
	}

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
		case "elderly-quarantine":
			return new ElderlyQuarantinePolicy(beginDay, endDay);
		case "school-closures":
			return new SchoolClosuresPolicy(beginDay, endDay);
		case "university-closures":
			return new UniversityClosuresPolicy(beginDay, endDay);
		case "case-isolation":
			return new CaseIsolationPolicy(beginDay, endDay);
		case "voluntary-home-isolation":
			return new VoluntaryHomeIsolationPolicy(beginDay, endDay);
		default:
			break;
		}
		return null;
	}

}
