package policy;

import model.Citizen;

public class SchoolClosuresPolicy extends Policy {

	/**
	 * Create a new school closures policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public SchoolClosuresPolicy(int beginDay, int endDay) {
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
		return !(age < 18);
	}

}
