package utils;

import graph.version.Graph;
import graph.version.Node;
import graph.version.loader.LoaderDBLP;
import graph.version.loader.LoaderProteins;
import graph.version.loader.LoaderYT;
import system.Config;

/**
 * Returns the number of nodes and the edges of the given graph
 * @author ksemer
 *
 */
public class Statistics {

	public static void main(String[] args) throws Exception {

		Config.loadConfigs();

		Config.TINLA_ENABLED = false;
		Config.TIPLA_ENABLED = false;
		Config.CTINLA_ENABLED = false;

		String dataset = Config.PATH_DATASET.toLowerCase();
		Graph lvg;

		if (dataset.contains("dblp")) // for dblp dataset
			lvg = new LoaderDBLP().loadDataset();
		else if (dataset.contains("yt")) // for yt dataset
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