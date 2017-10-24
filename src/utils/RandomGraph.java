package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Using a vg generated by FF model and output sub graphs and sub labels
 * 
 * @author ksemer
 */
public class RandomGraph {

	private static boolean createZipfLabels = false;
	private static boolean createUniformLabels = false;
	private static boolean createSubgraphs = false;
	private static boolean createNextLabels = false;
	private static boolean createSubLabels = true;

	private static int snaps = 20;
	private static int labels = 10;
	private static int graph_size = 100000;

	private static String initialLabelsFile = "random_labels_10";
	private static String path = "C:\\Users\\ksemer\\workspace\\tkde_data\\random_graph";
	private static String output_path = "C:\\Users\\ksemer\\workspace\\tkde_data\\";

	public static void main(String[] args) throws IOException {

		if (createSubgraphs)
			createSubVersionGraphs();

		if (createZipfLabels)
			createZipfInitialLabels();

		if (createUniformLabels)
			createUniformInitialLabels();

		if (createNextLabels)
			createNextLabels();

		if (createSubLabels)
			createSubVersionLabels();
	}

	/**
	 * Create labels for sub version graphs
	 * 
	 * @throws IOException
	 */
	private static void createSubVersionLabels() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(output_path + "random_labels_all"));
		String line = null, attr;
		String[] token, attrs;

		FileWriter w = new FileWriter(output_path + "random_labels_" + snaps);

		while ((line = br.readLine()) != null) {
			token = line.split("\t");
			attrs = token[1].split(",");
			attr = token[0] + "\t" + attrs[0];

			for (int i = 1; i < snaps; i++) {
				attr += "," + attrs[i];
			}

			w.write(attr + "\n");
		}
		br.close();
		w.close();
	}

	/**
	 * Create labels for all next instances
	 * 
	 * @throws IOException
	 */
	private static void createNextLabels() throws IOException {
		Random rnd = new Random();

		BufferedReader br = new BufferedReader(new FileReader(output_path + initialLabelsFile));
		FileWriter w = new FileWriter(output_path + "random_labels_all");
		String line = null, attr;
		String[] token;
		int id, l, l_prev;

		while ((line = br.readLine()) != null) {
			token = line.split("\t");
			id = Integer.parseInt(token[0]);
			l = Integer.parseInt(token[1]);
			attr = "\t" + l;
			l_prev = l;

			for (int s = 1; s < snaps; s++) {
				double x = Math.random();

				if (x <= 0.01) {
					l = rnd.nextInt(labels) + 1;

					while (l_prev == l) {
						l = rnd.nextInt(labels) + 1;
					}

					attr += "," + l;
					l_prev = l;
				} else {
					attr += "," + l_prev;
				}
			}

			w.write(id + attr + "\n");
		}
		br.close();
		w.close();
	}

	/**
	 * Create labels for first time instance using zipf
	 * 
	 * @throws IOException
	 */
	private static void createZipfInitialLabels() throws IOException {

		FileWriter w = new FileWriter(output_path + "random_labels_" + labels);
		zipf zipf = new zipf(labels, 1);
		int[] numberOfnodes = new int[labels + 1];

		// how many nodes should have this attribute
		for (int i = 1; i <= labels; i++)
			numberOfnodes[i] = (int) (zipf.getProbability(i) * graph_size);

		int attr = 1, counter = 0;
		for (int i = 0; i < graph_size; i++) {

			w.write(i + "\t" + attr + "\n");

			if (i == (numberOfnodes[attr] + counter - 1)) {
				counter += numberOfnodes[attr];
				attr++;
			}
		}
		w.close();
	}

	/**
	 * Create labels for first time instance using uniform
	 * 
	 * @throws IOException
	 */
	private static void createUniformInitialLabels() throws IOException {

		FileWriter w = new FileWriter(output_path + initialLabelsFile);
		int[] numberOfnodes = new int[labels + 1];

		// how many nodes should have this attribute
		for (int i = 1; i <= labels; i++)
			numberOfnodes[i] = graph_size / labels;

		int attr = 1, counter = 0;
		for (int i = 0; i < graph_size; i++) {

			w.write(i + "\t" + attr + "\n");

			if (i == (numberOfnodes[attr] + counter - 1)) {
				counter += numberOfnodes[attr];
				attr++;
			}
		}
		w.close();
	}

	/**
	 * Get a full version graph and create a sub version graph
	 * 
	 * @throws IOException
	 */
	public static void createSubVersionGraphs() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		String[] token;

		FileWriter w = new FileWriter(output_path + "random_" + snaps);
		int snap;

		while ((line = br.readLine()) != null) {
			token = line.split("\t");
			snap = Integer.parseInt(token[2]);

			if (snap < snaps) {
				w.write(line + "\n");
			}
		}
		br.close();
		w.close();
	}
}