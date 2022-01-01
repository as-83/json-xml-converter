package converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Converter {
    private final static String TEST_FILES_TEST_TXT = "./test.txt";
    private final static String TEST_TXT = "./test.txt";

    public static void convert(){
        Path path = Path.of(TEST_FILES_TEST_TXT);
        try {
            String fileContent = Files.readString(path);
            if (fileContent.startsWith("{")) {
                jsonToXmlDom(fileContent);
            } else if (fileContent.startsWith("<")) {
                xmlToJson();
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
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




    public static void jsonToXml() {
        JsonParser parser = new JsonParser();

        //String json = "{ \"f1\":\"Hello\",\"f2\":{\"f3\":\"World\"}}";
        String json = new Scanner(System.in).nextLine();

        JsonElement jsonTree = parser.parse(json);
        jsonToXml(jsonTree);

    }

    private static void jsonToXml(JsonElement jsonTree) {

        if (jsonTree.isJsonObject()) {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : set) {

                if (entry.getValue().isJsonObject()) {
                    System.out.print("<" + entry.getKey());
                    jsonToXml(entry.getValue());
                    System.out.print("\n</" + entry.getKey() + ">");
                } else if (entry.getValue().isJsonNull()) {
                    if (!entry.getKey().startsWith("#")) {
                        System.out.print("<" + entry.getKey());
                    }
                    System.out.print("/>");
                } else if (entry.getValue().isJsonPrimitive()) {
                    if (entry.getKey().startsWith("@")) {
                        System.out.print(" " + entry.getKey().substring(1) + " = " + entry.getValue() + "");
                    } else if (entry.getKey().startsWith("#")) {
                        System.out.print(">" + entry.getValue().toString().replaceAll("\"", "") );
                    } else {
                        System.out.print("\n<" + entry.getKey() + ">" + entry.getValue().toString().replaceAll("\"", "") + "</" + entry.getKey() + ">");
                    }

                }
            }

        }
    }

    //// https://mvnrepository.com/artifact/com.google.code.gson/gson
    //implementation group: 'com.google.code.gson', name: 'gson', version: '2.8.5'
    public static void jsonToXmlDom(String json) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        JsonParser parser = new JsonParser();

        JsonElement jsonTree = parser.parse(json);
        jsonToXmlDom(jsonTree, doc, null);

        toXmlFile(doc);


    }

    private static void jsonToXmlDom(JsonElement jsonTree, Document doc, Element parentNode) {

        if (jsonTree.isJsonObject()) {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
            Element newNode;

            for (Map.Entry<String, JsonElement> entry : set) {
                if (parentNode == null) {
                    parentNode = newNode = doc.createElementNS("", entry.getKey());
                    // добавляем корневой элемент в объект Document
                    doc.appendChild(parentNode);
                } else {
                    newNode = doc.createElement(entry.getKey().replaceAll("[#@]", ""));
                }

                if (entry.getValue().isJsonObject()) {
                    if (newNode != parentNode) {
                        parentNode.appendChild(newNode);
                    }
                    jsonToXmlDom(entry.getValue(), doc, newNode);
                } else if (entry.getValue().isJsonPrimitive()) {
                    if (entry.getKey().startsWith("@")) {
                        parentNode.setAttribute(entry.getKey().replaceAll("[#@]", ""), entry.getValue().toString().replaceAll("[#@\"]", ""));
                    } else if (entry.getKey().startsWith("#")) {
                        parentNode.appendChild(doc.createTextNode(entry.getValue().toString().replaceAll("\"", "")));
                    } else {
                        if (parentNode != newNode) {
                            parentNode.appendChild(newNode);
                            newNode.appendChild(doc.createTextNode(entry.getValue().toString().replaceAll("\"", "")));
                        } else {
                            parentNode.appendChild(doc.createTextNode(entry.getValue().toString().replaceAll("\"", "")));
                        }

                    }

                }
            }

        }
    }

    private static void toXmlFile(Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            //for omitting xml declaration
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // for formatted output
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(TEST_FILES_TEST_TXT));

            //writing xml dom to file and console
            transformer.transform(source, console);
           // transformer.transform(source, file);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void printXmlContent() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //Parsing the file and getting DOM model of the xml
        Document document = documentBuilder.parse(TEST_FILES_TEST_TXT);
        // Getting root element
        Node root = document.getDocumentElement();

        printXmlContent(root);
    }

    private static void printXmlContent(Node node) {
        if (node.getNodeType() != Node.TEXT_NODE) {
            NodeList childNodes = node.getChildNodes();
            NamedNodeMap attributes = node.getAttributes();
            System.out.print("\n\nElement:\npath = ");

            printPath(node);
            if (childNodes != null) {
                if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
                    System.out.println("value = \"" + childNodes.item(0).getTextContent() + "\"");
                } else if (childNodes.getLength() == 0) {
                    System.out.println("value = null");
                }
            }
            if (attributes.getLength() > 0) {
                System.out.println("attributes:");
                for (int i = 0; i < attributes.getLength(); i++) {
                    System.out.println(attributes.item(i).getNodeName() + " = \"" + attributes.item(i).getTextContent() + "\"");
                }
            }

            for (int i = 0; i < childNodes.getLength(); i++) {
                printXmlContent(childNodes.item(i));
            }
        }



    }

    private static void printPath(Node node) {

        Deque<String> path = getPath(node);
        while (!path.isEmpty()) {
            System.out.print(path.pop());
            if (path.size() > 0) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }

    private static Deque<String> getPath(Node node) {
        Deque<String> path = new ArrayDeque<>();
        Node parent;
        do {
            if (!node.getNodeName().equals("#document")) {
                path.push(node.getNodeName());
            }
            node = node.getParentNode();
        } while (node != null) ;
        return path;
    }

}
