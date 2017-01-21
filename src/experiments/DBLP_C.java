package experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algorithm.DurableMatching;
import algorithm.DurableTopkMatching;
import graph.pattern.PatternGraph;
import graph.version.Graph;
import graph.version.loader.LoaderDBLP;
import system.Config;

/**
 * Experiment for DBLP_C
 * 
 * @author ksemer
 */
public class DBLP_C {

	// only used in random queries and the division with randomIterations should
	// be zero
	private static boolean runTopk = false;
	private static boolean runMost = true;

	private final static Set<Integer> LABELS = new HashSet<Integer>(Arrays.asList(1664, 5887, 6066, 2770, 5292, 7523,
			5207, 163, 5668, 4640, 4169, 4654, 4806, 1945, 5200, 1872, 7741, 1390));

	public static void main(String[] args) throws Exception {
		Config.loadConfigs();
		BitSet iQ = new BitSet();
		Graph lvg = loadGraph();
		iQ.set(0, Config.MAXIMUM_INTERVAL);

		for (int l : LABELS) {
			List<Callable<?>> callables = new ArrayList<>();
			ExecutorService executor = Executors.newCachedThreadPool();

			for (int i = 2; i <= 6; i++) {
				String x = l + "0000" + i;
				PatternGraph pg = new PatternGraph(Integer.parseInt(x));

				for (int j = 0; j < i; j++)
					pg.addNode(j, l);

				for (int j = 0; j < i; j++) {
					for (int k = j + 1; k < i; k++) {
						pg.addEdge(j, k);
						pg.addEdge(k, j);
					}
				}

				if (runMost)
					callables.add(setCallableDurQ(lvg, pg, iQ, Config.MAX_RANKING));

				if (runTopk)
					callables.add(setCallableTopkQ(lvg, pg, iQ, Config.MAX_RANKING));
			}

			for (Callable<?> c : callables)
				executor.submit(c);

			executor.shutdown();

			while (!executor.isTerminated()) {
			}
		}
	}

	private static Graph loadGraph() throws Exception {
		String dataset = Config.PATH_DATASET.toLowerCase();
		Graph lvg = null;

		// for dblp dataset
		if (dataset.contains("dblp")) {
			Config.MAXIMUM_INTERVAL = 58;
			lvg = new LoaderDBLP().loadDataset();
		}

		return lvg;
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
}