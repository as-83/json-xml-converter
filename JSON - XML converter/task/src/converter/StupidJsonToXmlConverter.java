package converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.Set;

public class StupidJsonToXmlConverter {
    public static void jsonToXml(String json) {
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);
        jsonToXml(jsonTree);
    }

    //recursive method
    private static void jsonToXml(JsonElement jsonTree) {
        if (jsonTree.isJsonObject()) {
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : set) {
                if (entry.getValue().isJsonObject()) {
                    System.out.print("<" + entry.getKey());
                    //recursive call
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

}
