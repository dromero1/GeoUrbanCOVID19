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