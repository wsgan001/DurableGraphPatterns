package utils.dblp;


/**
 * Keep in an array the # of publications per year
 * year stats count from 0 to TOTAL_YEARS
 * @author ksemer
 */
public class Node
{
	private int status[] = new int[Parser.TOTAL_YEARS];
	

	public void increase(int year)
	{
		status[year - 1959]++;
	}
	
	public int[] getStatus()
	{
		return status;
	}
}