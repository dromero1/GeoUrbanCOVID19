package gis;

import com.vividsolutions.jts.geom.Geometry;
import repast.simphony.space.gis.Geography;

public class GISPolygon {

	/**
	 * Polygon id
	 */
	protected String id;

	/**
	 * Reference to geometry
	 */
	protected Geometry geometry;

	/**
	 * Reference to geography projection
	 */
	protected Geography<Object> geography;

	/**
	 * Create a new geo-spatial polygon
	 * 
	 * @param id Polygon id
	 */
	public GISPolygon(String id) {
		this.id = id;
	}

	/**
	 * Set geometry in the geography projection
	 * 
	 * @param geography Reference to geography projection
	 * @param geometry  Reference to geometry
	 */
	public void setGeometryInGeography(Geography<Object> geography,
			Geometry geometry) {
		this.geography = geography;
		this.geometry = geometry;
		this.geography.move(this, this.geometry);
	}

	/**
	 * Get polygon id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get reference to geometry
	 */
	public Geometry getGeometry() {
		return this.geometry;
	}

}