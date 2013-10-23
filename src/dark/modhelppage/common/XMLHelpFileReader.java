package dark.modhelppage.common;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHelpFileReader
{

    public void loadFromFile(File file)
    {
        if (file != null)
        {
            try
            {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();

                System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nList = doc.getElementsByTagName("Object");

                System.out.println("----------------------------");

                for (int temp = 0; temp < nList.getLength(); temp++)
                {

                    Node nNode = nList.item(temp);

                    System.out.println("\nCurrent Element :" + nNode.getNodeName());

                    if (nNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element eElement = (Element) nNode;

                        String version = eElement.getAttribute("version");
                        String type = eElement.getElementsByTagName("type").item(0).getTextContent();
                        String name = eElement.getElementsByTagName("itemName").item(0).getTextContent();
                        String desciption = eElement.getElementsByTagName("description").item(0).getTextContent();
                        String uses = eElement.getElementsByTagName("uses").item(0).getTextContent();
                        String tips = eElement.getElementsByTagName("tips").item(0).getTextContent();

                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class ObjectHelpInformation
    {
        String name;


    }
}
