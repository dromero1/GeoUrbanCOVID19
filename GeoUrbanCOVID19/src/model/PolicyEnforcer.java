package model;

import java.util.ArrayList;
import repast.simphony.essentials.RepastEssentials;
import simulation.EventScheduler;
import util.TickConverter;

public class PolicyEnforcer {

	/**
	 * Current policies
	 */
	private ArrayList<PolicyType> currentPolicies;

	/**
	 * Curfew IDs for each day. Reference: <pending>
	 */
	private int[][] curfewIds = { { 7, 8 }, { 9, 0 }, { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 }, { 9, 0, 1 } };

	/**
	 * Create a new policy enforcer
	 */
	public PolicyEnforcer() {
		this.currentPolicies = new ArrayList<PolicyType>();
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
	public void addPolicy(PolicyType policy) {
		this.currentPolicies.add(policy);
	}

	/**
	 * Remove policy
	 * 
	 * @param policy Policy
	 */
	public void removePolicy(PolicyType policy) {
		this.currentPolicies.remove(policy);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	public boolean isAllowedToGoOut(Citizen citizen) {
		boolean allowed = true;
		if (this.currentPolicies.contains(PolicyType.FULL_QUARANTINE)) {
			allowed = false;
		}
		if (this.currentPolicies.contains(PolicyType.ID_BASED_CURFEW)) {
			int id = citizen.getId();
			double ticks = Math.max(RepastEssentials.GetTickCount(), 0);
			int day = (int) TickConverter.ticksToDays(ticks) % 7;
			int[] allowedIds = this.curfewIds[day];
			allowed = false;
			for (int i = 0; i < allowedIds.length; i++) {
				if (id == allowedIds[i]) {
					allowed = true;
					break;
				}
			}
		}
		return allowed;
	}

}