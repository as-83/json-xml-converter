package converter;

import converter.converters.JsonToContentConverter;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException {
        Path path = Paths.get("test.txt");
        try {
            String input = Files.readString(path);
            JsonToContentConverter.jsonToContent(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
