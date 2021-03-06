package graph.version.loader;

import system.Config;
import utils.Storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import graph.version.Graph;
import graph.version.Node;

/**
 * Loader of DBLP graph
 * 
 * @author ksemer
 */
public class LoaderDBLP {
	// =================================================================
	private final int BEGINNER = 2; // label = 0
	private final int JUNIOR = 5; // label = 1
	private final int SENIOR = 10; // label = 2
	private final int PROF = 11; // label = 3
	private final static String PATH_AUTHORS_NAMES = Config.PATH_DATASET + "_authors_ids";
	private static Map<Integer, String> authorsNames;
	// =================================================================

	/**
	 * Create a labeled version graph in memory from a given DataSet nodeID \t
	 * nodeID \t time
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public Graph loadDataset() throws IOException, InterruptedException {

		System.out.println("Creating Labeled Version Graph...");
		BufferedReader br = new BufferedReader(new FileReader(Config.PATH_DATASET));
		String line = null;
		String[] edge;
		int n1_id, n2_id, time;
		long executionTime = System.currentTimeMillis();

		Graph lvg = new Graph();

		while ((line = br.readLine()) != null) {
			edge = line.split("\t");
			n1_id = Integer.parseInt(edge[0]);
			n2_id = Integer.parseInt(edge[1]);

			// edge[2] has the year/time
			time = convert(edge[2]);

			lvg.addNode(n1_id);
			lvg.addNode(n2_id);

			// src -> trg time label
			lvg.addEdge(n1_id, n2_id, time);

			if (!Config.ISDIRECTED)
				// src -> trg time label
				lvg.addEdge(n2_id, n1_id, time);
		}
		br.close();

		// load attributes
		loadAttributes(lvg);

		// For displaying memory usage
		if (Config.SHOW_MEMORY) {
			Runtime runtime = Runtime.getRuntime();

			// Run the garbage collector
			runtime.gc();

			// Calculate the used memory
			long memory = runtime.totalMemory() - runtime.freeMemory();

			System.out.println("Used memory with ViLa: " + Storage.bytesToMegabytes(memory));
		}

		System.out.println("ViLa time: " + (System.currentTimeMillis() - executionTime) + " (ms)");

		if (Config.TINLA_ENABLED || Config.CTINLA_ENABLED)
			lvg.createTimeNeighborIndex();
		else if (Config.TIPLA_ENABLED)
			lvg.createTiPLa();

		System.out.println("Loadtime of all: " + (System.currentTimeMillis() - executionTime) + " (ms)");

		loadNames(lvg.size());

		return lvg;
	}

	/**
	 * Load nodes attributes
	 * 
	 * @param lvg
	 * @throws IOException
	 */
	private void loadAttributes(Graph lvg) throws IOException {
		System.out.println("Loading labels...");

		BufferedReader br = new BufferedReader(new FileReader(Config.PATH_LABELS));
		String line = null;
		Node node;
		int value;

		// attributes for publications count
		if ((line = br.readLine()).contains("Publications_count")) {

			while ((line = br.readLine()) != null) {
				String[] token = line.split("\\|");

				// has the attribute value per time instance of the interval
				String[] attributes = token[1].split(",");

				// get node
				node = lvg.getNode(Integer.parseInt(token[0]));

				// author without co-authors in a paper
				if (node == null)
					continue;

				for (int t = 0; t < attributes.length; t++) {
					value = Integer.parseInt(attributes[t]);

					if (value == 0)
						continue;

					if (value <= BEGINNER) {
						node.updateLabelLifespan(0, t);
						lvg.udpateTiLa(t, 0, node);
					} else if (value <= JUNIOR) {
						node.updateLabelLifespan(1, t);
						lvg.udpateTiLa(t, 1, node);
					} else if (value <= SENIOR) {
						node.updateLabelLifespan(2, t);
						lvg.udpateTiLa(t, 2, node);
					} else if (value >= PROF) {
						node.updateLabelLifespan(3, t);
						lvg.udpateTiLa(t, 3, node);
					}
				}
			}
		} else if (line.contains("Conferences_count")) {
			Set<Integer> conferences = new HashSet<>();

			// attributes for conferences
			while ((line = br.readLine()) != null) {
				String[] token = line.split("\t");

				// has the attribute value per time instance of the interval
				String[] node_conf = token[0].split(",");

				// get node
				node = lvg.getNode(Integer.parseInt(node_conf[0]));

				// author without co-authors in a paper
				if (node == null)
					continue;

				// get conference id
				int conf = Integer.parseInt(node_conf[1]);

				conferences.add(conf);

				// years that author published in conf
				String[] years = token[1].split(",");

				for (int t = 0; t < years.length; t++) {
					value = Integer.parseInt(years[t]);

					node.updateLabelLifespan(conf, value);
					lvg.udpateTiLa(value, conf, node);
				}
			}
		}

		br.close();
	}

	/**
	 * Load DBLP authors names
	 * 
	 * @param size
	 * @throws IOException
	 */
	private static void loadNames(int size) throws IOException {
		System.out.println("Loading authors names in memory...");

		BufferedReader br = new BufferedReader(new FileReader(PATH_AUTHORS_NAMES));
		String line = null;
		String[] token;

		authorsNames = new HashMap<>(size);

		while ((line = br.readLine()) != null) {
			token = line.split("\t");
			authorsNames.put(Integer.parseInt(token[0]), token[1]);
		}
		br.close();
	}

	/**
	 * Return map of authors id-->name
	 * 
	 * @return
	 */
	public static Map<Integer, String> getAuthors() {
		return authorsNames;
	}

	/**
	 * Update auhorNames with object
	 * 
	 * @param object
	 */
	public static void setAuthors(Map<Integer, String> object) {
		authorsNames = object;
	}

	/**
	 * Convert a string time format to an integer value that it will be used in
	 * a BitSet
	 * 
	 * @param time
	 * @return
	 */
	private static int convert(String time) {
		return (Integer.parseInt(time) - 1959);
	}
}