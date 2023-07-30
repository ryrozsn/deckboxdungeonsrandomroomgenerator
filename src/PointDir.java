import java.awt.Point;

public class PointDir {

    private final Point coor;
    private final char direction;

    /**
     * A class used to hold all potential coordinates for the dungeon to expand to, also contains the direction
     * the card must face if placed at said location
     * @param coor - The coordiate of the card to add into the dungeon
     * @param direction - the direction of a card to be placed if done
     */
    public PointDir(Point coor, char direction) {
        this.coor = coor;
        this.direction = direction;
    }

    /**
     * returns the coordinate
     * @return the coordinate
     */
    public Point getCoor() { return coor; }

    /**
     * returns the direction of the potential card placed at coor
     * @return the direction
     */
    public char getDirection() { return direction; }

}