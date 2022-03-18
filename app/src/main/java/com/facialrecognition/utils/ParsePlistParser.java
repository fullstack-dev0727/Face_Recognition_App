package com.facialrecognition.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParsePlistParser {

	// parse Plist and fill in arraylist
	public ArrayList<ProductInfo> parsePlist(String xml) {
		final ArrayList<ProductInfo> dataModels = new ArrayList<ProductInfo>();
		//Get the xml string from assets
		
        final Document doc = XMLfromString(xml);
        
        final NodeList nodes_array = doc.getElementsByTagName("array");
		
		//Fill in the list items from the XML document
		for ( int index = 0; index < nodes_array.getLength(); index++ ) {
			
			final Node node = nodes_array.item(index);
			
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				final Element e = (Element)nodes_array.item(index);
				
				final NodeList nodeKey = e.getElementsByTagName("key");
				final NodeList nodeString = e.getElementsByTagName("string");
				ProductInfo model = new ProductInfo();
				
				for (int i=0; i<nodeString.getLength(); i++) {
					
					final Element eleKey = (Element)nodeKey.item(i);
					final Element eleString = (Element)nodeString.item(i);
					
					
					if ( eleString != null ) {
						
						String strValue = getValue(eleString, "string");
					
						if(getValue(eleKey, "key").equals("name")) {
							model = new ProductInfo();
							model.name = strValue;
						} else if(getValue(eleKey, "key").equals("red")) {
							model.red = Integer.parseInt(strValue);
						} else if(getValue(eleKey, "key").equals("green")) {
							model.green = Integer.parseInt(strValue);
						} else if(getValue(eleKey, "key").equals("blue")) {
							model.blue = Integer.parseInt(strValue);
							dataModels.add(model);
						}
					}
				}
			}
		}
		return dataModels;
	}
	
	// Create xml document object from XML String
	private  Document XMLfromString(String xml) {
		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}

		return doc;
	}

	private   String getValue(Element item, String str) {
		if(item.getChildNodes().getLength() == 0)
			return null;
		return item.getChildNodes().item(0).getNodeValue();
		
	}
}