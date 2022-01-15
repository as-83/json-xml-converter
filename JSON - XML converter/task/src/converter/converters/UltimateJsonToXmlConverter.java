package converter.converters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;
import java.util.Set;

public class UltimateJsonToXmlConverter {
    private final static String TEST_FILES_TEST_TXT = "./output.txt";
    private final static String TEST_TXT = "./test.txt";

    public static void jsonToXmlDom(String json) throws ParserConfigurationException {
        //getting document for xml dom representation
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        //getting json tree
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);

        //calling simple parser for correct jsons with correct attributes
        // jsonToXmlDom(jsonTree, doc, null);
        jsonToXmlDomUltimate(jsonTree, doc, null);

        toXmlFile(doc);//writing xml to file and console


    }

    /*
        Same as jsonToXmlDom(JsonElement jsonTree, Document xmlDom, Element parentNode) +
        special conditions of hyperskill.org for 4th stage:

        1. The object has a key with the same name as the object, with a # symbol in front of it.
         example, if the key of the object is "obj" then the value of this object has to be inside
         "#obj" key inside this object. Note that if such a key does not exist in the object, this
         object should not be considered a single XML object with attributes.

        2. The value object contains the #value key and all other attributes begin with the @ symbol
         and are longer than 1 character. If this object has at least one key that equals @ or does
         not start with @ (except #value), then this object should not be considered a single XML
         object with attributes.

        3. If the value of any key starting with @ is not a number, string or null (in other words,
         it will be an object starting with "{"), then this object cannot be an attribute of a single
         XML object and the @ symbol should be removed from this key, and thus the object cannot be
         considered a single XML object.

        The object without attributes would look like a standard JSON object, without @ or # symbols
         around. In particular, all wrong cases of creating an object with attributes result in a
         standard object after certain manipulations. For example inner1, inner2 and inner3 from the
         first example are objects without attributes with no errors in construction.
     */
    private static void jsonToXmlDomUltimate(JsonElement jsonTree, Document xmlDom, Element parentNode) {

        if (jsonTree.isJsonObject()) {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> jsonObjectEntrySet = jsonObject.entrySet();
            Element newXmlNode;

            //checking the attributes correctness
            Element finalParentNode = parentNode;//final copy for lambda
            boolean incorrectAttributes = jsonObjectEntrySet.stream()
                    .anyMatch(e -> e.getKey().startsWith("[^@#]") || e.getKey().trim().length() < 2 ) ||
                    jsonObjectEntrySet.stream().filter(e -> e.getKey().startsWith("@"))
                    .anyMatch(e -> e.getValue().isJsonObject()) ||
                    jsonObjectEntrySet.stream().filter(e -> e.getKey().startsWith("#")).count() != 1 ||
                    jsonObjectEntrySet.stream().anyMatch(e -> e.getKey().startsWith("#")
                            && !e.getKey().replace("#", "")
                            .equals(finalParentNode.getNodeName()));
            System.out.println(jsonObjectEntrySet);
            boolean removed = jsonObjectEntrySet.removeIf( e -> e.getKey().isBlank() ||
                    ( (e.getKey().startsWith("@") || (e.getKey().startsWith("#"))) && e.getKey().length() < 2) );
            //System.out.println(jsonObjectEntrySet);
            //System.out.println("Were removed from jsonObjectEntrySet = " + removed );
            //System.out.println("------------------------------------");
            //if something wrong with attributes names or values in json
            //or there are no attributes at all then add them as nodes not attributes
            if (incorrectAttributes) {
                for (Map.Entry<String, JsonElement> jsonEntry : jsonObjectEntrySet) {
                    //if first node
                    if (parentNode == null) {
                        parentNode = newXmlNode = xmlDom.createElementNS("", jsonEntry.getKey());
                        // add node to Document
                        xmlDom.appendChild(parentNode);
                    } else {
                        newXmlNode = xmlDom.createElement(jsonEntry.getKey().replaceAll("[#@]", ""));
                    }

                    if (jsonEntry.getValue().isJsonObject()) {
                        //if not first node
                        if (newXmlNode != parentNode) {
                            //add child
                            parentNode.appendChild(newXmlNode);
                        }
                        //recursive call for next jsonEntry
                        jsonToXmlDomUltimate(jsonEntry.getValue(), xmlDom, newXmlNode);
                    } else if (jsonEntry.getValue().isJsonPrimitive()) {
                        String textValue = jsonEntry.getValue().toString();

                        Text text = xmlDom.createTextNode(textValue.replaceAll("\"", ""));
                        if (textValue.replaceAll("\"", "").length() ==0) {
                            text = xmlDom.createTextNode(" ");

                        }
                        if (parentNode != newXmlNode) {
                            parentNode.appendChild(newXmlNode);
                            newXmlNode.appendChild(text);

                        } else {
                            parentNode.appendChild(text);
                        }

                    } else if (jsonEntry.getValue().isJsonNull()) {
                        if (parentNode != newXmlNode) {
                            parentNode.appendChild(newXmlNode);
                        }
                    }
                }
            } else { //if everything OK with attributes names and values then add attributes to current node
                for (Map.Entry<String, JsonElement> jsonEntry : jsonObjectEntrySet) {

                    if (jsonEntry.getValue().isJsonObject()) {
                        newXmlNode = xmlDom.createElement(jsonEntry.getKey().replaceAll("[#@]", ""));
                        parentNode.appendChild(newXmlNode);
                        jsonToXmlDomUltimate(jsonEntry.getValue(), xmlDom, newXmlNode);
                    } else {
                        if (jsonEntry.getKey().startsWith("@")) {
                            parentNode.setAttribute(jsonEntry.getKey().replaceAll("[#@]", ""), jsonEntry.getValue().toString().replaceAll("[#@\"]", ""));
                        } else if (jsonEntry.getKey().startsWith("#")) {
                            if (jsonEntry.getValue().isJsonPrimitive()) {
                                String textValue = jsonEntry.getValue().toString();
                                parentNode.appendChild(xmlDom.createTextNode(textValue.replaceAll("\"", "")));
                            }
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
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(TEST_FILES_TEST_TXT));

            //writing xml dom to file and console
            transformer.transform(source, console);
            transformer.transform(source, file);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


}
