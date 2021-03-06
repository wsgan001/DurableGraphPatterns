package utils.wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class CreateWikipediaGraph {

	// wiki dataset sorted by timestamp
	private static String dataset = "/home/ksemer/Desktop/data/en-wiki-talk-sorted";
	private static String output = "wiki-graph";
	private static int numOfSnapshots = 500;
	// private static SimpleDateFormat format = new SimpleDateFormat(
	// "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	public static void main(String[] args) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(dataset));
		FileWriter w = new FileWriter(output);
		String line = null;
		String[] token = null;

		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		Calendar cal = Calendar.getInstance();

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(dataset)));
		lnr.skip(Long.MAX_VALUE);
		int numOfEdges = lnr.getLineNumber() + 1;
		lnr.close();
		
		System.out.println("Total number of edges: " + numOfEdges);

		int numOfEdgesPerSnap = numOfEdges / numOfSnapshots;
		int c = 0, time = 0;
		Set<Integer> users = new HashSet<>();
		Set<Long> time_instances = new HashSet<>();
		
		while ((line = br.readLine()) != null) {
			token = line.split("\t");
			Date date = format.parse(token[2].trim());
			cal.setTime(date);
			time_instances.add(date.getTime());

			// D = cal.get(Calendar.YEAR) + "\t" + cal.get(Calendar.MONTH) + "\t" +
			// cal.get(Calendar.DAY_OF_MONTH);

			if (c > numOfEdgesPerSnap) {
				c = 0;
				System.out.println("Time: " + time + " users: " + users.size());
				time++;
			}

			c++;
			
			users.add(Integer.parseInt(token[0]));
			users.add(Integer.parseInt(token[1]));
			w.write(token[0] + "\t" + token[1] + "\t" + time + "\n");
		}
		br.close();
		w.close();
		System.out.println("Time: " + time + " users: " + users.size());
		System.out.println("Total time instances: " + time_instances.size());
	}
}