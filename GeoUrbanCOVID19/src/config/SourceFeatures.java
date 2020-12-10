package config;

public final class SourceFeatures {

	/**
	 * Communes database - id column
	 */
	public static final int COMMUNES_ID_COLUMN = 0;

	/**
	 * Communes database - population column
	 */
	public static final int COMMUNES_POPULATION_COLUMN = 2;

	/**
	 * Communes database - stratum 1 share column
	 */
	public static final int COMMUNES_STRATUM_1_SHARE_COLUMN = 3;

	/**
	 * Communes database - stratum 2 share column
	 */
	public static final int COMMUNES_STRATUM_2_SHARE_COLUMN = 4;

	/**
	 * Communes database - stratum 3 share column
	 */
	public static final int COMMUNES_STRATUM_3_SHARE_COLUMN = 5;
	/**
	 * Communes database - stratum 4 share column
	 */
	public static final int COMMUNES_STRATUM_4_SHARE_COLUMN = 6;
	/**
	 * Communes database - stratum 5 share column
	 */
	public static final int COMMUNES_STRATUM_5_SHARE_COLUMN = 7;

	/**
	 * Communes database - stratum 6 share column
	 */
	public static final int COMMUNES_STRATUM_6_SHARE_COLUMN = 8;

	/**
	 * Neighborhoods database - id column
	 */
	public static final int NEIGHBORHOODS_ID_COLUMN = 0;

	/**
	 * Neighborhoods database - commune column
	 */
	public static final int NEIGHBORHOODS_COMMUNE_COLUMN = 1;

	/**
	 * Policy compliance database - stratum column
	 */
	public static final int POLICY_COMPLIANCE_STRATUM_COLUMN = 0;

	/**
	 * Policy compliance database - share column
	 */
	public static final int POLICY_COMPLIANCE_SHARE_COLUMN = 1;

	/**
	 * Mask usage database - stratum column
	 */
	public static final int MASK_USAGE_STRATUM_COLUMN = 0;

	/**
	 * Mask usage database - share column
	 */
	public static final int MASK_USAGE_SHARE_COLUMN = 1;

	/**
	 * Private constructor
	 */
	private SourceFeatures() {
		throw new UnsupportedOperationException("Utility class");
	}

}