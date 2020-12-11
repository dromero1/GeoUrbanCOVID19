package policy;

import java.util.ArrayList;
import java.util.List;

import model.Citizen;
import repast.simphony.essentials.RepastEssentials;
import util.TickConverter;

public class Policy {

	/**
	 * Policy type
	 */
	private PolicyType type;

	/**
	 * Start day (unit: days)
	 */
	private int startDay;

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
	private List<List<Integer>> curfew;

	/**
	 * Create a new policy
	 * 
	 * @param type                     PolicyType
	 * @param startDay                 Start day
	 * @param endDay                   End day
	 * @param minimumAgeAllowed        minimumAgeAllowed
	 * @param maximumAgeAllowed        Maximum age allowed
	 * @param effectiveDeparturesShare Effective departures share
	 * @param isMaskMandatory          Mandatory mask flag
	 */
	public Policy(PolicyType type, int startDay, int endDay,
			int minimumAgeAllowed, int maximumAgeAllowed,
			double effectiveDeparturesShare, boolean isMaskMandatory) {
		this.type = type;
		this.startDay = startDay;
		this.endDay = endDay;
		this.minimumAgeAllowed = minimumAgeAllowed;
		this.maximumAgeAllowed = maximumAgeAllowed;
		this.effectiveDeparturesShare = effectiveDeparturesShare;
		this.isMaskMandatory = isMaskMandatory;
		this.curfew = new ArrayList<>();
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
			List<Integer> allowedIds = this.curfew.get(weekday);
			boolean match = false;
			for (int allowedId : allowedIds) {
				if (id == allowedId) {
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
	public void setCurfew(List<List<Integer>> curfew) {
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
	 * Get start day
	 */
	public int getStartDay() {
		return this.startDay;
	}

	/**
	 * Get end day
	 */
	public int getEndDay() {
		return this.endDay;
	}

}