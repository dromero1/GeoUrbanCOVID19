package gis;

import java.util.ArrayList;
import java.util.List;

public class GISCommune {

	/**
	 * Commune id
	 */
	private String id;

	/**
	 * Population (unit: people)
	 */
	private int population;

	/**
	 * Stratum shares (unit: percentage)
	 */
	private double[] stratumShares;

	/**
	 * Neighborhood list
	 */
	private List<GISNeighborhood> neighborhoods;

	/**
	 * New cases (unit: people)
	 */
	private int newCases;

	/**
	 * New deaths (unit: people)
	 */
	private int newDeaths;

	/**
	 * New immune (unit: people)
	 */
	private int newImmune;

	/**
	 * Create a new commune
	 * 
	 * @param id            Commune id
	 * @param population    Population
	 * @param stratumShares Stratum shares
	 */
	public GISCommune(String id, int population, double[] stratumShares) {
		this.id = id;
		this.population = population;
		this.stratumShares = stratumShares;
		this.neighborhoods = new ArrayList<>();
	}

	/**
	 * Handle the 'onNewCase' event
	 */
	public void onNewCase() {
		this.newCases++;
	}

	/**
	 * Handle the 'onNewDeath' event
	 */
	public void onNewDeath() {
		this.newDeaths++;
	}

	/**
	 * Handle the 'onNewImmune' event
	 */
	public void onNewImmune() {
		this.newImmune++;
	}

	/**
	 * Count new cases
	 */
	public int countNewCases() {
		int cases = this.newCases;
		this.newCases = 0;
		return cases;
	}

	/**
	 * Count new deaths
	 */
	public int countNewDeaths() {
		int deaths = this.newDeaths;
		this.newDeaths = 0;
		return deaths;
	}

	/**
	 * Count new immune
	 */
	public int countNewImmune() {
		int immune = this.newImmune;
		this.newImmune = 0;
		return immune;
	}

	/**
	 * Get commune id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get population
	 */
	public int getPopulation() {
		return this.population;
	}

	/**
	 * Get neighborhoods
	 */
	public List<GISNeighborhood> getNeighborhoods() {
		return this.neighborhoods;
	}

	/**
	 * Get stratum shares
	 */
	public double[] getStratumShares() {
		return this.stratumShares;
	}

	/**
	 * Add neighborhood
	 * 
	 * @param neighborhood Neighborhood
	 */
	public void addNeighborhood(GISNeighborhood neighborhood) {
		this.neighborhoods.add(neighborhood);
	}

}