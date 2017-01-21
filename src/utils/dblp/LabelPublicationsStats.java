package utils.dblp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Stats for DBLP labels (BEGINNER, JUNIOR, SENIOR, PROF)
 * 
 * @author ksemer
 *
 */
public class LabelPublicationsStats {
	private static String dataset = "/home/ksemer/workspaces/tkde_data/dblp/dblp_publications_labels";
	private static int MAX_INTERVAL = 58;

	private static final int BEGINNER = 2;
	private static final int JUNIOR = 5;
	private static final int SENIOR = 10;
	private static final int PROF = 11;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dataset));
		String line = null;
		String[] id, publicationsPerYear;

		int[][] ranking = new int[MAX_INTERVAL][4];

		br.readLine();
		while ((line = br.readLine()) != null) {
			id = line.split("\\|");
			publicationsPerYear = id[1].split(",");
			int number;

			for (int i = 0; i < publicationsPerYear.length; i++) {
				number = Integer.parseInt(publicationsPerYear[i]);

				if (number == 0)
					continue;

				if (number <= BEGINNER)
					ranking[i][0]++;
				else if (number <= JUNIOR)
					ranking[i][1]++;
				else if (number <= SENIOR)
					ranking[i][2]++;
				else if (number >= PROF)
					ranking[i][3]++;
			}
		}
		br.close();

		for (int i = 0; i < ranking.length; i++) {
			System.out.print((1959 + i) + "\t");

			for (int j = 0; j < ranking[0].length; j++) {
				System.out.print(ranking[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}
}