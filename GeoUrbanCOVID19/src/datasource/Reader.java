package datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import config.DBFeatures;
import model.Policy;
import model.PolicyType;
import util.PolicyUtil;

public class Reader {

	/**
	 * Load geometry from shapefile
	 * 
	 * @param filename File name
	 */
	public static ArrayList<SimpleFeature> loadGeometryFromShapefile(String filename) {
		File file = new File(filename);
		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			SimpleFeatureCollection featureCollection = featureSource.getFeatures();
			SimpleFeatureIterator featureIterator = featureCollection.features();
			ArrayList<SimpleFeature> simpleFeatures = new ArrayList<SimpleFeature>();
			while (featureIterator.hasNext()) {
				simpleFeatures.add(featureIterator.next());
			}
			featureIterator.close();
			store.dispose();
			return simpleFeatures;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Read policies database
	 * 
	 * @param filename File name
	 */
	public static ArrayList<Policy> readPoliciesDatabase(String filename) {
		ArrayList<Policy> policies = new ArrayList<>();
		try {
			File file = new File(filename);
			Scanner scanner = new Scanner(file);
			boolean first = true;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if (first) {
					first = false;
					continue;
				}
				String[] elements = data.split(";");
				String policyClass = "";
				int beginDay = 0;
				int endDay = 0;
				for (int i = 0; i < elements.length; i++) {
					switch (i) {
					case DBFeatures.POLICIES_TYPE_COLUMN:
						policyClass = elements[i];
						break;
					case DBFeatures.POLICIES_BEGIN_DAY_COLUMN:
						beginDay = Integer.parseInt(elements[i]);
						break;
					case DBFeatures.POLICIES_END_DAY_COLUMN:
						endDay = Integer.parseInt(elements[i]);
						break;
					default:
						break;
					}
				}
				PolicyType policyType = PolicyUtil.convertToInnerFormat(policyClass);
				Policy policy = new Policy(policyType, beginDay, endDay);
				policies.add(policy);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return policies;
	}

}