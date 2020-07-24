package policy;

import java.util.ArrayList;
import model.Citizen;
import simulation.EventScheduler;
import util.TickConverter;

public class PolicyEnforcer {

	/**
	 * Current policies
	 */
	private ArrayList<Policy> currentPolicies;

	/**
	 * Create a new policy enforcer
	 */
	public PolicyEnforcer() {
		this.currentPolicies = new ArrayList<>();
	}

	/**
	 * Schedule policy
	 * 
	 * @param policy Policy
	 */
	public void schedulePolicy(Policy policy) {
		EventScheduler eventScheduler = EventScheduler.getInstance();
		double startTick = TickConverter.daysToTicks(policy.getBeginDay());
		eventScheduler.scheduleOneTimeEvent(startTick, this, "addPolicy", policy);
		double endTick = TickConverter.daysToTicks(policy.getEndDay());
		eventScheduler.scheduleOneTimeEvent(endTick, this, "removePolicy", policy);
	}

	/**
	 * Add policy
	 * 
	 * @param policy Policy
	 */
	public void addPolicy(Policy policy) {
		this.currentPolicies.add(policy);
	}

	/**
	 * Remove policy
	 * 
	 * @param policy Policy
	 */
	public void removePolicy(Policy policy) {
		this.currentPolicies.remove(policy);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	public boolean isAllowedToGoOut(Citizen citizen) {
		for (Policy policy : this.currentPolicies) {
			if (!policy.isAllowedToGoOut(citizen)) {
				return false;
			}
		}
		return true;
	}

}