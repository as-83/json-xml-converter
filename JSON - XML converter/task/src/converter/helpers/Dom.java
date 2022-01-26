package converter.helpers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Dom {
    private Node root;

    public Dom() {
    }

    public Dom(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    //returns current dom in xml format
    public String getAsXml() {
        Node node;
        if (root.getChildren().size() == 1) {
            node = root.getChildren().get(0);
        } else {
            node = root;
        }
        String xml = getAsXml(node);

        return xml;

    }


    private String getAsXml(Node node) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<" + node.getNodeName());
        if (node.getAttributes().size() > 0) {
            String attributes = addAttributesToXml(node.getAttributes());
            stringBuilder.append(attributes);
        }
        List<Node> children = node.getChildren();
        if (children != null) {
            stringBuilder.append(">");
            for (Node child : children) {
                stringBuilder.append(getAsXml(child));
            }
            stringBuilder.append("</" + node.getNodeName() + ">");

        } else {
            if(node.getValue() != null) {
                stringBuilder.append(">");
                stringBuilder.append(node.getValue());
                stringBuilder.append("</" + node.getNodeName() + ">");
            } else {
                stringBuilder.append(" />");
            }
        }

        return  stringBuilder.toString();
    }

    private String addAttributesToXml(Map<String, String> attributes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            stringBuilder.append(" " + entry.getKey() + " = \"" + entry.getValue().replaceAll("\"", "") + "\"");
        }
        return stringBuilder.toString();
    }

    public String getAsJson() {
        String json = "{" + getAsJson(root) + "}";
        return json;
    }

    private String getAsJson(Node node) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\"" + node.getNodeName() + "\": ");
        if (node.getAttributes().size() > 0) {
            String attributes = addAttributesToJson(node.getAttributes());
            stringBuilder.append(attributes);
            stringBuilder.append("\"#" + node.getNodeName() + "\": ");
        }
        List<Node> children = node.getChildren();
        if (children != null) {
            stringBuilder.append("{");
            for (int i = 0; i < children.size(); i++) {
                boolean isLastChild = i == children.size()-1;//TODO if size == 1
                stringBuilder.append(getAsJson(children.get(i)));
                if (!isLastChild) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append("}");

        } else {
            if(node.getValue() != null) {
                stringBuilder.append("\"");
                stringBuilder.append(node.getValue());
                stringBuilder.append("\"");

            } else {
                stringBuilder.append("null");
            }

            if (node.getAttributes().size() > 0) {
                stringBuilder.append("}");
            }

        }

        return  stringBuilder.toString();
    }

    private String addAttributesToJson(LinkedHashMap<String, String> attributes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (Map.Entry<String, String> entry: attributes.entrySet()) {
            stringBuilder.append("\"@" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}
