package converter;


import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        //Converter.convert();
        //Converter.printXmlContent();
        String input = new Scanner(System.in).nextLine();
        Parser.parseXml2(input);
    }

}
