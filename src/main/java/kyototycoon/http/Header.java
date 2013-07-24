package kyototycoon.http;

public class Header {
    public String name;
    public String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public int getValueAsInt() {
        return Integer.parseInt(value);
    }
}
