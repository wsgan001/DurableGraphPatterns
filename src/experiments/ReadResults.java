package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadResults {
	private static String mainDir = "/home/ksemer/workspaces/tkde_data/output1/";

	private static int qsize = 6;
	private static String rankingDuration = "a";

	// for normal plots (no random)
	private static boolean contQ = true;
	private static boolean most = true, topk = false;

	public static void main(String[] args) throws IOException {
		// readForConf(mainDir);

		randomResultsRetrieval(mainDir + "prof/");
		// resultsRetrieval(mainDir + "most_durable/begin/", 1, 5);
		// resultsRetrieval(mainDir + "topk/begin/", 1, 5);
	}

	public static void readForConf(String path) throws IOException {
		List<Integer> labels = new ArrayList<Integer>(Arrays.asList(1664, 163, 2770, 4654, 5887, 4169, 7523, 6066, 5668,
				4640, 5207, 4806, 1945, 5200, 1872, 7741, 1390, 1872, 5292));

		String file = "", prefix;

		if (most)
			file += "most_pq=";
		else if (topk)
			file += "topk_pq=";

		if (contQ)
			prefix = "_cont_tila_r=" + rankingDuration;
		else
			prefix = "_tila_r=" + rankingDuration;

		for (int l : labels) {

			for (int i = 2; i <= 6; i++) {
				List<Integer> res = readFile(path + file + l + "0000" + i + prefix, false);

				try {
					int matches = res.get(0);
					int duration = res.get(2);
					int fact = 1;

					for (int j = 1; j <= i; j++) {
						fact *= j;
					}

					if (matches != 1000)
						matches /= fact;

					System.out.print(duration + "\t" + matches + "\t");
				} catch (Exception e) {
					System.out.print(" \t \t");
				}
			}

			System.out.println();
		}
	}

	public static void randomResultsRetrieval(String path) throws IOException {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		int[] vila = new int[qsize], tinla1 = new int[qsize], tinla2 = new int[qsize], ctinla1 = new int[qsize],
				ctinla2 = new int[qsize], tipla = new int[qsize];
		int[] vila_c = new int[qsize], tinla1_c = new int[qsize], tinla2_c = new int[qsize], ctinla1_c = new int[qsize],
				ctinla2_c = new int[qsize], tipla_c = new int[qsize];
		List<Integer> res;
		String name;
		int time, size;

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				name = listOfFiles[i].getName();

				if (!name.contains("r=" + rankingDuration)) {
					continue;
				}

				res = readFile(listOfFiles[i].getPath(), true);

				if (res.size() < 3)
					continue;

				time = res.get(1);
				size = res.get(3);

				if (name.contains("tila")) {

					vila[size - 1] += time;
					vila_c[size - 1]++;
				} else if (name.contains("tipla")) {

					tipla[size - 1] += time;
					tipla_c[size - 1]++;
				} else if (name.contains("ctinla(1)")) {
					ctinla1[size - 1] += time;
					ctinla1_c[size - 1]++;
				} else if (name.contains("ctinla(2)")) {

					ctinla2[size - 1] += time;
					ctinla2_c[size - 1]++;

				} else if (name.contains("tinla(1)")) {
					tinla1[size - 1] += time;
					tinla1_c[size - 1]++;
				} else if (name.contains("tinla(2)")) {
					tinla2[size - 1] += time;
					tinla2_c[size - 1]++;
				}
			}
		}

		System.out.println("Q Size\tVILA\tTINLA(1)\tTINLA(2)\tCTINLA(1)\tCTINLA(2)\tTIPLA\t");
		for (int i = 1; i < qsize; i++) {
			System.out.println((i + 1) + "\t" + (vila[i] / vila_c[i]) + "\t" + (tinla1[i] / tinla1_c[i]) + "\t"
					+ (tinla2[i] / tinla2_c[i]) + "\t" + (ctinla1[i] / ctinla1_c[i]) + "\t"
					+ (ctinla2[i] / ctinla2_c[i]) + "\t" + (tipla[i] / tipla_c[i]));
		}
	}

	public static void resultsRetrieval(String file, int st, int end) throws IOException {
		System.out.println("Q Size\tVILA\tTINLA(1)\tTINLA(2)\tCTINLA(1)\tCTINLA(2)\tTIPLA\t");
		String line, prefix;

		if (most)
			file += "most_pq=";
		else if (topk)
			file += "topk_pq=";

		for (int i = st; i <= end; i++) {
			line = "" + (i + 1) + "\t";
			prefix = file + i;

			if (contQ)
				prefix += "_cont";

			line += readFile(prefix + "_tila_r=" + rankingDuration, false).get(1) + "\t";
			line += readFile(prefix + "_tinla(1)_r=" + rankingDuration, false).get(1) + "\t";
			line += readFile(prefix + "_tinla(2)_r=" + rankingDuration, false).get(1) + "\t";
			line += readFile(prefix + "_ctinla(1)_r=" + rankingDuration, false).get(1) + "\t";
			line += readFile(prefix + "_ctinla(2)_r=" + rankingDuration, false).get(1) + "\t";
			line += readFile(prefix + "_tipla_r=" + rankingDuration, false).get(1) + "\t";
			System.out.println(line);
		}
	}

	private static List<Integer> readFile(String filePath, boolean random) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;
		String[] token;
		int numbOfMatches = 0, time = 0, duration = -1, size = 0;
		List<Integer> result = new ArrayList<>();

		while ((line = br.readLine()) != null) {
			if (line.contains("Total matches")) {
				token = line.split(":");
				numbOfMatches = Integer.parseInt(token[1].trim());
				result.add(numbOfMatches);
			} else if (line.contains("Recursive Time")) {
				token = line.split(":");
				token[1] = token[1].replace("(ms)", "");
				time = Integer.parseInt(token[1].trim());
				result.add(time);
			} else if (line.contains("Duration")) {
				token = line.split(":");
				duration = Integer.parseInt(token[1].trim());
				result.add(duration);

				if (!random)
					break;
			} else if (line.contains("pg_id")) {
				token = line.split(":");
				size = Integer.parseInt(token[1].trim());
			} else if (line.contains("--->") || line.contains("No matches")) {
				if (line.contains("No matches"))
					result.add(0);
				result.add(size + 1);
				break;
			}
		}
		br.close();

		return result;
	}
}