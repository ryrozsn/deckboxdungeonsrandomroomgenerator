import java.awt.Point;

public class PointDir {

    private Point coor;
    private char direction;

    public PointDir(Point coor, char direction) {
        this.coor = coor;
        this.direction = direction;
    }

    public Point getCoor() { return coor; }
    public char getDirection() { return direction; }

}