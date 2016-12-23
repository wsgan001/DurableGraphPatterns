package utils;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import graph.pattern.PatternGraph;
import graph.pattern.PatternNode;
import graph.version.Edge;
import graph.version.Node;
import system.Config;

/**
 * Pattern graph query generator
 * 
 * @author ksemer
 */
public class QueryGenerator {
	// ===============================================
	private static Map<Node, Integer> labels;
	private static int patternGraphID = 0;
	public static Set<Node> hasBeenVisited;
	// ===============================================

	/**
	 * DFS traversal
	 * 
	 * @param src
	 * @param size
	 * @return
	 */
	public static boolean dfs(Node src, int size) {
		hasBeenVisited = new HashSet<Node>();
		Stack<Node> st = new Stack<Node>();
		st.push(src);
		labels = new HashMap<>();
		labels.put(src, getMaxLabel(src));

		while (!st.isEmpty()) {
			Node v = st.pop();

			if (!hasBeenVisited.contains(v)) {
				hasBeenVisited.add(v);

				for (Edge w : v.getAdjacency()) {

					if (!hasBeenVisited.contains(w.getTarget())) {
						labels.put(w.getTarget(), getMaxLabel(w.getTarget()));
						st.push(w.getTarget());
					}
				}
			}

			if (hasBeenVisited.size() == size)
				break;
		}

		if (hasBeenVisited.size() < size)
			return false;

		return true;
	}

	/**
	 * Get label with the highest duration
	 * 
	 * @param src
	 * @return
	 */
	private static int getMaxLabel(Node src) {
		int max = 0, label = 0;
		BitSet lifespan;

		for (Entry<Integer, BitSet> entry : src.getLabels().entrySet()) {
			lifespan = entry.getValue();

			if (lifespan.cardinality() > max) {
				max = lifespan.cardinality();
				label = entry.getKey();
			}
		}

		return label;
	}

	/**
	 * Return query as a pattern graph
	 * 
	 * @param size
	 * @return
	 */
	public static PatternGraph getQuery(int size) {
		PatternGraph pg = new PatternGraph(patternGraphID++);
		PatternNode pn1, pn2;
		int count = 0;
		Map<Node, Integer> map = new HashMap<>();

		for (Node n : hasBeenVisited) {
			pg.addNode(count, labels.get(n));
			map.put(n, count);
			count++;
		}

		for (Node n : hasBeenVisited) {

			pn1 = pg.getNode(map.get(n));

			for (Node trg : hasBeenVisited) {
				if (n.getEdge(trg) != null) {
					pn2 = pg.getNode(map.get(trg));

					pn1.addEdge(pn2);

					if (!Config.ISDIRECTED)
						pn2.addEdge(pn1);
				}
			}
		}

		return pg;
	}
}