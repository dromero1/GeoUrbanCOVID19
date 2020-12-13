package output;

public interface OutputManagerObserver {

	/**
	 * Handle the 'onZeroActiveCases' event
	 */
	public void onZeroActiveCases();

	/**
	 * Handle the 'onCumulativeCasesThresholdReached' event
	 */
	public void onCumulativeCasesThresholdReached();

}