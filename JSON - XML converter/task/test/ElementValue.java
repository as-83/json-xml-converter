import java.util.Objects;
import java.util.regex.Pattern;

public class ElementValue {
    public static final ElementValue ABSENT = new ElementValue(Type.ABSENT);
    public static final ElementValue NULL = new ElementValue(Type.NULL);

    private static final Pattern TEXT_PATTERN = Pattern.compile("^\"?(?<text>.*?)\"?$");
    private final Type type;
    private final String text;

    public ElementValue(final Type type) {
        this.type = type;
        text = null;
    }

    public ElementValue(final String text) {
        this.type = Type.TEXT;
        this.text = text;
    }

    public static ElementValue parse(final String value) {
        if ("null".equalsIgnoreCase(value)) {
            return NULL;
        }
        final var text = TEXT_PATTERN.matcher(value).replaceFirst("${text}");
        return new ElementValue(text);
    }

    enum Type {
        ABSENT, NULL, TEXT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementValue that = (ElementValue) o;
        return type == that.type && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, text);
    }

    @Override
    public String toString() {
        switch (type) {
            case ABSENT:
                return "";
            case NULL:
                return "value = null\n";
            default:
                return "value = \"" + text + "\"\n";
        }
    }
}
