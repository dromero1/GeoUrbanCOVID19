package output;

public class OutputManager {

	/**
	 * New cases (unit: people)
	 */
	private int newCases;

	/**
	 * New deaths (unit: people)
	 */
	private int newDeaths;

	/**
	 * New immune (unit: people)
	 */
	private int newImmune;

	/**
	 * Handle the 'onNewCase' event
	 */
	public void onNewCase() {
		this.newCases++;
	}

	/**
	 * Handle the 'onNewDeath' event
	 */
	public void onNewDeath() {
		this.newDeaths++;
	}

	/**
	 * Handle the 'onNewImmune' event
	 */
	public void onNewImmune() {
		this.newImmune++;
	}

	/**
	 * Count new cases
	 */
	public int countNewCases() {
		int cases = this.newCases;
		this.newCases = 0;
		return cases;
	}

	/**
	 * Count new deaths
	 */
	public int countNewDeaths() {
		int deaths = this.newDeaths;
		this.newDeaths = 0;
		return deaths;
	}

	/**
	 * Count new immune
	 */
	public int countNewImmune() {
		int immune = this.newImmune;
		this.newImmune = 0;
		return immune;
	}

}