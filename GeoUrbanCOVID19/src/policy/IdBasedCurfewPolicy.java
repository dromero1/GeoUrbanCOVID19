package policy;

import model.Citizen;
import repast.simphony.essentials.RepastEssentials;
import util.TickConverter;

public class IdBasedCurfewPolicy extends Policy {

	/**
	 * Curfew IDs for each day. Reference: <pending>
	 */
	private int[][] curfewIds = { { 7, 8 }, { 9, 0 }, { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 }, { 9, 0, 1 } };

	/**
	 * Create a new id-based curfew policy
	 * 
	 * @param beginDay Begin day
	 * @param endDay   End day
	 */
	public IdBasedCurfewPolicy(int beginDay, int endDay) {
		super(beginDay, endDay);
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	@Override
	protected boolean isAllowedToGoOut(Citizen citizen) {
		int id = citizen.getId();
		double ticks = Math.max(RepastEssentials.GetTickCount(), 0);
		int day = (int) TickConverter.ticksToDays(ticks) % 7;
		int[] allowedIds = this.curfewIds[day];
		for (int i = 0; i < allowedIds.length; i++) {
			if (id == allowedIds[i]) {
				return true;
			}
		}
		return false;
	}

}
