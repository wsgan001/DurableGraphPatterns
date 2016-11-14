package utils.dblp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Creates the DBLP graph 
 * Map authors to ids
 * For each author the # of publications per year
 * @author ksemer
 */
public class Parser
{
	public static final int TOTAL_YEARS = 57;
	private String PATH_DBLP_INPUT = "files/dblp_tmp.txt";
	private String PATH_DBLP_AUTHORS_MAP = "files/DBLP_Authors_MAP";
	private String PATH_DBLP_GRAPH = "files/DBLP_Graph";
	private String PATH_DBLP_GRAPH_ATTRIBUTES = "files/DBLP_Authors_Attr";
	// is used to replace authors names with a unique id.
	private HashMap<String, Integer> allAuthors = new HashMap<String, Integer>();
	private HashMap<Integer, Node> authors = new HashMap<Integer, Node>();
	
	public Parser() throws IOException
	{
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(this.PATH_DBLP_INPUT), "UTF-8"));
		FileWriter w_graph = new FileWriter(this.PATH_DBLP_GRAPH, false);
		BufferedWriter w_authors = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(this.PATH_DBLP_AUTHORS_MAP), "UTF-8")); // write author \t his id

		int year = 0, id = 0;
		String line = null, title = null; //booktitle = null;
		List<String> authors = new ArrayList<String>();
		
    	while ((line = input.readLine()) != null)
    	{
    		if (line.contains("Author:"))
    		{
    			String author = line.replace("Author: ", "");
    			authors.add(author);
    				
    			// map author to a unique id
    			if (!allAuthors.containsKey(author))
    			{
    				allAuthors.put(line.replace("Author: ", ""), id);
    				w_authors.write(line.replace("Author: ", "") + "\t" + id + "\n");
    				this.authors.put(id, new Node());
    				id++;
    			}
    		}
    		else if (line.contains("Title:"))
    		{
    			if (line.contains("Identifying Converging"))
    				System.out.println(authors);
    			
    			title = line.replace("Title:", "");
    			title = title.replace(".", "");
    			title = title.replace(":", "");
    			title = title.replace("(", "");
    			title = title.replace(")", "");
    			title = title.replace("'", "");
    			title = title.replace(",", "");
    			title = title.replace("\"", "");
    		}
    		else if (line.contains("Year:") && !line.contains("Title:"))
    		{
    			year = Integer.parseInt(line.replace("Year: ", ""));
    		}
    		else if (line.contains("Booktitle:"))
    		{
 //   			booktitle = line.replace("Booktitle: ", "");
    			
    			// Write the authors
    			for (int i = 0; i < authors.size(); i++)
    			{	
    				for (int j = i+1; j < authors.size(); j++)
    				{
    					// write graph edge
    					w_graph.write(allAuthors.get(authors.get(i)) + "\t" + allAuthors.get(authors.get(j)) + "\t" + year + /*title +*/"\n");
    					this.authors.get(allAuthors.get(authors.get(i))).increase(year);
    				}
    			}
    			authors.clear();
    		}
    	}
    	input.close();
    	w_graph.close();
    	w_authors.close();
    	
		FileWriter w_author_attr = new FileWriter(this.PATH_DBLP_GRAPH_ATTRIBUTES, false); // write author \t attributeValue,attributeValue each one is per year

		for (Entry<Integer, Node> entry : this.authors.entrySet())
		{
			w_author_attr.write(entry.getKey() + "|");
			Node node = entry.getValue();
			int array[] = node.getStatus();
			
			for (int i = 0; i < array.length - 1; i++)
			{
				w_author_attr.write(array[i] + ",");
			}
			w_author_attr.write(array[array.length - 1] + "\n");
		}
		w_author_attr.close();
	}

	public static void main(String[] args) throws IOException { new Parser(); }
}
