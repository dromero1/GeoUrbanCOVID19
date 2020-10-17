package config;

public final class SourceFeatures {

	/**
	 * Policies database - type column
	 */
	public static final int POLICIES_TYPE_COLUMN = 0;

	/**
	 * Policies database - begin day column
	 */
	public static final int POLICIES_BEGIN_DAY_COLUMN = 1;

	/**
	 * Policies database - end day column
	 */
	public static final int POLICIES_END_DAY_COLUMN = 2;

	/**
	 * Communes database - id column
	 */
	public static final int COMMUNES_ID_COLUMN = 0;

	/**
	 * Communes database - population column
	 */
	public static final int COMMUNES_POPULATION_COLUMN = 2;

	/**
	 * Private constructor
	 */
	private SourceFeatures() {
		throw new UnsupportedOperationException("Utility class");
	}

}