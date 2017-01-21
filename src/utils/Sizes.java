package utils;

import graph.version.loader.LoaderDBLP;
import graph.version.loader.LoaderProteins;
import graph.version.loader.LoaderYT;
import system.Config;

public class Sizes {
	private static boolean VILA = false;
	private static boolean TINLA = false;
	private static boolean CTINLA = true;
	private static boolean TIPLA = true;

	private static void loadGraph() throws Exception {
		String dataset = Config.PATH_DATASET.toLowerCase();

		// for dblp dataset
		if (dataset.contains("dblp")) {
			Config.MAXIMUM_INTERVAL = 56;
			System.out.println("Nodes: " + new LoaderDBLP().loadDataset().size());
		} else if (dataset.contains("yt")) { // youtube
			Config.MAXIMUM_INTERVAL = 37;
			new LoaderYT().loadDataset();
		} else {
			// proteins
			new LoaderProteins().loadDataset();
		}

	}

	public static void main(String[] args) throws Exception {
		Config.loadConfigs();

		if (VILA) {
			Config.TINLA_ENABLED = false;
			Config.CTINLA_ENABLED = false;
			Config.TIPLA_ENABLED = false;
			loadGraph();
		}

		if (TINLA) {
			Config.TINLA_ENABLED = true;
			Config.CTINLA_ENABLED = false;
			Config.TIPLA_ENABLED = false;
			Config.TINLA_R = 2;
			loadGraph();
		}

		if (CTINLA) {
			Config.TINLA_ENABLED = false;
			Config.CTINLA_ENABLED = true;
			Config.TIPLA_ENABLED = false;
			Config.CTINLA_R = 2;
			loadGraph();
		}

		if (TIPLA) {
			Config.TINLA_ENABLED = false;
			Config.CTINLA_ENABLED = false;
			Config.TIPLA_ENABLED = true;
			loadGraph();
		}
	}
}