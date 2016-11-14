package utils.dblp;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * dplb parser
 * @author ksemer
 * -Djdk.xml.entityExpansionLimit=0
 */
public class dblpXMLParser extends DefaultHandler
{
	private static String inputPath = "files/dblp.xml";
	private String outputPath = "files/dblp_tmp.txt";
	private String temp;		 
	private boolean get = false, getName = false;
	private List<String> list = new ArrayList<String>();	
	
	/**
	 * Every time the parser encounters the beginning of a new element,
	 * it calls this method, which resets the string buffer
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	{		 		
		temp = "";
		
		if (qName.equalsIgnoreCase("inproceedings"))
			get = true;
		
		if (qName.equalsIgnoreCase("author"))
			getName = true;
	}
		
	/**
	 * When the parser encounters plain text (not XML  elements),
	 * it calls(this method, which accumulates them in a string buffer
	 */
	public void characters(char buffer[], int start, int length) throws SAXException
	{
		if (getName) {
			temp+= new String(buffer, start, length);
		} else {
			temp = new String(buffer, start, length);
		}
	} 
	
    /**
     * When the parser encounters the end of an element, it calls this method
     */
	public void endElement(String uri, String localName, String qName)
	{
		if (get)
		{
			if (qName.equalsIgnoreCase("author")) {
				list.add("Author: " + temp + "\n");
				getName = false;
			}
			else if (qName.equalsIgnoreCase("title"))
				list.add("Title: " + temp + "\n");
			else if (qName.equalsIgnoreCase("pages"))
				list.add("Pages: " + temp  + "\n");
			else if (qName.equalsIgnoreCase("year"))
				list.add("Year: " + temp + "\n");
			else if (qName.equalsIgnoreCase("booktitle"))
				list.add("Booktitle: " + temp + "\n");
			if (qName.equalsIgnoreCase("inproceedings"))
				get = false;
		}
	}
	
	/**
	 * Write the list's data to a file
	 * @throws IOException
	 */
	public void write() throws IOException
	{
		// write author \t his id
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "UTF-8"));

		for (String l : list)
		{
			w.write(l);
		}
		w.close();
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException 
	{ 
		SAXParserFactory spfac = SAXParserFactory.newInstance();

        //Now use the parser factory to create a SAXParser object
        SAXParser sp = spfac.newSAXParser();

        //Create an instance of this class; it defines all the handler methods
        dblpXMLParser handler = new dblpXMLParser();

        //Finally, tell the parser to parse the input and notify the handler
        sp.parse(inputPath, handler); 
        
        handler.write();
	}
}
