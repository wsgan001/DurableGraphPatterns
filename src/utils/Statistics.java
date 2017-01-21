package utils;

import graph.version.Graph;
import graph.version.Node;
import graph.version.loader.LoaderDBLP;
import graph.version.loader.LoaderProteins;
import graph.version.loader.LoaderYT;
import system.Config;

public class Statistics {
	public static String dataset = "";

	public static void main(String[] args) throws Exception {

		Config.loadConfigs();

		Config.STORE_OBJECT = false;
		Config.LOAD_OBJECT = false;
		Config.TINLA_ENABLED = false;
		Config.TIPLA_ENABLED = false;
		Config.CTINLA_ENABLED = false;

		Config.PATH_DATASET = "/home/ksemer/workspaces/tkde_data/protein/pcms/PCMS.gfu";

		String dataset = Config.PATH_DATASET.toLowerCase();
		Graph lvg;

		// for dblp dataset
		if (dataset.contains("dblp")) {
			lvg = new LoaderDBLP().loadDataset();
		}
		// for yt dataset
		else if (dataset.contains("yt"))
			lvg = new LoaderYT().loadDataset();
		else // for proteins
			lvg = new LoaderProteins().loadDataset();

		System.out.println("Nodes: " + lvg.size());
		int edges = 0;

		for (Node n : lvg.getNodes()) {
			edges += n.getAdjacency().size();
		}

		if (!Config.ISDIRECTED)
			edges /= 2;

		System.out.println("Edges: " + edges);
	}
}