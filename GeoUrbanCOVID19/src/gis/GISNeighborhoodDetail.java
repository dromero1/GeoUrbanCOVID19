package gis;

public class GISNeighborhoodDetail {

	/**
	 * Neighborhood id
	 */
	private String id;

	/**
	 * Commune id
	 */
	private String communeId;

	/**
	 * Create a new neighborhood detail
	 * 
	 * @param id        Neighborhood id
	 * @param communeId Commune id
	 */
	public GISNeighborhoodDetail(String id, String communeId) {
		this.id = id;
		this.communeId = communeId;
	}

	/**
	 * Get neighborhood id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get commune id
	 */
	public String getCommuneId() {
		return communeId;
	}

}