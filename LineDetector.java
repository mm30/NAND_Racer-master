/*A detector for lap count increment and winning detection*/

class LineDetector {
    private double a, b; // Normal vector of the finish line
    private double dir; // Direction of the normal vector (Rad)

    // a*x + b*y = c
    // checkLine = c of the check line (a line right behind the finish line)
    // mainLine = c of the finish line
    private int checkLine;
    private int mainLine;

    private Coordinates LBound, RBound; // Side bounds of the finish line

    private boolean checked; // true if check line is crossed
    private boolean main; // true if finish line is crossed (after crossing the check line)

    LineDetector(Gameobject l) {
        if (l.getObjectType() != ObjectType.LINE) throw new Error("Input must be a LINE");
        
        dir = Math.toRadians(l.getDirection());
        a = Math.cos(dir);
        b = Math.sin(dir);
        
        double x = l.getX(), y = l.getY();
        if (a > 0 || b > 0) checkLine = (int) ( a * (x - 20) + b * (x - 20) );
        else if (a < 0 || b < 0) checkLine = (int) ( a * (x + 20) + b * (y + 20) );

        mainLine = (int) (a*x + b*y);

        LBound = new Coordinates(x + 40 * Math.cos(dir - Math.PI/2),
                                 y + 40 * Math.sin(dir - Math.PI/2));
        RBound = new Coordinates(x + 40 * Math.cos(dir + Math.PI/2),
                                 y + 40 * Math.sin(dir + Math.PI/2));

        checked = false;
        main = false;
    }

    private void isCrossing(String l, Vehicle v) {
        double x = v.position().x(), y = v.position().y();
        int vLine = (int) ( a * x + b * y );

        if ("check".equals(l)) {
            if (Math.abs(vLine - checkLine) < 5) checked = true;
        }
        else if ("main".equals(l)) {
            if (Math.abs(vLine - mainLine) < 5) main = true;
        }
        else throw new Error("Line selection needed");
    }

    private boolean inBound(Vehicle v) {
        double x = v.position().x(), y = v.position().y();
        double upperX = Math.max(LBound.x(), RBound.x());
        double lowerX = Math.min(LBound.x(), RBound.x());
        double upperY = Math.max(LBound.y(), RBound.y());
        double lowerY = Math.min(LBound.y(), RBound.y());

        if (upperX - lowerX == 0) {
            if (lowerY <= y && y <= upperY) return true;
        }
        else if (upperY - lowerY == 0) {
            if (lowerX <= x && x <= upperX) return true;
        }
        else {
            if (lowerX <= x && x <= upperX &&
                lowerY <= y && y <= upperY) return true;
        }
        return false;
    }

    // return true if both check and main line are crossed in order, i.e., finish line crossed
    boolean detect(Vehicle v) {
        if (inBound(v)) {
            if (checked == false) isCrossing("check", v);
            else isCrossing("main", v);
        }
        return main;
    }

    // reset after both lines are crossed
    void reset() {
        if (checked && main) {
            checked = false;
            main = false;
        }
    }

    /*Testing*/
    private void claim(boolean b) { if (!b) throw new Error("Test failure"); }

    private void test() {
        
    }

    private void run() {
        
    }

    public static void main(String args) {
        
    }
}
