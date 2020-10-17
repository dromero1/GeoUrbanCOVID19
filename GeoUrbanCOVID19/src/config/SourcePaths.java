package config;

public final class SourcePaths {

	/**
	 * City geometry
	 */
	public static final String CITY_GEOMETRY_SHAPEFILE = "./data/maps/City-polygon.shp";

	/**
	 * Neighborhoods geometry
	 */
	public static final String NEIGHBORHOODS_GEOMETRY_SHAPEFILE = "./data/maps/Neighborhoods-polygon.shp";

	/**
	 * SOD matrix
	 */
	public static final String SOD_MATRIX = "./data/databases/sod-matrix.csv";

	/**
	 * Policies database
	 */
	public static final String POLICIES_DATABASE = "./data/databases/policies.csv";

	/**
	 * Private constructor
	 */
	private SourcePaths() {
		throw new UnsupportedOperationException("Utility class");
	}
	
}