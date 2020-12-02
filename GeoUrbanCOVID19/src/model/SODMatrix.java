package model;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import repast.simphony.util.collections.Pair;

public class SODMatrix {

	/**
	 * SOD matrix
	 */
	private List<List<Double>> sod;

	/**
	 * Rows mapping
	 */
	private Map<String, Integer> rows;

	/**
	 * Columns mapping
	 */
	private Map<Integer, String> columns;

	/**
	 * Create a new SOD matrix
	 * 
	 * @param sod     SOD
	 * @param rows    Rows mapping
	 * @param columns Columns mapping
	 */
	public SODMatrix(List<List<Double>> sod, Map<String, Integer> rows,
			Map<Integer, String> columns) {
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
	public List<Pair<String, Double>> getTravelsFromOrigin(String origin) {
		List<Pair<String, Double>> tfo = new ArrayList<>();
		int i = this.rows.get(origin);
		List<Double> fromOrigin = this.sod.get(i);
		for (int j = 0; j < fromOrigin.size(); j++) {
			String destination = this.columns.get(j);
			double travels = fromOrigin.get(j);
			if (travels > 0) {
				tfo.add(new Pair<>(destination, travels));
			}
		}
		return tfo;
	}

}