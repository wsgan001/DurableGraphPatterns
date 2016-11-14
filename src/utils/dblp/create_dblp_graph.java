package utils.dblp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creates the DBLP graph 
 * author1 \t author 2 \t attributes \t time
 * @author ksemer
 */
public class create_dblp_graph
{
	private String PATH_DBLP_INPUT = "files/dblp_tmp.txt";
	private String PATH_DBLP_AUTHORS_MAP = "files/DBLP_Authors_MAP";
	private String PATH_ATTRIBUTE_MAP = "files/DBLP_Label_MAP";
	private String PATH_DBLP_GRAPH = "files/DBLP_Graph";

	// map attribute to id
	private HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	 // is used to replace authors names with a unique id.
	private HashMap<String, Integer> allAuthors = new HashMap<String, Integer>();
//	// keeps for each author his attributes
//	private HashMap<Integer, Set<Integer>> authorsAttributes = new HashMap<Integer, Set<Integer>>();
	
	public create_dblp_graph() throws IOException
	{
		BufferedReader input = new BufferedReader(new FileReader(this.PATH_DBLP_INPUT));
		FileWriter w_graph = new FileWriter(this.PATH_DBLP_GRAPH, false);
		BufferedWriter w_authors = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.PATH_DBLP_AUTHORS_MAP), "UTF-8")); // write author \t his id
//		FileWriter w_author_attributes = new FileWriter(this.PATH_DBLP_GRAPH_ATTRIBUTES, false); // write author \t attribute1,attribute2,..., \t time

		// create attributes map
		loadAttributesMap();

		int year = 0, id = 0;
		boolean newArticle = false;
		@SuppressWarnings("unused")
		String line = null, booktitle = null, title = null, attributes;
//		int authorAttributes;
		List<String> authors = new ArrayList<String>();

    	while ((line = input.readLine()) != null)
    	{
    		if (newArticle)
    		{
    			if (title != null) 
    			{
    				// get attributes from title
					attributes = getAttributes(title);
					
	    			// Write the authors
	    			for (int i = 0; i < authors.size(); i++)
	    			{
	//    				// get author's attributes
	//    				authorAttributes = this.authorsAttributes.get(allAuthors.get(authors.get(i)));
	    				
	//    				// update author's attributes
	//    				if (authorAttributes != null)
	//    					authorAttributes.addAll(attributes);
	//    				else
	//    				{
	//    					authorAttributes = new HashSet<Integer>();
	//    					authorAttributes.addAll(attributes);
	//    					this.authorsAttributes.put(allAuthors.get(authors.get(i)), authorAttributes);
	//    				}
	    				
	    				for (int j = i+1; j < authors.size(); j++)
	    				{
	    					// write graph edge
	    					w_graph.write(allAuthors.get(authors.get(i)) + "\t" + allAuthors.get(authors.get(j)) + "\t" + attributes + "\t" + year + "\t" + /*title +*/"\n");
	    				}
	    			}
    			}
    			authors.clear();
    			newArticle = false;
    			title = null;
    			booktitle = null;
    		}
    		else
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
    					id++;
    				}
    			}
    			else if (line.contains("Title:"))
    			{
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
    				booktitle = line.replace("Booktitle: ", "");
    				newArticle = true;
    			}
    		}
    	}
    	input.close();
    	w_graph.close();
    	w_authors.close();
    	
//    	for (int author : this.authorsAttributes.keySet())
//    	{
//    		w_author_attributes.write(author + "\t");
//    		
//    		for (int attr : this.authorsAttributes.get(author))
//    		{
//    			w_author_attributes.write(attr + ",");
//    		}
//    		w_author_attributes.write("\n");
//    	}
//    	w_author_attributes.close();
	}

	/**
	 * 
	 * @param title
	 * @return
	 */
	private String getAttributes(String title) 
	{
		String[] token = title.split("\\s+");
		String attributes = "", attr;
		Set<Integer> attrs = new HashSet<>();
		
		for (int i = 0; i < token.length; i++)
		{
			attr = token[i].toLowerCase();
			
			if (this.attributes.containsKey(attr))
				attrs.add(this.attributes.get(attr));
		}
		
		for (int a : attrs)
		{
			if (attributes.isEmpty())
				attributes+= a;
			else
				attributes+= "," + a;
		}

		// return no attribue found
		if (attributes.isEmpty())
			return "naf";
		
		return attributes;
	}

	/**
	 * 
	 */
	private void loadAttributesMap() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(this.PATH_ATTRIBUTE_MAP));
		String line = "";
		String[] token;
		
		while ((line = br.readLine()) != null)
		{
			token = line.split("\\s");
			this.attributes.put(token[0].toLowerCase(), Integer.parseInt(token[1]));
		}
		br.close();
	}

	public static void main(String[] args) throws IOException { new create_dblp_graph(); }
}
