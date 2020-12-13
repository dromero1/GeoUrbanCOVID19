package output;

import java.util.ArrayList;
import java.util.List;
import simulation.ParametersAdapter;

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
	 * Cumulative cases (unit: people)
	 */
	private int cumulativeCases;

	/**
	 * Active cases (unit: people)
	 */
	private int activeCases;

	/**
	 * Observers
	 */
	private List<OutputManagerObserver> observers;

	/**
	 * Create a new output manager
	 */
	public OutputManager() {
		this.observers = new ArrayList<>();
	}

	/**
	 * Handle the 'onNewCase' event
	 */
	public void onNewCase() {
		// Update statistics
		this.newCases++;
		this.cumulativeCases++;
		this.activeCases++;
		// Notify observers if the cumulative cases threshold is reached
		double threshold = ParametersAdapter.getCumulativeCasesThreshold();
		double susceptibleCount = ParametersAdapter.getSusceptibleCount();
		if (this.cumulativeCases / susceptibleCount > threshold) {
			notifyCumulativeCasesThresholdReached();
		}
	}

	/**
	 * Handle the 'onNewDeath' event
	 */
	public void onNewDeath() {
		// Update statistics
		this.newDeaths++;
		this.activeCases--;
		// Notify observers if the active cases fall to zero
		if (this.activeCases == 0) {
			notifyZeroActiveCases();
		}
	}

	/**
	 * Handle the 'onNewImmune' event
	 */
	public void onNewImmune() {
		// Update statistics
		this.newImmune++;
		this.activeCases--;
		// Notify observers if the active cases fall to zero
		if (this.activeCases == 0) {
			notifyZeroActiveCases();
		}
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

	/**
	 * Register observer
	 */
	public void registerObserver(OutputManagerObserver observer) {
		this.observers.add(observer);
	}

	/**
	 * Get cumulative cases
	 */
	public int getCumulativeCases() {
		return this.cumulativeCases;
	}

	/**
	 * Get active cases
	 */
	public int getActiveCases() {
		return this.activeCases;
	}

	/**
	 * Notify observers of zero active cases
	 */
	private void notifyZeroActiveCases() {
		for (OutputManagerObserver observer : this.observers) {
			observer.onZeroActiveCases();
		}
	}

	/**
	 * Notify observers of cumulative cases threshold reached
	 */
	private void notifyCumulativeCasesThresholdReached() {
		for (OutputManagerObserver observer : this.observers) {
			observer.onCumulativeCasesThresholdReached();
		}
	}

}