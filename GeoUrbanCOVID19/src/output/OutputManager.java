package output;

public class OutputManager {

	/**
	 * New cases (unit: cases)
	 */
	private int newCases;

	/**
	 * Measure the current effective reproductive number
	 */
	public double measureRe() {
		return 0.0;
	}

	/**
	 * Handle the 'onNewCase' event
	 */
	public void onNewCase() {
		this.newCases++;
	}

	/**
	 * Count new cases
	 */
	public int countNewCases() {
		int cases = this.newCases;
		this.newCases = 0;
		return cases;
	}

}