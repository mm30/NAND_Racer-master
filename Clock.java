//class that keeps track of the time elapsed when in game
import java.math.BigDecimal;
import java.math.RoundingMode;
class Clock{

    private double limit, prevTime;   //gives time in seconds
    private Clock next;

    //constructor to set the respective variables
    Clock(double limit, double prevTime, Clock next){
        this.limit = limit;
        this.prevTime = prevTime;
        this.next = next;
    }

    //function to increment the value of counter
    void tick(double elapsedT){
        prevTime = prevTime+elapsedT;
        if(prevTime == limit){
            prevTime = 0;
            if(next != null){next.tick(elapsedT);}
        }
    }
    //function to reset the Clock
    void reset(){
        prevTime = 0;
    }

    //function to return the value of the current time
    String getTime(){
        BigDecimal bgDec = new BigDecimal(Double.toString(prevTime));
        bgDec = bgDec.setScale(4, RoundingMode.HALF_UP);
        return bgDec.toString();
    }
/************************TESTING**************************/
    //used instead of assert
    private void claim(boolean b){
        if(!b) throw new Error("Test fails");
    }

    //function used to test the clock class
    private void testClock(){
        int timer = 0;
        while(timer<59){
            timer++;
            tick(1);
            double counter = Double.parseDouble(getTime());
            System.out.println(counter);
            if(counter!=timer)
                throw new Error("Timer does not produce correct values");
        }
    }

    //function used call of the test functions
    private void test(){
        System.out.println("Starting tests...");
        testClock();
        System.out.println("Testing complete!");
    }

    //function used to switch on||off testing
    private void startTest(String[] args){
        if(args.length == 0)
            test();
        else
            return;
    }

    //main used for testing
    public static void main(String[] args) {
        Clock clk = null;
        clk = new Clock(60, 0, clk);
        clk.startTest(args);
    }
}
