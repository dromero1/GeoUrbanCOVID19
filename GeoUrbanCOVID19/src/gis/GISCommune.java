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
	 * Neighborhood list
	 */
	private List<GISNeighborhood> neighborhoods;

	/**
	 * Create a new commune
	 * 
	 * @param id         Commune id
	 * @param population Population
	 */
	public GISCommune(String id, int population) {
		this.id = id;
		this.population = population;
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
	 * Add neighborhood
	 * 
	 * @param neighborhood Neighborhood
	 */
	public void addNeighborhood(GISNeighborhood neighborhood) {
		this.neighborhoods.add(neighborhood);
	}

}