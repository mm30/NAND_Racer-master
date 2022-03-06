class Coordinates {
    private double x;
    private double y;

    Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double x() { return x; }
    double y() { return y; }

    void setX(double x) { this.x = x; }
    void setY(double y) { this.y = y; }
}
