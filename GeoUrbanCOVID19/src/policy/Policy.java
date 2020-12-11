package policy;

import model.Citizen;
import repast.simphony.essentials.RepastEssentials;
import util.TickConverter;

public abstract class Policy {

	/**
	 * Policy type
	 */
	private PolicyType type;

	/**
	 * Begin day (unit: days)
	 */
	private int beginDay;

	/**
	 * End day (unit: days)
	 */
	private int endDay;

	/**
	 * Minimum age allowed (unit: years)
	 */
	private int minimumAgeAllowed;

	/**
	 * Maximum age allowed (unit: years)
	 */
	private int maximumAgeAllowed;

	/**
	 * Effective departures share
	 */
	private double effectiveDeparturesShare;

	/**
	 * Is mask usage mandatory?
	 */
	private boolean isMaskMandatory;

	/**
	 * Curfew
	 */
	private int[][] curfew;

	/**
	 * Create a new policy
	 * 
	 * @param type                     PolicyType
	 * @param beginDay                 Begin day
	 * @param endDay                   End day
	 * @param minimumAgeAllowed        minimumAgeAllowed
	 * @param maximumAgeAllowed        Maximum age allowed
	 * @param effectiveDeparturesShare Effective departures share
	 * @param isMaskMandatory          Mandatory mask flag
	 */
	public Policy(PolicyType type, int beginDay, int endDay,
			int minimumAgeAllowed, int maximumAgeAllowed,
			double effectiveDeparturesShare, boolean isMaskMandatory) {
		this.type = type;
		this.beginDay = beginDay;
		this.endDay = endDay;
		this.minimumAgeAllowed = minimumAgeAllowed;
		this.maximumAgeAllowed = maximumAgeAllowed;
		this.effectiveDeparturesShare = effectiveDeparturesShare;
		this.isMaskMandatory = isMaskMandatory;
	}

	/**
	 * Is the citizen allowed to go out?
	 * 
	 * @param citizen Citizen
	 */
	public boolean isAllowedToGoOut(Citizen citizen) {
		// Check quarantine
		if (this.type == PolicyType.QUARANTINE) {
			return false;
		}
		// Check age
		if (this.type != PolicyType.NONE) {
			int age = citizen.getAge();
			if (age < this.minimumAgeAllowed || age > this.maximumAgeAllowed) {
				return false;
			}
		}
		// Check id-based curfew
		if (this.type == PolicyType.CURFEW) {
			int id = citizen.getId();
			double ticks = RepastEssentials.GetTickCount();
			int weekday = TickConverter.ticksToWeekday(ticks);
			int[] allowedIds = this.curfew[weekday];
			boolean match = false;
			for (int i = 0; i < allowedIds.length; i++) {
				if (id == allowedIds[i]) {
					match = true;
				}
			}
			if (!match) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set curfew
	 * 
	 * @param curfew Curfew
	 */
	public void setCurfew(int[][] curfew) {
		this.curfew = curfew;
	}

	/**
	 * Get effective departures share
	 */
	public double getEffectiveDeparturesShare() {
		return this.effectiveDeparturesShare;
	}

	/**
	 * Is mask usage mandatory?
	 */
	public boolean isMaskMandatory() {
		return this.isMaskMandatory;
	}

	/**
	 * Get policy type
	 */
	public PolicyType getPolicyType() {
		return this.type;
	}

	/**
	 * Get begin day
	 */
	public int getBeginDay() {
		return this.beginDay;
	}

	/**
	 * Get end day
	 */
	public int getEndDay() {
		return this.endDay;
	}

}