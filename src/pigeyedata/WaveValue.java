package pigeyedata;

public class WaveValue {
	String type; // copies the ID of the pig being analyzed
	double latency; // Saves the latency of each WaveForm measurement
	double waveForm; // Saves the raw WaveForm value for each measurement
	double smoothed; // Saves the smoothed WaveForm value for each measurement
	String leftOrRight; // Saves an "L" for if the pig eye being analyzed is Left and "R" for right
	boolean alpha; // Determines whether the WaveValue object is an a-wave value
	boolean beta; // Determines whether the WaveValue object is a b-wave value
	boolean smoothAnalysis;// Saves whether the WaveValue object is a smoothed or raw measurement.
	//WaveValue object constructor
	public WaveValue(String leftOrRight, String type, double latency, double waveForm, double smoothed) {
		this.type = type;
		this.latency = latency;
		this.waveForm = waveForm;
		this.leftOrRight = leftOrRight;
		this.smoothed = smoothed;
	}
	// returns the latency of the object
	public double getLatency() {
		return latency;
	}
	//return the raw wave form of the object
	public double getWaveForm() {
		return waveForm;
	}
	// returns whether the object is smoothed or raw data
	public double getSmoothed() {
		return smoothed;
	}
	// retrieves whether the object is analyzing the left or right eye
	public String getLeftOrRight() {
		return leftOrRight;
	}
	// toString() prints the contents of the WaveValue object in a readable form.
	public String toString() {
		String str = "TYPE:" + type + "   WAVEFORM:" + waveForm + "   SMOOTHED:" + smoothed + "   LATENCY:" + latency + "   LEFT OR RIGHT:" + leftOrRight;
		if(alpha) {
			str += "   alpha";
		}else if (beta) {
			str += "   beta";
		}
		if(smoothAnalysis) {
			str += "   Smoothed Analysis";
		}else {
			str += "   Raw Analysis";
		}
		return str;
	}
}