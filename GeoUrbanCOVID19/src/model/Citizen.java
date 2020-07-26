package model;

import java.util.ArrayList;
import java.util.HashMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import gis.GISPolygon;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.gis.util.GeometryUtil;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.NdPoint;
import simulation.EventScheduler;
import simulation.SimulationBuilder;
import util.TickConverter;

public class Citizen {

	/**
	 * Displacement per step (unit: meters). Reference: <pending>
	 */
	public static final double DISPLACEMENT_PER_STEP = 50;

	/**
	 * Particle expulsion interval (unit: minutes)
	 */
	public static final double PARTICLE_EXPULSION_INTERVAL = 15;

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
	 * Time to incubation end (unit: hours)
	 */
	protected double incubationEnd;

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
	 * Family
	 */
	private ArrayList<Citizen> family;

	/**
	 * Living neighborhood
	 */
	private GISPolygon livingNeighborhood;

	/**
	 * Reference to simulation builder
	 */
	protected SimulationBuilder simulationBuilder;

	/**
	 * Scheduled actions
	 */
	private HashMap<SchedulableAction, ISchedulableAction> scheduledActions;

	/**
	 * Create a new citizen agent
	 * 
	 * @param simulationBuilder Reference to the simulation builder
	 * @param compartment       Compartment
	 */
	public Citizen(SimulationBuilder simulationBuilder, Compartment compartment) {
		this.simulationBuilder = simulationBuilder;
		this.compartment = compartment;
		this.id = Randomizer.getRandomId();
		this.age = Randomizer.getRandomAge();
		this.atHome = true;
		this.wakeUpTime = Randomizer.getRandomWakeUpTime();
		this.returningHomeTime = Randomizer.getRandomReturningHomeTime();
		this.family = new ArrayList<>();
		this.scheduledActions = new HashMap<>();
	}

	/**
	 * Initialize
	 */
	@ScheduledMethod(start = 0)
	public void init() {
		relocate(this.homeplace);
		initDisease();
		scheduleRecurringEvents();
	}

	/**
	 * Step
	 */
	public void step() {
		if (this.simulationBuilder.policyEnforcer.isAllowedToGoOut(this)) {
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
		if (this.simulationBuilder.policyEnforcer.isAllowedToGoOut(this)) {
			this.atHome = false;
			relocate(this.workplace);
		}
	}

	/**
	 * Return to homeplace
	 */
	public void returnHome() {
		if (!this.atHome) {
			this.atHome = true;
			relocate(this.homeplace);
		}
	}

	/**
	 * Transition to the exposed compartment
	 */
	public void transitionToExposed() {
		this.compartment = Compartment.EXPOSED;
		double incubationPeriod = Randomizer.getRandomIncubationPeriod();
		double infectiousPeriod = Math.max(incubationPeriod + Randomizer.INFECTION_MIN, 1);
		this.incubationEnd = RepastEssentials.GetTickCount() + TickConverter.daysToTicks(incubationPeriod);
		double ticks = TickConverter.daysToTicks(infectiousPeriod);
		EventScheduler eventScheduler = EventScheduler.getInstance();
		eventScheduler.scheduleOneTimeEvent(ticks, this, "transitionToInfected");
	}

	/**
	 * Transition to the infected compartment
	 */
	public void transitionToInfected() {
		this.compartment = Compartment.INFECTED;
		PatientType patientType = Randomizer.getRandomPatientType();
		// Schedule regular expulsion
		EventScheduler eventScheduler = EventScheduler.getInstance();
		double expelInterval = TickConverter.minutesToTicks(PARTICLE_EXPULSION_INTERVAL);
		ISchedulableAction expelAction = eventScheduler.scheduleRecurringEvent(1, this, expelInterval,
				"expelParticles");
		this.scheduledActions.put(SchedulableAction.EXPEL_PARTICLES, expelAction);
		// Schedule removal
		boolean isDying = Randomizer.isGoingToDie(patientType);
		String removalMethod = (isDying) ? "die" : "transitionToImmune";
		double timeToDischarge = Randomizer.getRandomTimeToDischarge();
		double ticksToRemoval = TickConverter.daysToTicks(timeToDischarge - Randomizer.INFECTION_MIN);
		eventScheduler.scheduleOneTimeEvent(ticksToRemoval, this, removalMethod);
	}

	/**
	 * Transition to the immune compartment
	 */
	public void transitionToImmune() {
		this.compartment = Compartment.IMMUNE;
		this.simulationBuilder.outputManager.onNewImmune();
		unscheduleAction(SchedulableAction.EXPEL_PARTICLES);
	}

	/**
	 * Transition to the dead compartment
	 */
	public void die() {
		this.compartment = Compartment.DEAD;
		this.simulationBuilder.outputManager.onNewDeath();
		unscheduleAction(SchedulableAction.STEP);
		unscheduleAction(SchedulableAction.WAKE_UP);
		unscheduleAction(SchedulableAction.RETURN_HOME);
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
	public ArrayList<Citizen> getFamily() {
		return this.family;
	}

	/**
	 * Set family
	 * 
	 * @param family List of family members
	 */
	public void setFamily(ArrayList<Citizen> family) {
		this.family = family;
	}

	/**
	 * Get living neighborhood
	 */
	public GISPolygon getLivingNeighborhood() {
		return this.livingNeighborhood;
	}

	/**
	 * Set living neighborhood
	 * 
	 * @param neighborhood Neighborhood
	 */
	public void setLivingNeighborhood(GISPolygon neighborhood) {
		this.livingNeighborhood = neighborhood;
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
		return (this.compartment == Compartment.EXPOSED || this.compartment == Compartment.INFECTED) ? 1 : 0;
	}

	/**
	 * Initialize disease
	 */
	private void initDisease() {
		switch (this.compartment) {
		case EXPOSED:
			transitionToExposed();
			break;
		case INFECTED:
			this.incubationEnd = -TickConverter.daysToTicks(Randomizer.INFECTION_MIN);
			transitionToInfected();
			break;
		default:
			break;
		}
	}

	/**
	 * Schedule recurring events
	 */
	private void scheduleRecurringEvents() {
		EventScheduler eventScheduler = EventScheduler.getInstance();
		ISchedulableAction stepAction = eventScheduler.scheduleRecurringEvent(1, this, 1, "step");
		ISchedulableAction wakeUpAction = eventScheduler.scheduleRecurringEvent(this.wakeUpTime, this,
				TickConverter.TICKS_PER_DAY, "wakeUp");
		ISchedulableAction returnHomeAction = eventScheduler.scheduleRecurringEvent(this.returningHomeTime, this,
				TickConverter.TICKS_PER_DAY, "returnHome");
		this.scheduledActions.put(SchedulableAction.STEP, stepAction);
		this.scheduledActions.put(SchedulableAction.WAKE_UP, wakeUpAction);
		this.scheduledActions.put(SchedulableAction.RETURN_HOME, returnHomeAction);
	}

	/**
	 * Walk randomly
	 */
	private void randomWalk() {
		double angle = RandomHelper.nextDoubleFromTo(0, 2 * Math.PI);
		Geometry location = this.simulationBuilder.geography.moveByVector(this, DISPLACEMENT_PER_STEP, angle);
	}

	/**
	 * Infect nearby susceptible individuals
	 */
	private void infect() {
		Parameters simParams = RunEnvironment.getInstance().getParameters();
		double distance = simParams.getDouble("infectionRadius");
		Geometry searchArea = GeometryUtil.generateBuffer(this.simulationBuilder.geography,
				this.simulationBuilder.geography.getGeometry(this), distance);
		Envelope searchEnvelope = searchArea.getEnvelopeInternal();
		Iterable<Citizen> citizens = this.simulationBuilder.geography.getObjectsWithin(searchEnvelope, Citizen.class);
		double incubationDiff = RepastEssentials.GetTickCount() - this.incubationEnd;
		for (Citizen citizen : citizens) {
			if (citizen.compartment == Compartment.SUSCEPTIBLE && Randomizer.isGettingExposed(incubationDiff)) {
				citizen.transitionToExposed();
				this.simulationBuilder.outputManager.onNewCase();
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
		Coordinate coordinate = new Coordinate(destination.getX(), destination.getY());
		Point point = geometryFactory.createPoint(coordinate);
		this.simulationBuilder.geography.move(this, point);
	}

	/**
	 * Unschedule action
	 * 
	 * @param schedulableAction Action to unschedule
	 */
	private void unscheduleAction(SchedulableAction schedulableAction) {
		ISchedulableAction action = this.scheduledActions.get(schedulableAction);
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.removeAction(action);
		this.scheduledActions.remove(schedulableAction);
	}

}