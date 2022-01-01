package converter;

import converter.helpers.Dom;
import converter.helpers.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {


    private static Dom parseXml(String input) {
        Dom dom = new Dom();
        Node node = new Node();

        List<String> elements = new ArrayList<>();
//        while(true) {
//            String element = input.substring(0, input.indexOf(">" + 1));
//            input = input.replaceFirst(element, "");
//
//        }


        System.out.println("----------");
        for (String elem: elements) {
            System.out.println(elem);
        }
        System.out.println("----------");
        node.setNodeName(input.substring(1, input.indexOf(">")));

        return dom;
    }

    //splits input string with xml to tags
    public static List<String> getTokens(String xml) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("(<[^<>]+>)|(>\\w*?</)");
        Matcher matcher = pattern.matcher(xml);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        tokens.forEach(System.out::println);
        return tokens;
    }
    
    

    public static void parseXml2(String input) {
        Dom dom = new Dom();
        Node currentNode = new Node();
        StringBuilder currentTag = new StringBuilder();
        LinkedList<String> tagsStack = new LinkedList<>();
        Map<String, String> attributes = new LinkedHashMap<>();


        for (int i = 0; i < input.length(); i++) {
            //if beginning of tag
            if (isTagsStart(input, i)) {
                i++;
                //parsing tag name
                while (isNoTagsNameEnd(input, i)) {
                    currentTag.append(input.charAt(i));
                    i++;
                }
                tagsStack.push(currentTag.toString());
                System.out.println("Element:");
                printPath(tagsStack);

                //if beginning of attributes section
                if (input.charAt(i) == ' ') {
                    i++;
                    //parsing Attributes
                    while (input.charAt(i) != '/' && input.charAt(i) != '>'){
                        i = parseAttribute(input, attributes, i);
                    }
                }
                if (input.charAt(i) == '/') { //if tag value is null
                    System.out.println("value = null");
                    i+=2;
                    printAttributes(attributes);
                    tagsStack.pop();

                }
            }

            if (input.charAt(i) == '>' && i != input.length() - 1 ) {
                //if empty value
                if (input.charAt(i + 1) == '<' && input.charAt(i + 2) == '/') {
                    System.out.println("value = \"\"");
                    i+=3;
                    //move to the end of closing tag
                    while (input.charAt(i) != '>') {
                        i++;
                    }
                    i++;
                    tagsStack.pop();
                }
                //if tag has value
                if (input.charAt(i + 1) != '<') {
                    i++;
                    System.out.print("value = ");
                    //parse value
                    while (input.charAt(i) != '<') {
                        System.out.print(input.charAt(i));
                        i++;
                    }
                    System.out.println();
                    //move to the end of closing tag
                    while (input.charAt(i) != '>') {
                        i++;
                    }
                    i++;
                    tagsStack.pop();
                }
                printAttributes(attributes);

            }
        }


    }

    private static void printAttributes(Map<String, String> attributes) {
        if (attributes.size() > 0) {
            System.out.println("attributes:");
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                System.out.println(entry.getKey() + " = \"" + entry.getValue() + "\"");
            }
            attributes.clear();
        }
    }

    private static int parseAttribute(String input,Map<String, String> attributes, int i) {
        StringBuilder currentAttrName = new StringBuilder();
        StringBuilder currentAttrVal = new StringBuilder();
        //parsing attribute name
        while (!isAttrNameEnd(input, i)) {
            currentAttrName.append(input.charAt(i));
            i++;
        }
        //moving to the  beginning of attribute value
        while (input.charAt(i) != '\"') {
            i++;
        }
        i++;
        //parsing attribute value
        while (input.charAt(i) != '\"') {
            currentAttrVal.append(input.charAt(i));
            i++;
        }
        i++;
        attributes.put(currentAttrName.toString(), currentAttrVal.toString());
        return i;
    }

    private static boolean isAttrNameEnd(String input, int i) {
        return input.charAt(i) == '=' || input.charAt(i) == ' ';
    }

    private static void printPath(LinkedList<String> tagsStack) {
        System.out.print("path = ");
        for (int i = 0; i < tagsStack.size(); i++) {
            System.out.print(tagsStack.get(i));
            if (i < tagsStack.size() - 1) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }

    private static boolean isNoTagsNameEnd(String input, int i) {
        return input.charAt(i) != '/' && input.charAt(i) != '>' && input.charAt(i) != ' ';
    }

    private static boolean isTagsStart(String input, int i) {
        return input.charAt(i) == '<' && input.charAt(i + 1) != '/';
    }
}
