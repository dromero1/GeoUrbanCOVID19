package gis;

public class GISNeighborhood extends GISPolygon {

	/**
	 * Commune
	 */
	private GISCommune commune;

	/**
	 * Detail
	 */
	private GISNeighborhoodDetail detail;

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
	 * Create a new geo-spatial neighborhood
	 * 
	 * @param id Neighborhood id
	 */
	public GISNeighborhood(String id) {
		super(id);
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
	 * Get commune
	 */
	public GISCommune getCommune() {
		return this.commune;
	}

	/**
	 * Get detail
	 */
	public GISNeighborhoodDetail getDetail() {
		return this.detail;
	}

	/**
	 * Set commune
	 * 
	 * @param commune Commune
	 */
	public void setCommune(GISCommune commune) {
		this.commune = commune;
		this.commune.addNeighborhood(this);
	}

	/**
	 * Set detail
	 * 
	 * @param detail Neighborhood detail
	 */
	public void setDetail(GISNeighborhoodDetail detail) {
		this.detail = detail;
	}

}