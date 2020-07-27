package model;

import java.util.ArrayList;
import java.util.HashMap;
import repast.simphony.util.collections.Pair;

public class SODMatrix {

	/**
	 * SOD matrix
	 */
	private ArrayList<ArrayList<Double>> sod;

	/**
	 * Rows mapping
	 */
	private HashMap<String, Integer> rows;

	/**
	 * Columns mapping
	 */
	private HashMap<Integer, String> columns;

	/**
	 * Create a new SOD matrix
	 * 
	 * @param sod     SOD
	 * @param rows    Rows mapping
	 * @param columns Columns mapping
	 */
	public SODMatrix(ArrayList<ArrayList<Double>> sod, HashMap<String, Integer> rows,
			HashMap<Integer, String> columns) {
		this.sod = sod;
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * Contains origin?
	 * 
	 * @param origin Origin
	 */
	public boolean containsOrigin(String origin) {
		return this.rows.containsKey(origin);
	}

	/**
	 * Get travels from origin
	 * 
	 * @param origin Origin
	 */
	public ArrayList<Pair<String, Double>> getTravelsFromOrigin(String origin) {
		ArrayList<Pair<String, Double>> tfo = new ArrayList<>();
		int i = this.rows.get(origin);
		ArrayList<Double> fromOrigin = this.sod.get(i);
		for (int j = 0; j < fromOrigin.size(); j++) {
			String destination = this.columns.get(j);
			double travels = fromOrigin.get(j);
			if (travels > 0) {
				tfo.add(new Pair<String, Double>(destination, travels));
			}
		}
		return tfo;
	}

}