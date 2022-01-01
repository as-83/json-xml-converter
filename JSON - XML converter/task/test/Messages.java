import java.util.ListResourceBundle;

public class Messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"empty", "Your output is empty line."},
                {"minimumTwoLines",
                        "Every element block should contains minimum two lines."},
                {"startElement",
                        "The first line of element block should starts with \"Element\" keyword.\n"
                                + "Found the first line: \"{0}\"."},
                {"startPath",
                        "The second line of element block should starts with \"path\" keyword.\n"
                                + "Found the second line in the block: \"{0}\"."},
                {"pathPattern",
                        "The path of element should be in the format: \"path = tag0, tag1, ..., tagN\"\n"
                                + "Found the path in format: \"{0}\"."},
                {"thirdLine",
                        "The third line of block should starts with keywords 'value' or 'attributes'.\n"
                                + "Found the third line: {0}."},
                {"valuePattern",
                        "The value of element should be in the format:\n"
                                + "value = \"...\" or value = null\n" + "Found: {0}."},
                {"startAttributes",
                        "The line after value should starts with \"attributes\" keyword.\n"
                                + "Found the line after the \"value\": \"{0}\"."},
                {"noAttributes",
                        "After keyword \"attributes\" should be at least one attribute.\n"
                                + "If an element doesn't have attributes the program "
                                + "should skip printing the keyword \"attributes\" for that element."},
                {"attributePattern",
                        "The attribute of element should be in the format:\n"
                                + "key = \"value\"" + "Found: {0}."},

                {"lessElements",
                        "The number of elements is less then expected.\n"
                                + "The program outputs only {0} elements but expected {1}."},
                {"moreElements",
                        "The number of elements is more then expected.\n"
                                + "The program outputs {0} elements but expected only {1}."},
                {"elementsNotEqual",
                        "The element number {0} is not equals to expected.\n"
                                + "The expected output for element #{0} is\n{1}"},
        };
    }
}