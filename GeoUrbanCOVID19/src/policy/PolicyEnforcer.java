package policy;

import model.Citizen;
import simulation.EventScheduler;
import util.TickConverter;

public class PolicyEnforcer {

	/**
	 * Current policy
	 */
	private Policy currentPolicy;

	/**
	 * Schedule policy
	 * 
	 * @param policy Policy
	 */
	public void schedulePolicy(Policy policy) {
		EventScheduler eventScheduler = EventScheduler.getInstance();
		int startDay = policy.getStartDay();
		double startTick = TickConverter.daysToTicks(startDay);
		eventScheduler.scheduleOneTimeEvent(startTick, this, "setPolicy",
				policy);
	}

	/**
	 * Set policy
	 * 
	 * @param policy Policy
	 */
	public void setPolicy(Policy policy) {
		this.currentPolicy = policy;
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	public boolean isAllowedToGoOut(Citizen citizen) {
		return this.currentPolicy.isAllowedToGoOut(citizen);
	}

	/**
	 * Is mask usage mandatory?
	 */
	public boolean isMaskMandatory() {
		return this.currentPolicy.isMaskMandatory();
	}

	/**
	 * Get effective departures share
	 */
	public double getEffectiveDeparturesShare() {
		return this.currentPolicy.getEffectiveDeparturesShare();
	}

}