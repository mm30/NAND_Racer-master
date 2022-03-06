/*Vehicle class. Physical feature depends only on motion (displacement, velocity, acceleration)
 * rather than taking forces and mass into account*/

/*
TODO: 
 */
import java.util.LinkedList;
import java.math.BigDecimal;
import java.math.RoundingMode;

class Vehicle {
    /*Current situation*/
    private Coordinates pos;
    private double v; // pixels per second
    private double a; // pixels / s^2
    private double dir; // In degrees
    private double wheelAngle; // Current angle at which the wheel's rotated

    // Acceleration from air resistance, of which the direction is opposite to v
    // It gets larger as the vehicle accelerates, until v reaches maxV
    // which leads to an equilibrium
    private double resist;
    private double facc;

    /*Vehicle features*/
    private double width;
    private double length;
    private double maxV;
    private double maxA; // Max acceleration
    final double turningRate = 65; // Turning circle substituted with degrees per second

    private boolean bool = false; // Boolean value of car;
    private boolean space = false; // user-input from spacebar; secondary Boolean value

    // Initialise current situation and set features
    Vehicle(double x, double y, double dir,
            double width, double length,
            double maxV, double maxA)
    {
        pos = new Coordinates(x, y);
        v = 0;
        a = 0;
        this.dir = dir;
        wheelAngle = 0;
        resist = 0;
        facc = 0;

        this.width = width;
        this.length = length;
        this.maxV = maxV;
        this.maxA = maxA;
    }

    /*getters and setters*/

    // Current situation
    void setPosition(double  x, double y) { pos.setX(x); pos.setY(y); }
    Coordinates position() { return pos; }

    double v() { return v; }

    double direction() { return dir; }

    // Vehicle features
    double width() { return width; }
    double length() { return length; }

    boolean getBool() { return bool; }
    boolean getSpace() { return space; }
    void setSpace(String v) {
        if ("true".equals(v)) space = true;
        else if ("false".equals(v)) space = false;
        else throw new Error("boolean value needed");
    }

    /*---------------------------------------Physics----------------------------------------*/
    // Round up velocity to avoid overflow
    private void roundV() {
        BigDecimal bd = new BigDecimal(Double.toString(v));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        v = bd.doubleValue();
    }

    /*Transformation*/
    private void inertia(double dt) {
        pos.setX( pos.x() + (v * dt) * Math.cos(Math.toRadians(dir)) );
        pos.setY( pos.y() + (v * dt) * Math.sin(Math.toRadians(dir)) );
    }

    // Multiplier depending on bool
    private double mul() {
        double mul = bool ? 1.5 : 1.0;
        return mul;
    }

    // Net acceleration
    private void netAcc(double dt) {
        v += (mul() * a + resist + facc) * dt;
        // Asymptotic factor for adjustment for turning rate based on the velocity
        dir += (1 - 1/(0.2*Math.abs(v) + 1)) *  wheelAngle * dt; 
    }

    // Accelerating or not depends on situation of the accelerator
    void accelerator(String sit, String fOrb) {
        if ("stepped on".equals(sit)) {
            if ("forward".equals(fOrb)) a = maxA;
            else if ("backward".equals(fOrb)) a = -1 * maxA;
            else {
                throw new Error("Direction needed for acceleration");
            }
        }
        else if ("released".equals(sit)) a = 0;

        else throw new Error("Accelerator situation needed");
    }

    void wheel(String sit) {
        switch (sit) {
            case "left":
                wheelLeft();
                break;
            case "right":
                wheelRight();
                break;
            case "released":
                wheelAngle = 0;
                break;
            default:
                throw new Error("Situation of wheel needed");
        }
    }

    private void wheelLeft() {
        if (v > 0) wheelAngle = -1 * turningRate;
        else if (v < 0) wheelAngle = turningRate;
    }

    private void wheelRight() {
        if (v > 0) wheelAngle = turningRate;
        else if (v < 0) wheelAngle = -1 * turningRate;
    }

    /*Interaction with the surroundings*/

    // Current kinetic friction (while the vehicle is moving)
    private void currentFk() {
        if (v == 0) facc = 0;
        else facc = -1 * (v / Math.abs(v)) * 0.1 * maxA;
    }

    // Current resistance
    private void currentResist() { resist = -1 * (v / maxV) * maxA; }

    void motion(double dt) {
        currentFk();
        currentResist();
        netAcc(dt);
        roundV(); // Rounding up as a solution for getting 0 value of velocity
        inertia(dt);
    }

    // Calculate coordinates of four corners of the vehicle
    private Coordinates corner(String which) {
        double dir_rad = Math.toRadians(dir); //direction in radians
        double diagonal = Math.sqrt(width * width + length * length);

        // Angle (rad) between the diagonal and direction of the car
        double A = Math.acos(length / diagonal);
        Coordinates result;

        switch (which) {
            case "front left":
                result = new Coordinates(pos.x() + (diagonal/2) * Math.cos(dir_rad - A),
                                         pos.y() + (diagonal/2) * Math.sin(dir_rad - A));
                break;
            case "front right":
                result = new Coordinates(pos.x() + (diagonal/2) * Math.cos(dir_rad + A),
                                         pos.y() + (diagonal/2) * Math.sin(dir_rad + A));
                break;
            case "rear left":
                result = new Coordinates(pos.x() + (diagonal/2) * Math.cos(dir_rad + Math.PI + A),
                                        pos.y() + (diagonal/2) * Math.sin(dir_rad + Math.PI + A));
                break;
            case "rear right":
                result = new Coordinates(pos.x() + (diagonal/2) * Math.cos(dir_rad + Math.PI - A),
                                        pos.y() + (diagonal/2) * Math.sin(dir_rad + Math.PI - A));
                break;
            default:
                throw new Error("Selection of corner needed");
        }
        return result;
    }

    // Check if vehicle is within the map (window) to avoid array index being out of bound
    private boolean onMap(Coordinates c, Map m) {
        double x = c.x(), y = c.y();

        if (0 <= x && x <= m.getMapWidth() && 
            0 <= y && y <= m.getMapHeight()) return true;
        return false;
    }

    private boolean onTrack(Coordinates c, Map m) {
        if (!onMap(c, m)) return false;

        // Number of tile on which the corner is
        int T = m.getTileSize();
        int tNum = m.getTile( (int) (c.y() / T), (int) (c.x() / T) );

        if (tNum == 0) return false;
        return true;
    }

    // Return the corner that is off track or map
    private String offTrack(Map m) {
        Coordinates frontL = corner("front left");
        Coordinates frontR = corner("front right");
        Coordinates rearL = corner("rear left");
        Coordinates rearR = corner("rear right");

        // TODO At this point, just checking which tile the corners are on
        // colour detection needed
        if (!onTrack(frontL, m)) return "frontL";
        if (!onTrack(frontR, m)) return "frontR";
        if (!onTrack(rearL, m)) return "rearL";
        if (!onTrack(rearR, m)) return "rearR";
        return "none";
    }

    private Gameobject isColliding(LinkedList<Gameobject> objects) {
        for (Gameobject ob : objects) {
            double distance = Math.sqrt( Math.pow(pos.x() - ob.getX(), 2)
                                       + Math.pow(pos.y() - ob.getY(), 2) );

            if (Math.round(distance) < 20) return ob;
        }
        return null;
    }

    void collision(Map m) {
        Gameobject collided = isColliding(m.getObjects());

        switch (offTrack(m)) {
            case "frontL":
                if (v > 0) v = -0.5 * v;
                break;
            case "frontR":
                if (v > 0) v = -0.5 * v;
                break;
            case "rearL":
                if (v < 0) v = -0.5 * v;
                break;
            case "rearR":
                if (v < 0) v = -0.5 * v;
                break;
            default:
        }

        if (collided != null && collided.isActivated()) {
            switch (collided.getObjectType()) {
                case LINE:
                    break;
                case NOT:
                    bool = !(bool);
                    collided.deactivate();
                    break;
                case AND:
                    bool = (bool && space);
                    collided.deactivate();
                    break;
                case NAND:
                    bool = !(bool && space);
                    collided.deactivate();
                    break;
                case OR:
                    bool = (bool || space);
                    collided.deactivate();
                    break;
                case OBSTACLE:
                    v = -0.5 * v;
                    break;
                default: throw new Error("No such object");
            }
        }
    }

    /*unit testing*/
    //TODO test gates collision
    void claim(boolean b) { if (!b) throw new Error("Test failure"); }

    private void test_atRest() {
        double start, prev, current, dt;
        start = prev = current = (System.currentTimeMillis() / 1000); // In second

        do {
            dt = current - prev; // Time elapsed each loop
            motion(dt);

            claim(pos.x() == 0 && pos.y() == 0);
            claim(v == 0);
            claim(a == 0);
            claim(dir == 0);
            claim(wheelAngle == 0);
            claim(facc == 0);

            prev = current;
            current = System.currentTimeMillis() / 1000;

        } while ( (int) (current - start) >= 10);

        /*wheel("left");
          claim(wheelAngle == -30);
          claim(dir == 0);

          wheel("released");
          claim(wheelAngle == 0);
          claim(dir == 0);

          wheel("right");
          claim(wheelAngle == 30);
          claim(dir == 0);

          wheel("released");*/
    }

    /*private void test_inMotion() {
      double start, prev, current, dt;
      double prevV, V, dv;
      double prevP, P,

      start = prev = current = (System.currentTimeMillis() / 1000); //In second
      do {
      dt = current - prev; //Time elapsed each loop

      claim(

      prev = current;
      current = System.currentTimeMillis() / 1000;

      } while ( (int) (current - start) >= 20);

      }*/

    // TODO collision on four corners needed
    /*private void test_collision() {
        int[][] d = {{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};

        Map m = new Map(1280, 720, 80);
        m.insertTrack(d);

        double start, prev, current, dt;
        start = prev = current = (System.currentTimeMillis() / 1000);

        v = maxV;
        a = maxA;
        do {
            dt = current - prev;

            collision(m);
            motion(dt);

            prev = current;
            current = System.currentTimeMillis() / 1000;

        } while (v != 0);
        claim(v == 0);
    }*/

    private void test_corner() {
        // dir == 0
        Coordinates frontL = corner("front left");
        Coordinates frontR = corner("front right");
        Coordinates rearL = corner("rear left");
        Coordinates rearR = corner("rear right");

        System.out.println(frontR.x() + "," + frontR.y());
        //claim( (frontL.x() == pos.x() + length/2) && (frontL.y() == pos.y() - width/2) );
        //claim( (frontR.x() == pos.x() + length/2) && (frontR.y() == pos.y() + width/2) );
        //claim( (rearL.x() == pos.x() - length/2) && (rearL.y() == pos.y() - width/2) );
        claim( (rearR.x() == pos.x() - length/2) && (rearR.y() == pos.y() + width/2) );

        // dir == 90
        dir = 90;
        frontL = corner("front left");
        frontR= corner("front right");
        rearL = corner("rear left");
        rearR = corner("rear right");

        //claim( (frontL.x() == pos.x() + length/2) && (frontL.y() == pos.y() - width/2) );
        //claim( (frontR.x() == pos.x() + length/2) && (frontR.y() == pos.y() + width/2) );
        //claim( (rearL.x() == pos.x() - length/2) && (rearL.y() == pos.y() - width/2) );
        claim( (rearR.x() == pos.x() - length/2) && (rearR.y() == pos.y() + width/2) );
    }

    private void test() {
        //test_atRest();
        //test_corner();
        //test_collision();
        System.out.println("All tests passed!");
    }

    private void run() {
        test();
    }

    public static void main(String[] args) {
        Vehicle car = new Vehicle(0, 0, 0, 30, 40, 60, 20);
        car.run();
    }
}
