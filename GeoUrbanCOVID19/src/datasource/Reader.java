package datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import config.SourceFeatures;
import model.SODMatrix;
import policy.Policy;
import policy.PolicyFactory;

public class Reader {

	/**
	 * Source split regular expression
	 */
	private static final String SOURCE_SPLIT_REGEX = ";";

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
	public static List<SimpleFeature> loadGeometryFromShapefile(String filename) {
		File file = new File(filename);
		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			SimpleFeatureCollection featureCollection = featureSource.getFeatures();
			SimpleFeatureIterator featureIterator = featureCollection.features();
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
		File file = new File(filename);
		try (Scanner scanner = new Scanner(file)) {
			boolean first = true;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if (first) {
					first = false;
				} else {
					String[] elements = data.split(SOURCE_SPLIT_REGEX);
					String policyType = null;
					int beginDay = 0;
					int endDay = 0;
					for (int i = 0; i < elements.length; i++) {
						switch (i) {
						case SourceFeatures.POLICIES_TYPE_COLUMN:
							policyType = elements[i];
							break;
						case SourceFeatures.POLICIES_BEGIN_DAY_COLUMN:
							beginDay = Integer.parseInt(elements[i]);
							break;
						case SourceFeatures.POLICIES_END_DAY_COLUMN:
							endDay = Integer.parseInt(elements[i]);
							break;
						default:
							break;
						}
					}
					Policy policy = PolicyFactory.makePolicy(policyType, beginDay, endDay);
					policies.add(policy);
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		return policies;
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

}