package policy;

import model.Citizen;

public class FullQuarantinePolicy extends Policy {

	/**
	 * Create a new full quarantine policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public FullQuarantinePolicy(int beginDay, int endDay) {
		super(beginDay, endDay);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	@Override
	protected boolean isAllowedToGoOut(Citizen citizen) {
		return false;
	}

}