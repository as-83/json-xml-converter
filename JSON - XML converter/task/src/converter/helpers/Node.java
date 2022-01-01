package converter.helpers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private String nodeName;
    private String value;
    private Map<String, String> attributes = new LinkedHashMap<>();
    private List<Node> children;
    private List<Node> parent;
    private NodeType nodeType;

    public Node() {
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Node> getParent() {
        return parent;
    }

    public void setParent(List<Node> parent) {
        this.parent = parent;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }


}
