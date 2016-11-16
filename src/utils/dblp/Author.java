package utils.dblp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keep in an array the # of publications per year year stats count from 0 to
 * TOTAL_YEARS
 * 
 * @author ksemer
 */
public class Author {
	private static final int firstYearOfDBLP = 1959;
	private int status[] = new int[CreateDBLPGraph.currentYear - firstYearOfDBLP + 1];
	private Map<Integer, Set<Integer>> conferencesPerYear;
	private int id;

	/**
	 * Constructor
	 * @param id
	 */
	public Author(int id) {
		this.id = id;
		this.conferencesPerYear = new HashMap<>();
	}

	/**
	 * Return authors id
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * Increment publications for the given year
	 */
	public void increasePublicationSize(int year) {
		status[year - firstYearOfDBLP]++;
	}

	public int[] getStatus() {
		return status;
	}

	/**
	 * Update authors conferences and for given conference
	 * set that author has published a paper in the given year
	 * @param confID
	 * @param year
	 */
	public void addConference(int confID, int year) {
		Set<Integer> set;
		
		if ((set = conferencesPerYear.get(confID)) == null) {
			set = new HashSet<>();
			conferencesPerYear.put(confID, set);
		}
		
		set.add(year - firstYearOfDBLP);
	}
	
	/**
	 * Return conferencesPerYear structure
	 * @return
	 */
	public Map<Integer, Set<Integer>> getConferencesPerYear() {
		return conferencesPerYear;
	}
}