public class CardDisplay {
    private final String name;
    private final char type;
    private final char direction;

    public CardDisplay(String name, char direction, char type) {
        this.name = name;
        this.direction = direction;
        this.type = type;
    }

    /**
     * Returns the formatted string version of the card in the format
     * [name + type, direction]
     * Example: [1A, u]
     * @return formatted string
     */
    public String toString() {
        return "[" + name + type + ", " + direction + "]";
    }

    /**
     * Returns method
     * @return name and type
     */
    public String getName() {
        return (name + type).trim();
    }

    /**
     * Return method
     * @return just name variable
     */
    public String getPureName() { return name; }

    /**
     * Return method
     * @return just type variable (ie is the A side or B side)
     */
    public char getType() { return type; }

    /**
     * Return the direction of the card in the dungeon
     * @return a char ie u = up, d = down, l = left, and r = right
     */
    public char getDirection() { return direction; }
}