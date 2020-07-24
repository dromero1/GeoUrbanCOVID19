package policy;

import model.Citizen;

public abstract class Policy {

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
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public Policy(int beginDay, int endDay) {
		this.beginDay = beginDay;
		this.endDay = endDay;
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	protected abstract boolean isAllowedToGoOut(Citizen citizen);

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