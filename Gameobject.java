// to be stored as x y string
// eg 120 360 LINE

import javafx.scene.image.Image;

class Gameobject{
    private Coordinates coordinates;
    private double dir;
    private ObjectType type;
    private boolean active;

    Gameobject(double x, double y, double direction, ObjectType type){
        this.coordinates = new Coordinates(x, y);
        dir = direction;
        this.type = type;
        active = true;
    }

    // Getters
    double getX(){ return coordinates.x(); }
    double getY(){ return coordinates.y(); }
    double getDirection() { return dir; }
    ObjectType getObjectType() { return type; }

    // Deactivate gates if collided
    void deactivate() { active = false; }

    // Activate gates after certain amount of time of deactivation
    void activate() { active = true; }

    boolean isActivated() { return active; }
}
