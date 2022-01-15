package converter.converters;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmlToJsonWithDocumentBuilderConverter {
    private final static String TEST_FILES_TEST_TXT = "./output.txt";
    private final static String TEST_TXT = "./test.txt";

    public static void convert() throws IOException, SAXException, ParserConfigurationException {
        Path path = Path.of(TEST_FILES_TEST_TXT);
            String fileContent = Files.readString(path);
            xmlToJson();
    }

    public static void xmlToJson() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //Parsing the file and getting DOM model of the xml
        Document document = documentBuilder.parse(TEST_FILES_TEST_TXT);
        // Getting root element
        Node root = document.getDocumentElement();

        String json = xmlToJson(root);
        writeJsonToFile(json);
    }

    private static void writeJsonToFile(String json) {
        System.out.println(json);
        /*try {
            PrintWriter printWriter = new PrintWriter(TEST_FILES_TEST_TXT);
            printWriter.print(json);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    //Recursively
    private static String xmlToJson(Node node) {

        NodeList childNodes = node.getChildNodes();
        StringBuilder stringBuilder = new StringBuilder();
        NamedNodeMap attributes = node.getAttributes();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            if (node.getPreviousSibling() == null) {
                stringBuilder.append("{");
            }
            stringBuilder.append("\"" + node.getNodeName() + "\":");
            if (node.hasAttributes()) {
                stringBuilder.append("{");
                stringBuilder.append(getAttributes(attributes));
                stringBuilder.append("\"#" + node.getNodeName() + "\":");
            }
            if (childNodes.getLength() == 0) {
                stringBuilder.append("null}");
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            stringBuilder.append("\"" + node.getTextContent() + "\"");
            if (node.getParentNode().getNextSibling() != null && !node.getParentNode().hasAttributes()) {
                stringBuilder.append(", ");
            }

        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            stringBuilder.append(xmlToJson(childNodes.item(i)));
        }


        if ((node.getNodeType() == Node.ELEMENT_NODE || node.getParentNode().hasAttributes()) &&
                !(node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE && node.getNextSibling() != null)) {
            stringBuilder.append("}");
            if (node.getNextSibling() != null) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }


    private static String getAttributes(NamedNodeMap attributes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < attributes.getLength(); i++) {
            stringBuilder.append("\"@" + attributes.item(i).getNodeName() + "\": \"" +
                    attributes.item(i).getTextContent() + "\"");
            if (i < attributes.getLength()) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }




   }
