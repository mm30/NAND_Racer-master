//class that models the speedometer value of the vechile
class Speedometer{

    private double multiplier = 0.5;   //simple scaling factor to give reasonable value
    private double speed;

    //constructor to set the current car instance
    Speedometer(){
        speed = 0;
    }

    //function that gets speed of the car
    void setSpeed(Vehicle car){
        speed = Math.abs(car.v()*multiplier); //max should be 180
    }

    //function that produces the angle of the needle
    double getAngle(){
        double angle = speed;
        return angle;
    }

    /*******************************TESTING************************************/

    //used instead of assert
    private void claim(boolean b){
        if(!b) throw new Error("Test fails");
    }

    //function used to test speedO()
    private void testSpeedO(){
        Vehicle car = new Vehicle(0.0, 0.0, 90.0, 10.0, 10.0, 40.0, 10.0);
        setSpeed(car);
        claim(speed == 0.0);
        return;
    }

    private void test(){
        System.out.println("Starting testing...");
        testSpeedO();
        System.out.println("Finished testing...");
        return;
    }

    private void startTest(String[] args){
        if(args.length == 0)
            test();
        else
            return;
    }

    //main used to test the speedometer class
    public static void main(String[] args){
        Speedometer speedo = new Speedometer();
        speedo.startTest(args);
    }
}
