package converter;

import java.util.*;


public class Parser {

    public static void parseXml2(String input) {
        /*Dom dom = new Dom();
        Node currentNode = new Node();*/

        ArrayDeque<String> tagsStack = new ArrayDeque<String>();
        Map<String, String> attributes = new LinkedHashMap<>();

        for (int i = 0; i < input.length(); i++) {
            //if beginning of tag
            if (input.charAt(i) == '<' && input.charAt(i + 1) != '/') {
                i++;
                //parsing tag name
                StringBuilder currentTagName = new StringBuilder();
                while (isNoTagsNameEnd(input, i)) {
                    currentTagName.append(input.charAt(i));
                    i++;
                }
                tagsStack.add(currentTagName.toString());
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
                    i++;
                    printAttributes(attributes);
                    tagsStack.removeLast();

                }
            } else if (input.charAt(i) == '<' && input.charAt(i + 1) == '/') {
                while (input.charAt(i) == '<' && input.charAt(i + 1) == '/') {
                    //move to the end of closing tag
                    while (input.charAt(i) != '>') {
                        i++;
                    }
                    tagsStack.removeLast();
                    if (i < input.length() - 1 && input.charAt(i + 1) == '<' && input.charAt(i + 2) == '/') {
                        i++;
                    }
                }
            }

            if (input.charAt(i) == '>' && input.charAt(i - 1) != '/' && i != input.length() - 1 ) {
                //if empty value
                if (input.charAt(i + 1) == '<' && input.charAt(i + 2) == '/') {
                    System.out.println("value = \"\"");
                }
                //if tag has value
                if (input.charAt(i + 1) != '<') {

                    i++;

                    if (input.charAt(i) == '\n' || input.charAt(i) == '\t') {
                        while (input.charAt(i) == '\n' || input.charAt(i) == '\t') {
                            i++;
                        }
                    } else {
                        System.out.print("value = \"");
                        //parse value
                        while (input.charAt(i + 1) != '<') {
                            System.out.print(input.charAt(i));
                            i++;
                        }
                        System.out.print(input.charAt(i));
                        System.out.println("\"");
                    }
                    printAttributes(attributes);
                }

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
        } else {
            System.out.println();
        }
    }

    private static int parseAttribute(String input,Map<String, String> attributes, int i) {
        StringBuilder currentAttrName = new StringBuilder();
        StringBuilder currentAttrVal = new StringBuilder();
        //parsing attribute name
        while (!(input.charAt(i) == '=' || input.charAt(i) == ' ')) {
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
        if (input.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    private static void printPath(ArrayDeque<String> tagsStack) {
        System.out.print("path = ");

        Iterator<String> iterator = tagsStack.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next());
            if (iterator.hasNext()) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }

    }

    private static boolean isNoTagsNameEnd(String input, int i) {
        return input.charAt(i) != '/' && input.charAt(i) != '>' && input.charAt(i) != ' ';
    }

}
