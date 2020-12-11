package model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import gis.GISNeighborhood;
import gis.GISPolygon;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.gis.util.GeometryUtil;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.NdPoint;
import simulation.EventScheduler;
import simulation.ParametersAdapter;
import simulation.SimulationBuilder;
import util.TickConverter;

public class Citizen {

	/**
	 * Displacement per step (unit: meters). Reference: <pending>
	 */
	public static final double DISPLACEMENT_PER_STEP = 50;

	/**
	 * Citizen id
	 */
	private int id;

	/**
	 * Age
	 */
	private int age;

	/**
	 * Compartment
	 */
	private Compartment compartment;

	/**
	 * Homeplace
	 */
	private NdPoint homeplace;

	/**
	 * Workplace
	 */
	private NdPoint workplace;

	/**
	 * Wake up time
	 */
	private double wakeUpTime;

	/**
	 * Returning home time
	 */
	private double returningHomeTime;

	/**
	 * Currently at home
	 */
	private boolean atHome;

	/**
	 * Time to incubation end (unit: hours)
	 */
	private double incubationEnd;

	/**
	 * Mask usage probability (unit: probability)
	 */
	private double maskUsageProbability;

	/**
	 * Policy compliance probability (unit: probability)
	 */
	private double policyComplianceProbability;

	/**
	 * Mask usage. Whether the citizen wears a mask or not.
	 */
	private boolean usesMask;

	/**
	 * Policy compliance. Whether the citizen complies with external policies or
	 * not.
	 */
	private boolean compliesWithPolicies;

	/**
	 * Approval to go out. Whether the citizen is allowed to go out or not.
	 */
	private boolean allowedToGoOut;

	/**
	 * Desire to go out. Whether the citizen wants to go out or not.
	 */
	private boolean wantsToGoOut;

	/**
	 * Walker flag. Whether the citizen is a walker or not.
	 */
	private boolean walks;

	/**
	 * Asleep flag
	 */
	private boolean asleep;

	/**
	 * Family
	 */
	private List<Citizen> family;

	/**
	 * Living neighborhood
	 */
	private GISNeighborhood livingNeighborhood;

	/**
	 * Working neighborhood
	 */
	private GISNeighborhood workingNeighborhood;

	/**
	 * Current neighborhood
	 */
	private GISNeighborhood currentNeighborhood;

	/**
	 * Stratum
	 */
	private int stratum;

	/**
	 * Reference to simulation builder
	 */
	private SimulationBuilder simulationBuilder;

	/**
	 * Scheduled actions
	 */
	private Map<SchedulableAction, ISchedulableAction> scheduledActions;

	/**
	 * Create a new citizen agent
	 * 
	 * @param simulationBuilder Reference to the simulation builder
	 * @param compartment       Compartment
	 */
	public Citizen(SimulationBuilder simulationBuilder,
			Compartment compartment) {
		this.simulationBuilder = simulationBuilder;
		this.compartment = compartment;
		this.id = Randomizer.getRandomId();
		this.age = Randomizer.getRandomAge();
		this.atHome = true;
		this.wakeUpTime = Randomizer.getRandomWakeUpTime();
		this.returningHomeTime = Randomizer.getRandomReturningHomeTime();
		this.walks = Randomizer.getRandomWalkingPattern();
		this.family = new ArrayList<>();
		this.scheduledActions = new EnumMap<>(SchedulableAction.class);
	}

	/**
	 * Initialize
	 */
	@ScheduledMethod(start = 0)
	public void init() {
		this.currentNeighborhood = this.livingNeighborhood;
		this.stratum = Randomizer.getRandomStratum(this.currentNeighborhood);
		this.policyComplianceProbability = this.simulationBuilder.policyCompliance
				.get(this.stratum);
		this.maskUsageProbability = this.simulationBuilder.maskUsage
				.get(this.stratum);
		relocate(this.homeplace);
		initDisease();
		scheduleRecurringEvents();
	}

	/**
	 * Step
	 */
	public void step() {
		if (this.walks && !this.asleep && this.wantsToGoOut
				&& (this.allowedToGoOut || !this.compliesWithPolicies)) {
			randomWalk();
		}
	}

	/**
	 * Expel particles
	 */
	public void expelParticles() {
		infect();
	}

	/**
	 * Wake up and go to workplace
	 */
	public void wakeUp() {
		this.asleep = false;
		// Set mask usage
		boolean isMaskMandatory = this.simulationBuilder.policyEnforcer
				.isMaskMandatory();
		this.usesMask = false;
		if (isMaskMandatory) {
			this.usesMask = Randomizer
					.getRandomMaskUsage(this.maskUsageProbability);
		}
		// Set policy compliance
		this.compliesWithPolicies = Randomizer
				.getRandomPolicyCompliance(this.policyComplianceProbability);
		// Set desire to go out
		double effectiveDeparturesShare = this.simulationBuilder.policyEnforcer
				.getEffectiveDeparturesShare();
		this.wantsToGoOut = Randomizer
				.getRandomDesireToGoOut(effectiveDeparturesShare);
		// Set isolation restrictions
		this.allowedToGoOut = this.simulationBuilder.policyEnforcer
				.isAllowedToGoOut(this);
		// Determine whether to go out or not
		if (this.wantsToGoOut
				&& (this.allowedToGoOut || !this.compliesWithPolicies)) {
			this.atHome = false;
			this.currentNeighborhood = this.workingNeighborhood;
			relocate(this.workplace);
		}
	}

	/**
	 * Sleep
	 */
	public void sleep() {
		this.asleep = true;
	}

	/**
	 * Return to homeplace
	 */
	public void returnHome() {
		if (!this.atHome) {
			this.atHome = true;
			this.currentNeighborhood = this.livingNeighborhood;
			relocate(this.homeplace);
		}
	}

	/**
	 * Transition to the exposed compartment
	 */
	public void transitionToExposed() {
		this.compartment = Compartment.EXPOSED;
		double incubationPeriod = Randomizer.getRandomIncubationPeriod();
		double infectiousPeriod = Math
				.max(incubationPeriod + Randomizer.INFECTION_MIN, 1);
		this.incubationEnd = RepastEssentials.GetTickCount()
				+ TickConverter.daysToTicks(incubationPeriod);
		double ticks = TickConverter.daysToTicks(infectiousPeriod);
		EventScheduler eventScheduler = EventScheduler.getInstance();
		eventScheduler.scheduleOneTimeEvent(ticks, this,
				"transitionToInfected");
	}

	/**
	 * Transition to the infected compartment
	 */
	public void transitionToInfected() {
		this.compartment = Compartment.INFECTED;
		PatientType patientType = Randomizer.getRandomPatientType();
		// Schedule regular particle expulsion
		EventScheduler eventScheduler = EventScheduler.getInstance();
		double expulsionInterval = ParametersAdapter
				.getParticleExpulsionInterval();
		double expelInterval = TickConverter.minutesToTicks(expulsionInterval);
		ISchedulableAction expelAction = eventScheduler.scheduleRecurringEvent(
				1, this, expelInterval, "expelParticles");
		this.scheduledActions.put(SchedulableAction.EXPEL_PARTICLES,
				expelAction);
		// Schedule removal
		boolean isDying = Randomizer.isGoingToDie(patientType);
		String removalMethod = (isDying) ? "die" : "transitionToImmune";
		double timeToDischarge = Randomizer.getRandomTimeToDischarge();
		double ticksToRemoval = TickConverter
				.daysToTicks(timeToDischarge - Randomizer.INFECTION_MIN);
		eventScheduler.scheduleOneTimeEvent(ticksToRemoval, this,
				removalMethod);
	}

	/**
	 * Transition to the immune compartment
	 */
	public void transitionToImmune() {
		this.compartment = Compartment.IMMUNE;
		this.simulationBuilder.outputManager.onNewImmune();
		this.currentNeighborhood.onNewImmune();
		unscheduleAction(SchedulableAction.STEP);
		unscheduleAction(SchedulableAction.WAKE_UP);
		unscheduleAction(SchedulableAction.RETURN_HOME);
		unscheduleAction(SchedulableAction.SLEEP);
		unscheduleAction(SchedulableAction.EXPEL_PARTICLES);
	}

	/**
	 * Transition to the dead compartment
	 */
	public void die() {
		this.compartment = Compartment.DEAD;
		this.simulationBuilder.outputManager.onNewDeath();
		this.currentNeighborhood.onNewDeath();
		unscheduleAction(SchedulableAction.STEP);
		unscheduleAction(SchedulableAction.WAKE_UP);
		unscheduleAction(SchedulableAction.RETURN_HOME);
		unscheduleAction(SchedulableAction.SLEEP);
		unscheduleAction(SchedulableAction.EXPEL_PARTICLES);
	}

	/**
	 * Get citizen id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Get age
	 */
	public int getAge() {
		return this.age;
	}

	/**
	 * Get compartment
	 */
	public Compartment getCompartment() {
		return this.compartment;
	}

	/**
	 * Get homeplace
	 */
	public NdPoint getHomeplace() {
		return this.homeplace;
	}

	/**
	 * Set homeplace
	 * 
	 * @param location Homeplace location
	 */
	public void setHomeplace(NdPoint location) {
		this.homeplace = location;
	}

	/**
	 * Get workplace
	 */
	public NdPoint getWorkplace() {
		return this.workplace;
	}

	/**
	 * Set workplace
	 * 
	 * @param location Workplace location
	 */
	public void setWorkplace(NdPoint location) {
		this.workplace = location;
	}

	/**
	 * Get family
	 */
	public List<Citizen> getFamily() {
		return this.family;
	}

	/**
	 * Set family
	 * 
	 * @param family List of family members
	 */
	public void setFamily(List<Citizen> family) {
		this.family = family;
	}

	/**
	 * Get living neighborhood
	 */
	public GISNeighborhood getLivingNeighborhood() {
		return this.livingNeighborhood;
	}

	/**
	 * Set living neighborhood
	 * 
	 * @param neighborhood Neighborhood
	 */
	public void setLivingNeighborhood(GISNeighborhood neighborhood) {
		this.livingNeighborhood = neighborhood;
	}

	/**
	 * Get working neighborhood
	 */
	public GISPolygon getWorkingNeighborhood() {
		return this.workingNeighborhood;
	}

	/**
	 * Set working neighborhood
	 * 
	 * @param neighborhood Neighborhood
	 */
	public void setWorkingNeighborhood(GISNeighborhood neighborhood) {
		this.workingNeighborhood = neighborhood;
	}

	/**
	 * Get stratum
	 */
	public int getStratum() {
		return this.stratum;
	}

	/**
	 * Is susceptible?
	 */
	public int isSusceptible() {
		return this.compartment == Compartment.SUSCEPTIBLE ? 1 : 0;
	}

	/**
	 * Is exposed?
	 */
	public int isExposed() {
		return this.compartment == Compartment.EXPOSED ? 1 : 0;
	}

	/**
	 * Is infected?
	 */
	public int isInfected() {
		return this.compartment == Compartment.INFECTED ? 1 : 0;
	}

	/**
	 * Is immune?
	 */
	public int isImmune() {
		return this.compartment == Compartment.IMMUNE ? 1 : 0;
	}

	/**
	 * Is dead?
	 */
	public int isDead() {
		return this.compartment == Compartment.DEAD ? 1 : 0;
	}

	/**
	 * Is active case?
	 */
	public int isActiveCase() {
		return (this.compartment == Compartment.EXPOSED
				|| this.compartment == Compartment.INFECTED) ? 1 : 0;
	}

	/**
	 * Initialize disease
	 */
	private void initDisease() {
		if (this.compartment == Compartment.EXPOSED) {
			transitionToExposed();
		}
	}

	/**
	 * Schedule recurring events
	 */
	private void scheduleRecurringEvents() {
		EventScheduler eventScheduler = EventScheduler.getInstance();
		ISchedulableAction stepAction = eventScheduler.scheduleRecurringEvent(1,
				this, 1, "step");
		ISchedulableAction wakeUpAction = eventScheduler.scheduleRecurringEvent(
				this.wakeUpTime, this, TickConverter.TICKS_PER_DAY, "wakeUp");
		ISchedulableAction returnHomeAction = eventScheduler
				.scheduleRecurringEvent(this.returningHomeTime, this,
						TickConverter.TICKS_PER_DAY, "returnHome");
		double ticksToSleep = ParametersAdapter.getTicksToSleep();
		double bedtime = this.returningHomeTime + ticksToSleep;
		ISchedulableAction bedtimeAction = eventScheduler
				.scheduleRecurringEvent(bedtime, this,
						TickConverter.TICKS_PER_DAY, "sleep");
		this.scheduledActions.put(SchedulableAction.STEP, stepAction);
		this.scheduledActions.put(SchedulableAction.WAKE_UP, wakeUpAction);
		this.scheduledActions.put(SchedulableAction.RETURN_HOME,
				returnHomeAction);
		this.scheduledActions.put(SchedulableAction.SLEEP, bedtimeAction);
	}

	/**
	 * Walk randomly
	 */
	private void randomWalk() {
		double angle = RandomHelper.nextDoubleFromTo(0, 2 * Math.PI);
		this.simulationBuilder.geography.moveByVector(this,
				DISPLACEMENT_PER_STEP, angle);
	}

	/**
	 * Infect nearby susceptible individuals
	 */
	private void infect() {
		double distance = ParametersAdapter.getInfectionRadius();
		Geometry searchArea = GeometryUtil.generateBuffer(
				this.simulationBuilder.geography,
				this.simulationBuilder.geography.getGeometry(this), distance);
		Envelope searchEnvelope = searchArea.getEnvelopeInternal();
		Iterable<Citizen> citizens = this.simulationBuilder.geography
				.getObjectsWithin(searchEnvelope, Citizen.class);
		double incubationDiff = RepastEssentials.GetTickCount()
				- this.incubationEnd;
		for (Citizen citizen : citizens) {
			if (citizen.compartment == Compartment.SUSCEPTIBLE
					&& Randomizer.isGettingExposed(incubationDiff,
							this.usesMask, citizen.usesMask)) {
				citizen.transitionToExposed();
				this.simulationBuilder.outputManager.onNewCase();
				this.currentNeighborhood.onNewCase();
			}
		}
	}

	/**
	 * Relocate to destination
	 * 
	 * @param destination Destination
	 */
	private void relocate(NdPoint destination) {
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(destination.getX(),
				destination.getY());
		Point point = geometryFactory.createPoint(coordinate);
		this.simulationBuilder.geography.move(this, point);
	}

	/**
	 * Unschedule action
	 * 
	 * @param schedulableAction Action to unscheduled
	 */
	private void unscheduleAction(SchedulableAction schedulableAction) {
		ISchedulableAction action = this.scheduledActions
				.get(schedulableAction);
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.removeAction(action);
		this.scheduledActions.remove(schedulableAction);
	}

}