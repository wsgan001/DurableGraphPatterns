package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import algorithm.DurableMatching;
import algorithm.DurableTopkMatching;
import graph.pattern.PatternGraph;
import graph.pattern.PatternNode;
import graph.version.Graph;
import graph.version.Node;
import graph.version.loader.LoaderDBLP;
import graph.version.loader.LoaderProteins;
import graph.version.loader.LoaderRandom;
import graph.version.loader.LoaderWikipedia;
import graph.version.loader.LoaderYT;
import system.Config;
import utils.QueryGenerator;
import utils.Storage;

/**
 * Experiment class
 * 
 * @author ksemer
 */
public class Experiments {
	private static String out;
	public static boolean runRandomQueries = true;
	private static boolean randomDataset = false;
	private static int randomIterations = 20;
	private static List<PatternGraph> queries = new ArrayList<>();
	private static int randomDatasetInterval;

	/**
	 * Main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Config.loadConfigs();

		Config.K = 10;
		Config.ADAPTIVE_THETA = 0.5;
		out = Config.PATH_OUTPUT;

		if (randomDataset) {
			for (int i = 20; i <= 40; i += 20) {
				System.out.println("------Snapshots: " + i + "------");
				randomDatasetInterval = i;
				Config.PATH_DATASET = "/home/ksemer/workspaces/tkde_data/random_dataset/random_" + i;
				Config.PATH_LABELS = "/home/ksemer/workspaces/tkde_data/random_dataset/random_labels_" + i;
				Config.PATH_OUTPUT = "";

				if (!runRandomQueries)
					runIndex();
				else
					runRandomIndex("");
				System.out.println("-----------");
			}
			return;
		}

		if (!runRandomQueries)
			runIndex();
		else {
			if (Config.PATH_DATASET.toLowerCase().contains("dblp")) {
				runRandomIndex("dblp");
			} else if (Config.PATH_DATASET.toLowerCase().contains("yt")) {
				runRandomIndex("yt");
			} else if (Config.PATH_DATASET.toLowerCase().contains("wiki")) {
				runRandomIndex("wiki");
			} else
				runRandomIndex("");
		}
	}

	@SuppressWarnings("unchecked")
	private static void runRandomIndex(String name) throws Exception {

		File f = new File(Config.PATH_OBJECT + name);

		if (f.exists()) {
			
			queries = (List<PatternGraph>) Storage.deserialize(Config.PATH_OBJECT + name);
			System.out.println("Queries have been loaded");
			PatternGraph pg;
			PatternNode pn1, pn2;

			for (int i = 0; i < queries.size(); i++) {
				pg = queries.get(i);

				System.out.println("Pattern graph id: " + pg.getID());

				for (int j = 0; j < pg.getNodes().size(); j++) {
					pn1 = pg.getNode(j);

					for (int k = j + 1; k < pg.getNodes().size(); k++) {
						pn2 = pg.getNodes().get(k);

						if (pn1.getAdjacency().contains(pn2)) {
							if (!Config.ISDIRECTED) {
								System.out.println(pn1.getID() + " (" + pn1.getLabel() + ") <--> " + pn2.getID() + " ("
										+ pn2.getLabel() + ")");
							} else
								System.out.println(pn1.getID() + " (" + pn1.getLabel() + ") --> " + pn2.getID() + " ("
										+ pn2.getLabel() + ")");
						}
					}
				}
			}
		} else {

			Config.TINLA_ENABLED = false;
			Config.CTINLA_ENABLED = false;
			Config.TIPLA_ENABLED = false;

			Graph lvg = loadGraph();
			List<Node> allNodes = new ArrayList<>(lvg.getNodes());
			Random r = new Random();

			for (int i = 0; i < randomIterations; i++) {
				PatternGraph pg = null;
				System.out.println("Query generation iteration: " + (i + 1));

				for (int size = 2; size <= 6; size++) {

					int j = r.nextInt(lvg.size() - 1);
					Node node = allNodes.get(j);

					if (QueryGenerator.dfs(node, size)) {
						// get random query as a pattern graph
						pg = QueryGenerator.getQuery(size);
						queries.add(pg);
					} else
						size--;
				}
			}

			lvg.getNodes().clear();
			lvg = null;
			System.gc();
			System.out.println("Queries Generation has finished!!!");
			Storage.serialize(queries, Config.PATH_OBJECT + name);
		}

		runIndex();
	}

	private static void runIndex() throws Exception {

		Config.TINLA_ENABLED = false;
		Config.CTINLA_ENABLED = false;
		Config.TIPLA_ENABLED = false;

		runQ();

		Config.TINLA_ENABLED = true;
		Config.CTINLA_ENABLED = false;
		Config.TIPLA_ENABLED = false;

		Config.TINLA_R = 1;
		runQ();

		Config.TINLA_R = 2;
		runQ();

		Config.TINLA_ENABLED = false;
		Config.CTINLA_ENABLED = true;
		Config.TIPLA_ENABLED = false;

		Config.CTINLA_R = 1;
		runQ();

		Config.CTINLA_R = 2;
		runQ();

		Config.TINLA_ENABLED = false;
		Config.CTINLA_ENABLED = false;
		Config.TIPLA_ENABLED = true;

		runQ();
		
		Config.TINLA_ENABLED = true;
		Config.CTINLA_ENABLED = false;
		Config.TIPLA_ENABLED = true;

		runQ();
	}

	private static void runQ() throws Exception {
		String dataset = Config.PATH_DATASET.toLowerCase();

		Graph lvg = loadGraph();

		// for dblp dataset
		if (dataset.contains("dblp")) {

			if (runRandomQueries)
				run_random(lvg, "dblp");
			else {
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_prof.txt", "prof");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_senior.txt", "senior");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_junior.txt", "junior");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_begin.txt", "begin");
			}

			if (runRandomQueries)
				run_random(lvg, "dblp");
			else {
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_prof.txt", "prof");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_senior.txt", "senior");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_junior.txt", "junior");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_begin.txt", "begin");
			}
		} else if (dataset.contains("yt")) { // youtube

			if (runRandomQueries)
				run_random(lvg, "yt");
			else {
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_least.txt", "least");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_most.txt", "most");
			}

			if (runRandomQueries)
				run_random(lvg, "yt");
			else {
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_least.txt", "least");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_most.txt", "most");
			}
		} else if (dataset.contains("random")) {
			if (runRandomQueries)
				run_random(lvg, "random");
			else {
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_least_rand.txt", "least");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_most_rand.txt", "most");
			}

			if (runRandomQueries)
				run_random(lvg, "random");
			else {
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_least_rand.txt", "least");
				run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_most_rand.txt", "most");
			}
		} else { // proteins

			String[] x = dataset.split("/");
			String dataName = x[x.length - 1].replace(".gfu", "");

			// run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_" +
			// dataName + ".txt", dataName);

			if (runRandomQueries)
				run_random(lvg, dataName);
			else
				run_for_proteins(lvg, dataName);

			// Config.MAX_RANKING_ENABLED = false;
			// Config.MAXBINARY_RANKING_ENABLED = true;

			// run(lvg, "/home/ksemer/workspaces/tkde_data/queries/queries_" +
			// dataName + ".txt", dataName);
			// if (runRandomQueries)
			// run_random(lvg, dataName);
			// else
			// run_for_proteins(lvg, dataName);
		}
	}

	private static void run(Graph lvg, String queryInput, String outputPr) throws Exception {
		BitSet iQ;
		Config.PATH_OUTPUT = out + outputPr + "/";

		iQ = new BitSet(Config.MAXIMUM_INTERVAL);
		iQ.set(0, Config.MAXIMUM_INTERVAL, true);

		String[] edge;
		PatternGraph pg = null;
		String line = null;
		boolean nodes = false;
		int sizeOfNodes = 0, id = 0, n1, n2;

		ExecutorService executor = Executors.newFixedThreadPool(Config.THREADS);

		BufferedReader br = new BufferedReader(new FileReader(Config.PATH_QUERY));

		while ((line = br.readLine()) != null) {

			if (line.contains("--") && pg != null) {

				if (Config.RUN_DURABLE_QUERIES) {

					if (Config.MAX_RANKING_ENABLED)
						executor.submit(setCallableDurQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAX_RANKING));

					if (Config.MAXBINARY_RANKING_ENABLED)
						executor.submit(setCallableDurQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAXBINARY_RANKING));

					if (Config.MIN_RANKING_ENABLED)
						executor.submit(setCallableDurQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MIN_RANKING));
				}

				if (Config.RUN_TOPK_QUERIES) {

					if (Config.MAX_RANKING_ENABLED)
						executor.submit(setCallableTopkQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAX_RANKING));

					if (Config.MAXBINARY_RANKING_ENABLED)
						executor.submit(setCallableTopkQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAXBINARY_RANKING));

					if (Config.MIN_RANKING_ENABLED)
						executor.submit(setCallableTopkQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MIN_RANKING));
				}
			} else if (line.contains("#")) {

				id = 0;
				nodes = true;
				sizeOfNodes = Integer.parseInt(br.readLine());
				line = line.trim().replace("#", "");
				pg = new PatternGraph(Integer.parseInt(line));
				continue;
			} else if (nodes) {

				pg.addNode(id++, Integer.parseInt(line.trim()));
				sizeOfNodes--;

				if (sizeOfNodes == 0) {
					nodes = false;
					continue;
				}
			} else {
				// edge
				edge = line.split("\\s+");

				// src node
				n1 = Integer.parseInt(edge[0]);

				// trg node
				n2 = Integer.parseInt(edge[1]);

				// src -> trg
				pg.addEdge(n1, n2);

				if (!Config.ISDIRECTED)
					// trg -> src
					pg.addEdge(n2, n1);
			}
		}
		br.close();

		executor.shutdown();

		while (!executor.isTerminated()) {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}

		lvg = null;
	}

	private static void run_random(Graph lvg, String outputPr) throws Exception {
		
		PatternGraph pg = null;
		BitSet iQ = new BitSet(Config.MAXIMUM_INTERVAL);
		Config.PATH_OUTPUT = out + outputPr + "/";

		iQ.set(0, Config.MAXIMUM_INTERVAL, true);

		ExecutorService executor = Executors.newFixedThreadPool(Config.THREADS);

		for (int i = 0; i < queries.size(); i++) {
			pg = queries.get(i);

			if (Config.RUN_DURABLE_QUERIES) {

				if (Config.MAX_RANKING_ENABLED)
					executor.submit(setCallableDurQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAX_RANKING));

				if (Config.MAXBINARY_RANKING_ENABLED)
					executor.submit(setCallableDurQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAXBINARY_RANKING));

				if (Config.MIN_RANKING_ENABLED)
					executor.submit(setCallableDurQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MIN_RANKING));
			}

			if (Config.RUN_TOPK_QUERIES) {

				if (Config.MAX_RANKING_ENABLED)
					executor.submit(setCallableTopkQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAX_RANKING));

				if (Config.MAXBINARY_RANKING_ENABLED)
					executor.submit(setCallableTopkQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MAXBINARY_RANKING));

				if (Config.MIN_RANKING_ENABLED)
					executor.submit(setCallableTopkQ(lvg, (PatternGraph) Storage.deepClone(pg), iQ, Config.MIN_RANKING));
			}
		}

		System.out.println("-----------------");
		executor.shutdown();

		while (!executor.isTerminated()) {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}

		lvg = null;
	}

	private static void run_for_proteins(Graph lvg, String outputPr) throws Exception {

		for (Entry<String, Integer> entry : LoaderProteins.labels.entrySet()) {
			int label = entry.getValue();

			ExecutorService executor = Executors.newCachedThreadPool();
			List<Callable<?>> callables = new ArrayList<>();

			for (int i = 2; i <= 6; i++) {
				BitSet iQ;
				Config.PATH_OUTPUT = out + outputPr + "/" + entry.getKey() + "-";
				iQ = new BitSet(Config.MAXIMUM_INTERVAL);
				iQ.set(0, Config.MAXIMUM_INTERVAL, true);

				PatternGraph pg = new PatternGraph(i);

				// create node with label
				for (int j = 0; j < i; j++) {
					pg.addNode(j, label);
				}

				// create edges
				for (int k = 0; k < i; k++) {
					for (int q = k + 1; q < i; q++) {
						pg.addEdge(k, q);
						pg.addEdge(q, k);
					}
				}

				if (Config.RUN_DURABLE_QUERIES) {

					if (Config.MAX_RANKING_ENABLED)
						callables.add(setCallableDurQ(lvg, pg, iQ, Config.MAX_RANKING));

					if (Config.MAXBINARY_RANKING_ENABLED)
						callables.add(setCallableDurQ(lvg, pg, iQ, Config.MAXBINARY_RANKING));

					if (Config.MIN_RANKING_ENABLED)
						callables.add(setCallableDurQ(lvg, pg, iQ, Config.MIN_RANKING));
				}

				if (Config.RUN_TOPK_QUERIES) {

					if (Config.MAX_RANKING_ENABLED)
						callables.add(setCallableTopkQ(lvg, pg, iQ, Config.MAX_RANKING));

					if (Config.MAXBINARY_RANKING_ENABLED)
						callables.add(setCallableTopkQ(lvg, pg, iQ, Config.MAXBINARY_RANKING));

					if (Config.MIN_RANKING_ENABLED)
						callables.add(setCallableTopkQ(lvg, pg, iQ, Config.MIN_RANKING));
				}
			}

			for (Callable<?> c : callables)
				executor.submit(c);

			executor.shutdown();

			while (!executor.isTerminated()) {
			}

			executor.shutdownNow();
			System.out.println("shutdown finished");
		}

		System.out.println("shutdown finished");
		lvg = null;

		Runtime runtime = Runtime.getRuntime();

		// Run the garbage collector
		runtime.gc();
	}

	/**
	 * Set callable Durable query execution
	 * 
	 * @param lvg
	 * @param pg
	 * @param iQ
	 * @param rankingStrategy
	 * @return
	 */
	private static Callable<?> setCallableDurQ(Graph lvg, PatternGraph pg, BitSet iQ, int rankingStrategy) {
		Callable<?> c = () -> {
			try {
				new DurableMatching(lvg, pg, iQ, Config.CONTIGUOUS_MATCHES, rankingStrategy);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			return true;
		};
		return c;
	}

	/**
	 * Set callable Topk Durable query execution
	 * 
	 * @param lvg
	 * @param pg
	 * @param iQ
	 * @param rankingStrategy
	 * @return
	 */
	private static Callable<?> setCallableTopkQ(Graph lvg, PatternGraph pg, BitSet iQ, int rankingStrategy) {
		Callable<?> c = () -> {
			try {
				new DurableTopkMatching(lvg, pg, iQ, Config.CONTIGUOUS_MATCHES, Config.K, rankingStrategy);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			return true;
		};
		return c;
	}

	private static Graph loadGraph() throws Exception {
		String dataset = Config.PATH_DATASET.toLowerCase();
		Graph lvg = null;

		// for dblp dataset
		if (dataset.contains("dblp")) {
			lvg = new LoaderDBLP().loadDataset();
		} else if (dataset.contains("yt")) { // youtube
			lvg = new LoaderYT().loadDataset();
		} else if (dataset.contains("random")) {
			Config.MAXIMUM_INTERVAL = randomDatasetInterval;
			lvg = new LoaderRandom().loadDataset();
		} else if (dataset.contains("wiki")) {
			lvg = new LoaderWikipedia().loadDataset();
		} else {
			// proteins
			lvg = new LoaderProteins().loadDataset();
		}

		return lvg;
	}
}
