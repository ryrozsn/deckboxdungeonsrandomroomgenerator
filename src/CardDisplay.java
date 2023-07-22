public class CardDisplay {
    public String name;
    public char type;
    public char direction;

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
}