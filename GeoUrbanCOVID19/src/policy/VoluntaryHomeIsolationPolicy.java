package policy;

import java.util.List;
import model.Citizen;
import model.Compartment;

public class VoluntaryHomeIsolationPolicy extends Policy {

	/**
	 * Create a new voluntary home isolation policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public VoluntaryHomeIsolationPolicy(int beginDay, int endDay) {
		super(beginDay, endDay);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	@Override
	protected boolean isAllowedToGoOut(Citizen citizen) {
		List<Citizen> family = citizen.getFamily();
		for (Citizen member : family) {
			Compartment compartment = member.getCompartment();
			if (!member.equals(citizen) && compartment == Compartment.INFECTED) {
				return false;
			}
		}
		return true;
	}

}
