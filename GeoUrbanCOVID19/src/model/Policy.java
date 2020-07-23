package model;

public class Policy {

	/**
	 * Policy type
	 */
	private PolicyType policyType;

	/**
	 * Begin day (unit: days)
	 */
	private int beginDay;

	/**
	 * End day (unit: days)
	 */
	private int endDay;

	/**
	 * Create a new policy
	 * 
	 * @param policyType Policy type
	 * @param beginDay   Begin day
	 * @param endDay     End day
	 */
	public Policy(PolicyType policyType, int beginDay, int endDay) {
		this.policyType = policyType;
		this.beginDay = beginDay;
		this.endDay = endDay;
	}

	/**
	 * Get policy type
	 */
	public PolicyType getPolicyType() {
		return policyType;
	}

	/**
	 * Get begin day
	 */
	public int getBeginDay() {
		return beginDay;
	}

	/**
	 * Get end day
	 */
	public int getEndDay() {
		return endDay;
	}

}