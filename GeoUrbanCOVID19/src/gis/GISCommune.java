package gis;

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
	 * Create a new commune
	 * 
	 * @param id Commune id
	 */
	public GISCommune(String id, int population) {
		this.id = id;
		this.population = population;
	}

	/**
	 * Get commune id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get population
	 */
	public int getPopulation() {
		return population;
	}

}