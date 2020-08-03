package policy;

import model.Citizen;

public class ElderlyQuarantinePolicy extends Policy {

	/**
	 * Create a new elderly quarantine policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public ElderlyQuarantinePolicy(int beginDay, int endDay) {
		super(beginDay, endDay);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	@Override
	protected boolean isAllowedToGoOut(Citizen citizen) {
		int age = citizen.getAge();
		return !(age >= 60);
	}

}
