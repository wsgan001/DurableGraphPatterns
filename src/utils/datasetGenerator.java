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
import java.util.TreeMap;

public class datasetGenerator {
	private static String src = "/tempweb/files/yt_graph";
	private static Map<Integer, List<Integer>> nodes;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(src));
		String line = null;
		int n1, n2;
		
		nodes = new TreeMap<>();
		
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] token = line.split("\t");
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
		createDataset(20);
		createDataset(30);
		createDataset(40);
		createDataset(50);
		createDataset(60);
		createDataset(70);
		createDataset(80);
		createDataset(90);
		createDataset(100);
	}

	private static void createDataset(int numberOflabels) throws IOException {
		System.out.println("Running for number of labels: " + numberOflabels);
		
		FileWriter w = new FileWriter("/tempweb/files/yt_label_" + numberOflabels);
		zipf zipf = new zipf(numberOflabels, 1);
		int[] numberOfnodes = new int[numberOflabels + 1];
		// keeps all nodes
		List<Integer> arr = new ArrayList<>(nodes.keySet());

		// how many nodes should have this attribute
		for (int i = 1; i <= numberOflabels; i++)
			numberOfnodes[i] = (int) (zipf.getProbability(i) * nodes.size());
		
		// since we call it many times
		for (int key : nodes.keySet())
			nodes.get(key).clear();
				
		// ana poses fores na alazei
		int times = 4;
		for (int j = 0; j < times; j++) {

			// shuffle nodes
			Collections.shuffle(arr);
			
			int attr = 1, counter = 0;
			for (int i = 0; i < arr.size(); i++) {
				// get node arr.get(i) and add attribute attr
				nodes.get(arr.get(i)).add(attr);
				
				if (i == (numberOfnodes[attr] + counter - 1)) {
					counter+= numberOfnodes[attr];
					attr++;
				}
			}
		}
		
		for(Entry<Integer, List<Integer>> entry : nodes.entrySet()) {
			w.write(entry.getKey() + "\t");
			for (int k = 0; k < entry.getValue().size() - 1; k++)
				w.write(entry.getValue().get(k) + ",");
			
			w.write(entry.getValue().get(entry.getValue().size() - 1) + "\n");
		}

		w.close();
	}
}