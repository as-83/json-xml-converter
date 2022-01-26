package converter.domconverters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import converter.helpers.Dom;
import converter.helpers.Node;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonToDomConverter {
    private static ArrayDeque<String> pathStack = new ArrayDeque<>();
    private static Map<String, String> attributes = new LinkedHashMap<>();
    private static  String textValue = "";
    private Dom dom;

    public static Dom convert(String json) throws ParserConfigurationException, FileNotFoundException {
        //getting json tree
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);
        /* PrintStream tmpOut = System.out;
        System.setOut(new PrintStream("out.txt"));*/
        Dom dom = new Dom();
        Node node = new Node();
        node.setNodeName("root");
        dom.setRoot(node);
        convert(jsonTree, node);
        //System.setOut(tmpOut);
        return dom;
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
    private static void convert(JsonElement jsonTree, Node currentNode) {

        if (jsonTree.isJsonObject()) {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> jsonObjectEntrySet = jsonObject.entrySet();
            Node newNode = new Node();

            //checking the attributes correctness
            boolean noAttributes = jsonObjectEntrySet.stream()
                    .anyMatch(e -> e.getKey().matches("[^@#].+") || e.getKey().trim().length() < 2 ) ||
                    jsonObjectEntrySet.stream().filter(e -> e.getKey().startsWith("@"))
                    .anyMatch(e -> e.getValue().isJsonObject()) ||
                    jsonObjectEntrySet.stream().filter(e -> e.getKey().startsWith("#")).count() != 1 ||
                    jsonObjectEntrySet.stream().anyMatch(e -> e.getKey().startsWith("#")
                            && !e.getKey().replace("#", "")
                            .equals(pathStack.getLast()));
            //System.out.println(jsonObjectEntrySet);

            //removing jsonObjects with wrong keys
            boolean removed = jsonObjectEntrySet.removeIf( e -> e.getKey().isBlank() ||
                    ( (e.getKey().startsWith("@") || (e.getKey().startsWith("#"))) && e.getKey().length() < 2) );


            //System.out.println(jsonObjectEntrySet);
            //System.out.println("Were removed from jsonObjectEntrySet = " + removed );
            //System.out.println("------------------------------------");

            //if something wrong with attributes names or values in json
            //or there are no attributes at all then add them as nodes not attributes
            if (noAttributes) {
                for (Map.Entry<String, JsonElement> jsonEntry : jsonObjectEntrySet) {
                    newNode = new Node();
                    newNode.setNodeName(jsonEntry.getKey().replaceFirst("[@#]", ""));
                    currentNode.addChild(newNode);
                    newNode.setParent(currentNode);

                    pathStack.add(jsonEntry.getKey().replaceFirst("[@#]", ""));

                    if (jsonEntry.getValue().isJsonObject()) {
                        Set<Map.Entry<String, JsonElement>> nextJsonObjectEntrySet = jsonEntry.getValue().getAsJsonObject().entrySet();
                        long correctKeysCount = nextJsonObjectEntrySet.stream().filter( e -> !e.getKey().isBlank() &&
                                !( (e.getKey().startsWith("@") || (e.getKey().startsWith("#"))) && e.getKey().length() == 1)).count();
                        if (correctKeysCount == 0){
                            newNode.setValue("");
                        } else {
                            //recursive call for next jsonEntry
                            convert(jsonEntry.getValue(), newNode);
                        }
                    } else if (jsonEntry.getValue().isJsonPrimitive()) {
                        textValue = jsonEntry.getValue().toString().replaceAll("[@#\"]", "");
                        newNode.setValue(textValue);
                    } else if (jsonEntry.getValue().isJsonNull()) {
                        newNode.setValue(null);
                    }
                    pathStack.removeLast();
                }
            } else { //if everything OK with attributes names and values then add attributes to current node
                for (Map.Entry<String, JsonElement> jsonEntry : jsonObjectEntrySet) {

                    if (jsonEntry.getValue().isJsonObject()) {
                        convert(jsonEntry.getValue(), newNode);
                    } else {
                        if (jsonEntry.getKey().startsWith("@")) {
                            String value = jsonEntry.getValue().toString().replaceAll("\"", "");
                            if (jsonEntry.getValue().isJsonNull()) {
                                value = "";
                            }
                            currentNode.addAttribute(jsonEntry.getKey().replaceAll("[#@]", ""),value);
                           // attributes.put(jsonEntry.getKey().replaceAll("[#@]", ""),value);
                        } else if (jsonEntry.getKey().startsWith("#")) {
                            if (jsonEntry.getValue().isJsonPrimitive()) {
                                textValue = jsonEntry.getValue().toString().replaceAll("[@#\"]", "");
                                currentNode.setValue(textValue);//TODO
                            }
                        }
                    }
                }
            }

        }
    }




}
