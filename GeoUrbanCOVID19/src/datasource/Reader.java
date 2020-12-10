package datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import config.SourceFeatures;
import gis.GISCommune;
import gis.GISNeighborhoodDetail;
import model.SODMatrix;
import policy.Policy;

public class Reader {

	/**
	 * Source split regular expression
	 */
	private static final String SOURCE_SPLIT_REGEX = ",";

	/**
	 * Private constructor
	 */
	private Reader() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Load geometry from shapefile
	 * 
	 * @param filename File name
	 */
	public static List<SimpleFeature> loadGeometryFromShapefile(
			String filename) {
		File file = new File(filename);
		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			SimpleFeatureCollection featureCollection = featureSource
					.getFeatures();
			SimpleFeatureIterator featureIterator = featureCollection
					.features();
			ArrayList<SimpleFeature> simpleFeatures = new ArrayList<>();
			while (featureIterator.hasNext()) {
				simpleFeatures.add(featureIterator.next());
			}
			featureIterator.close();
			store.dispose();
			return simpleFeatures;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * Read policies database
	 * 
	 * @param filename File name
	 */
	public static List<Policy> readPoliciesDatabase(String filename) {
		List<Policy> policies = new ArrayList<>();
		try (FileReader reader = new FileReader(filename)) {
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return policies;
	}

	/**
	 * Read communes database
	 * 
	 * @param filename File name
	 */
	public static Map<String, GISCommune> readCommunesDatabase(
			String filename) {
		Map<String, GISCommune> communes = new HashMap<>();
		File file = new File(filename);
		try (Scanner scanner = new Scanner(file)) {
			boolean first = true;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if (first) {
					first = false;
				} else {
					String[] elements = data.split(SOURCE_SPLIT_REGEX);
					String id = null;
					int population = 0;
					double[] stratumShares = new double[6];
					for (int i = 0; i < elements.length; i++) {
						switch (i) {
						case SourceFeatures.COMMUNES_ID_COLUMN:
							id = elements[i];
							break;
						case SourceFeatures.COMMUNES_POPULATION_COLUMN:
							population = Integer.parseInt(elements[i]);
							break;
						case SourceFeatures.COMMUNES_STRATUM_1_SHARE_COLUMN:
							stratumShares[0] = Double.parseDouble(elements[i]);
							break;
						case SourceFeatures.COMMUNES_STRATUM_2_SHARE_COLUMN:
							stratumShares[1] = Double.parseDouble(elements[i]);
							break;
						case SourceFeatures.COMMUNES_STRATUM_3_SHARE_COLUMN:
							stratumShares[2] = Double.parseDouble(elements[i]);
							break;
						case SourceFeatures.COMMUNES_STRATUM_4_SHARE_COLUMN:
							stratumShares[3] = Double.parseDouble(elements[i]);
							break;
						case SourceFeatures.COMMUNES_STRATUM_5_SHARE_COLUMN:
							stratumShares[4] = Double.parseDouble(elements[i]);
							break;
						case SourceFeatures.COMMUNES_STRATUM_6_SHARE_COLUMN:
							stratumShares[5] = Double.parseDouble(elements[i]);
							break;
						default:
							break;
						}
					}
					GISCommune commune = new GISCommune(id, population,
							stratumShares);
					communes.put(id, commune);
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return communes;
	}

	/**
	 * Read neighborhoods database
	 * 
	 * @param filename File name
	 */
	public static Map<String, GISNeighborhoodDetail> readNeighborhoodsDatabase(
			String filename) {
		Map<String, GISNeighborhoodDetail> neighborhoods = new HashMap<>();
		File file = new File(filename);
		try (Scanner scanner = new Scanner(file)) {
			boolean first = true;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if (first) {
					first = false;
				} else {
					String[] elements = data.split(SOURCE_SPLIT_REGEX);
					String id = null;
					String communeId = null;
					for (int i = 0; i < elements.length; i++) {
						switch (i) {
						case SourceFeatures.NEIGHBORHOODS_ID_COLUMN:
							id = elements[i];
							break;
						case SourceFeatures.NEIGHBORHOODS_COMMUNE_COLUMN:
							communeId = elements[i];
							break;
						default:
							break;
						}
					}
					GISNeighborhoodDetail detail = new GISNeighborhoodDetail(id,
							communeId);
					neighborhoods.put(id, detail);
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return neighborhoods;
	}

	/**
	 * Read SOD matrix
	 * 
	 * @param filename File name
	 */
	public static SODMatrix readSODMatrix(String filename) {
		List<List<Double>> sod = new ArrayList<>();
		HashMap<String, Integer> rows = new HashMap<>();
		HashMap<Integer, String> columns = new HashMap<>();
		File file = new File(filename);
		try (Scanner scanner = new Scanner(file)) {
			int i = 0;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				String[] elements = data.split(SOURCE_SPLIT_REGEX);
				if (i == 0) {
					for (int j = 1; j < elements.length; j++) {
						columns.put(j - 1, elements[j]);
					}
				} else {
					List<Double> row = new ArrayList<>();
					for (int j = 0; j < elements.length; j++) {
						if (j == 0) {
							rows.put(elements[j], i - 1);
						} else {
							row.add(Double.parseDouble(elements[j]));
						}
					}
					sod.add(row);
				}
				i++;
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return new SODMatrix(sod, rows, columns);
	}

	/**
	 * Read policy compliance database
	 * 
	 * @param filename File name
	 */
	public static Map<Integer, Double> readPolicyComplianceDatabase(
			String filename) {
		Map<Integer, Double> policyCompliance = new HashMap<>();
		File file = new File(filename);
		try (Scanner scanner = new Scanner(file)) {
			boolean first = true;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if (first) {
					first = false;
				} else {
					String[] elements = data.split(SOURCE_SPLIT_REGEX);
					int stratum = 0;
					double share = 0.0;
					for (int i = 0; i < elements.length; i++) {
						switch (i) {
						case SourceFeatures.POLICY_COMPLIANCE_STRATUM_COLUMN:
							stratum = Integer.parseInt(elements[i]);
							break;
						case SourceFeatures.POLICY_COMPLIANCE_SHARE_COLUMN:
							share = Double.parseDouble(elements[i]);
							break;
						default:
							break;
						}
					}
					policyCompliance.put(stratum, share);
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return policyCompliance;
	}

	/**
	 * Read mask usage database
	 * 
	 * @param filename File name
	 */
	public static Map<Integer, Double> readMaskUsageDatabase(String filename) {
		Map<Integer, Double> maskUsage = new HashMap<>();
		File file = new File(filename);
		try (Scanner scanner = new Scanner(file)) {
			boolean first = true;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if (first) {
					first = false;
				} else {
					String[] elements = data.split(SOURCE_SPLIT_REGEX);
					int stratum = 0;
					double share = 0.0;
					for (int i = 0; i < elements.length; i++) {
						switch (i) {
						case SourceFeatures.MASK_USAGE_STRATUM_COLUMN:
							stratum = Integer.parseInt(elements[i]);
							break;
						case SourceFeatures.MASK_USAGE_SHARE_COLUMN:
							share = Double.parseDouble(elements[i]);
							break;
						default:
							break;
						}
					}
					maskUsage.put(stratum, share);
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return maskUsage;
	}

}