public class CardDisplay {
    private String name;
    private char type;
    private char direction;

    public CardDisplay(String name, char direction, char type) {
        this.name = name;
        this.direction = direction;
        this.type = type;
    }

    public String toString() {
        return "[" + name + type + ", " + direction + "]";
    }

    public String getName() {
        return (name + type).trim();
    }

    public String getPureName() { return name; }

    public char getType() { return type; }

    public char getDirection() { return direction; }
}