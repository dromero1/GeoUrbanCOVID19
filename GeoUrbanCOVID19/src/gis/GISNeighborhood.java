package gis;

public class GISNeighborhood extends GISPolygon {

	/**
	 * New cases (unit: people)
	 */
	private int newCases;

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
	 * Count new cases
	 */
	public int countNewCases() {
		int cases = this.newCases;
		this.newCases = 0;
		return cases;
	}

}