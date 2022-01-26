package converter;

import converter.domconverters.JsonToDomConverter;
import converter.domconverters.XmlToDomConverter;
import converter.helpers.Dom;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// https://mvnrepository.com/artifact/com.google.code.gson/gson
// implementation group: 'com.google.code.gson', name: 'gson', version: '2.7'
public class Main {
    public static void main(String[] args) throws ParserConfigurationException {
        Path path = Paths.get("test.txt");
        try {
            String input = Files.readString(path).replaceAll("[\n\r] *", "");
            char firstChar = input.charAt(0);
            Dom dom = null;
            String result = "";
            if (firstChar == '{') {
                dom = JsonToDomConverter.convert(input);
                result = dom.getAsXml();
            } else if (firstChar == '<') {
                dom = XmlToDomConverter.convert(input);
                result = dom.getAsJson();
            } else {
                result = "Unknown format";
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
