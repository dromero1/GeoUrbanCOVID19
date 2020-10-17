package policy;

import model.Citizen;
import model.Compartment;

public class CaseIsolationPolicy extends Policy {

	/**
	 * Create a new case isolation policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public CaseIsolationPolicy(int beginDay, int endDay) {
		super(beginDay, endDay);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	@Override
	protected boolean isAllowedToGoOut(Citizen citizen) {
		Compartment compartment = citizen.getCompartment();
		return compartment != Compartment.INFECTED;
	}

}
