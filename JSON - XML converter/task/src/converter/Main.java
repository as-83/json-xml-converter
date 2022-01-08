package converter;


import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        //String input = "<node><child name = \"child_name1\" type = \"child_type1\"><subchild id = \"1\" auth=\"auth1\">Value1</subchild></child><child name = \"child_name2\" type = \"child_type2\"><subchild id = \"2\" auth=\"auth1\">Value2</subchild><subchild id = \"3\" auth=\"auth2\">Value3</subchild><subchild id = \"4\" auth=\"auth3\"></subchild></child></node>";
        Path path = Paths.get("test.txt");
        String input = null;
        try {
            input = Files.readString(path);
            Parser.parseXml2(input.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
