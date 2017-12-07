package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.zipf;

import java.util.TreeMap;

/**
 * Generate labels for whole interval for the given dataset
 * 
 * @author ksemer
 *
 */
public class StaticLabelGenerator {
	// input graph
	private static String dataset = "wiki-graph";

	// path for labels
	private static String labels_output = "wiki_label_";

	private static Map<Integer, List<Integer>> nodes;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dataset));
		String line = null;
		int n1, n2;

		nodes = new TreeMap<>();

		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] token = line.split("\\s++");
			n1 = Integer.parseInt(token[0]);
			n2 = Integer.parseInt(token[1]);

			if (!nodes.containsKey(n1))
				nodes.put(n1, new ArrayList<>());

			if (!nodes.containsKey(n2))
				nodes.put(n2, new ArrayList<>());
		}
		br.close();

		System.out.println("Total nodes: " + nodes.size());

		createDataset(10);
	}

	/**
	 * Create sizeOflabels for the dataset interval
	 * 
	 * @param numberOflabels
	 * @throws IOException
	 */
	private static void createDataset(int sizeOflabels) throws IOException {
		System.out.println("Running size of labels: " + sizeOflabels);

		FileWriter w = new FileWriter(labels_output + sizeOflabels);
		zipf zipf = new zipf(sizeOflabels, 1);
		int[] numberOfnodes = new int[sizeOflabels + 1];

		// keeps all nodes
		List<Integer> arr = new ArrayList<>(nodes.keySet());

		// how many nodes should have this attribute
		for (int i = 1; i <= sizeOflabels; i++) {
			numberOfnodes[i] = (int) (zipf.getProbability(i) * nodes.size());
		}

		// since we call it many times
		for (int key : nodes.keySet())
			nodes.get(key).clear();

		// shuffle nodes
		Collections.shuffle(arr);

		int attr = 1, counter = 0;
		for (int i = 0; i < arr.size(); i++) {

			// get node arr.get(i) and add attribute attr
			nodes.get(arr.get(i)).add(attr);

			if (i == (numberOfnodes[attr] + counter - 1)) {
				counter += numberOfnodes[attr];
				attr++;
			}
		}

		for (Entry<Integer, List<Integer>> entry : nodes.entrySet()) {

			w.write(entry.getKey() + "\t" + entry.getValue().get(0) + "\n");
		}

		w.close();
	}
}