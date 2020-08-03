package policy;

import model.Citizen;

public class UniversityClosuresPolicy extends Policy {

	/**
	 * Create a new university closures policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public UniversityClosuresPolicy(int beginDay, int endDay) {
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
		return !(age >= 18 && age <= 24);
	}

}
