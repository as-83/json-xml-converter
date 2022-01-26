package converter.domconverters;

import converter.helpers.Dom;
import converter.helpers.Node;

import java.util.ArrayDeque;

public class XmlToDomConverter {

    public static Dom convert(String input) {
        Dom dom = new Dom();
        Node currentNode = null;
        Node parentNode = null;

        ArrayDeque<String> tagsStack = new ArrayDeque<>();

        for (int i = 0; i < input.length(); i++) {
            //if beginning of tag
            if (currentNode != null) {
                parentNode = currentNode;
            }

            if (input.charAt(i) == '<' && input.charAt(i + 1) != '/') {
                currentNode = new Node();
                if (parentNode != null) {
                    parentNode.addChild(currentNode);
                    currentNode.setParent(parentNode);
                } else{
                    dom.setRoot(currentNode);
                }
                i++;
                //parsing tag name
                StringBuilder currentTagName = new StringBuilder();
                while (isNoTagsNameEnd(input, i)) {
                    currentTagName.append(input.charAt(i));
                    i++;
                }
                currentNode.setNodeName(currentTagName.toString());
                tagsStack.add(currentTagName.toString());

                //if beginning of attributes section
                if (input.charAt(i) == ' ') {
                    i++;
                    //parsing Attributes
                    while (input.charAt(i) != '/' && input.charAt(i) != '>'){
                        i = parseAttribute(input, currentNode, i);
                    }
                }
                if (input.charAt(i) == '/') { //if tag value is null
                    i++;
                    tagsStack.removeLast();
                    currentNode = currentNode.getParent();
                    if (currentNode != null) {
                        parentNode = currentNode.getParent();
                    }
                }
            } else if (input.charAt(i) == '<' && input.charAt(i + 1) == '/') {
                while (input.charAt(i) == '<' && input.charAt(i + 1) == '/') {
                    //move to the end of closing tag
                    while (input.charAt(i) != '>') {
                        i++;
                    }
                    currentNode = currentNode.getParent();//TODO siblings
                    if (currentNode != null) {
                        parentNode = currentNode.getParent();
                    }
                    //TODO?
                    tagsStack.removeLast();
                    if (i < input.length() - 1 && input.charAt(i + 1) == '<' && input.charAt(i + 2) == '/') {
                        i++;
                    }
                    /*if (tagsStack.isEmpty()) {
                        i++;
                    }*/
                }
            }

            if (input.charAt(i) == '>' && input.charAt(i - 1) != '/' && i != input.length() - 1 ) {
                //if empty value
                if (input.charAt(i + 1) == '<' && input.charAt(i + 2) == '/') {
                    currentNode.setValue("");
                }
                //if tag has value
                if (input.charAt(i + 1) != '<') {
                    StringBuilder valueBuilder = new StringBuilder();
                    i++;
                    //parse value
                    while (input.charAt(i + 1) != '<') {
                        valueBuilder.append(input.charAt(i));
                        i++;
                    }
                    valueBuilder.append(input.charAt(i));
                    currentNode.setValue(valueBuilder.toString());
                }

            }
        }
        return  dom;

    }

    private static int parseAttribute(String input,Node node, int i) {
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
        node.addAttribute(currentAttrName.toString(), currentAttrVal.toString());
        if (input.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    private static boolean isNoTagsNameEnd(String input, int i) {
        return input.charAt(i) != '/' && input.charAt(i) != '>' && input.charAt(i) != ' ';
    }

}
