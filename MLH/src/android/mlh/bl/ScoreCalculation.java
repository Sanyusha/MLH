package android.mlh.bl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.util.Log;

/**
 * @author alex
 *
 */
public class ScoreCalculation {
	
	private static final String LOG_D = "ScoreCalculation";
	private static final String SCORE_ERR = "SCORE CALCULATION ERROR";
	
	/**
	 * Calculates a general score according to every result score and priority.
	 * @param a_Results
	 * the hashmap of result grades.
	 * @param a_ResultPriorities
	 * the hashmap of result priorities.
	 * @return
	 * the calculated score or the error message in the case that calculation is not
	 * possible.
	 */
	public static String calculate(HashMap<String, String> a_Results,
			HashMap<String, String> a_ResultPriorities) {
		
		if (a_Results == null || a_Results.isEmpty()) return SCORE_ERR;
		
		HashMap<String, String> results = new HashMap<String, String>();
		HashMap<String, String> resultPriorities = new HashMap<String, String>();
		
		if (a_ResultPriorities == null || a_ResultPriorities.isEmpty()) {
			resultPriorities = fillDefaultPriorities(a_Results.keySet(), 1);
		} else {
			resultPriorities = a_ResultPriorities;
		}
		
		resultPriorities = getUpdatedHashMap(resultPriorities, 1);
		
		if (resultPriorities == null) return SCORE_ERR;
		
		//results = getUpdatedHashMap(a_Results, MAX_SCORE);
		results = a_Results;
		
		int finalScore = 0;

		Iterator<Entry<String, String>> it = results.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String currKey = entry.getKey();
			
			if (resultPriorities.containsKey(currKey)) {
				int resultScore = (int) (Float.parseFloat(entry.getValue())
						* Float.parseFloat(resultPriorities.get(currKey)));
				
				finalScore += resultScore;
			}
		}

		Log.d(LOG_D, "Final score is " + finalScore + "\n" + resultPriorities + "\n" + results);
		
		return finalScore + "";
	}
	
	
	private static HashMap<String, String> getUpdatedHashMap(HashMap<String, String> a_Map, float maxScore) {
		float sum = getHashMapValuesSum(a_Map);
		
		if (sum != 0) {
			return getUpdatedHashMapBySum(a_Map, sum, maxScore);
		} else {
			return null;
		}
	}

	/**
	 * Iterates over entire HashMap and calculates the sum of all its values
	 */
	private static float getHashMapValuesSum(HashMap<String, String> a_Map) {
		float sum = 0;

		Iterator<Entry<String, String>> it = a_Map.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();

			sum += Float.parseFloat(entry.getValue());
		}

		return sum;
	}

	/**
	 * Iterates over entire HashMap and updates its values
	 */
	private static HashMap<String, String> getUpdatedHashMapBySum(HashMap<String, String> a_Map, 
			float sum, float maxScore) {

		HashMap<String, String> retValue = new HashMap<String, String>();

		Iterator<Entry<String, String>> it = a_Map.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();

			float updatedValue = (Float.parseFloat(entry.getValue()) / sum) * maxScore;

			retValue.put(entry.getKey(), updatedValue + "");
		}

		return retValue;
	}
	
	/**
	 * Creates a hashmap with the results and the default priority for every result.
	 * @param a_Results
	 * the set of possible results.
	 * @param defaultPriority
	 * the priority that will be assigned to every result.
	 * @return
	 * the hashmap with the results and the default priority for every result.
	 */
	private static HashMap<String, String> fillDefaultPriorities(Set<String> a_Results, float defaultPriority) {
		HashMap<String, String> retValue = new HashMap<String, String>();

		Iterator<String> it = a_Results.iterator();

		while (it.hasNext()) {
			String key = it.next();

			retValue.put(key, defaultPriority + "");
		}

		return retValue;
	}
}
