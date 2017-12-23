package experiments;

import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import graph.version.Graph;
import graph.version.loader.LoaderDBLP;
import graph.version.loader.LoaderProteins;
import graph.version.loader.LoaderWikipedia;
import graph.version.loader.LoaderYT;
import system.Config;
import system.Query;

public class RunReal {

	public static void main(String[] args) throws Exception {
		Config.loadConfigs();

		run();

		Config.TINLA_ENABLED = true;
		Config.TINLA_R = 1;
		run();

		Config.TINLA_R = 2;
		run();

		Config.TINLA_ENABLED = false;
		Config.CTINLA_ENABLED = true;
		Config.CTINLA_R = 1;
		run();

		Config.CTINLA_R = 2;
		run();

		Config.CTINLA_ENABLED = false;
		Config.TIPLA_ENABLED = true;
		run();

		Config.BLOOM_ENABLED = true;
		run();

		Config.TIPLA_ENABLED = false;
		Config.TINLA_ENABLED = true;
		Config.TINLA_R = 1;
		run();

		Config.TINLA_R = 2;
		run();

		Config.TINLA_ENABLED = false;
		Config.CTINLA_ENABLED = true;
		Config.CTINLA_R = 1;
		run();

		Config.CTINLA_R = 2;
		run();		
	}

	private static void run() throws Exception {

		String dataset = Config.PATH_DATASET.toLowerCase();
		Graph lvg;

		// for dblp dataset
		if (dataset.contains("dblp"))
			lvg = new LoaderDBLP().loadDataset();
		// for yt dataset
		else if (dataset.contains("yt"))
			lvg = new LoaderYT().loadDataset();
		else if (dataset.contains("wiki"))
			lvg = new LoaderWikipedia().loadDataset();
		// for proteins
		else
			lvg = new LoaderProteins().loadDataset();

		if (Config.RUN_DURABLE_QUERIES || Config.RUN_TOPK_QUERIES) {

			BitSet iQ = new BitSet(Config.MAXIMUM_INTERVAL);
			iQ.set(0, Config.MAXIMUM_INTERVAL, true);

			ExecutorService executor = new Query(lvg, iQ).run();

			while (!executor.isTerminated()) {
				executor.awaitTermination(60, TimeUnit.SECONDS);
			}
		}

		lvg = null;
	}
}