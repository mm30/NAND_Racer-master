// this class handles individual scores
class Score {
    private double time;
    private String username;
    private int maxNameSize = 20;

    Score(String name, double time){
        this.time = time;
        username = fitUsername(name);
    }

    /* empty usernames will be recognised as Unknown
    usernames longer than 20 chars will be shortened
     */
    private String fitUsername(String name){
        if (name.equals("")){
            return "Unknown";
        }
        for (int i = 0; i < name.length(); i++){
            if(name.charAt(i) == ' '){
                return name.substring(0, i);
            }
        }
        if (name.length() > maxNameSize){
            return name.substring(0, maxNameSize);
        }
        return name;
    }

    double getTime(){
        return time;
    }
    String getUsername(){
        return username;
    }

    /************* TESTING *************/
    /*
    public static void main(String[] args) {
        Score valid = new Score("jack", 12);
        valid.startTest();
    }
    private void claim(boolean b) {
        if (!b) throw new Error("Test fails");
    }

    private void startTest(String[] args){
        if(args.length == 0)
            test();
        else
            return;
    }

    private void test(){
        System.out.println("Starting tests...");
        testConstructor();
        testFit();
        System.out.println("Tests passed");
    }
    private void testConstructor(){
        claim(getComets() == 12);
        claim(getUsername().equals("jack"));
    }
    private void testFit(){
        String empty = "";
        claim(fitUsername(empty).equals("Unknown"));
        String tooLong = "thisistoolongtofit";
        claim(fitUsername(tooLong).equals("thisistool"));
        String noSpacesAllowed = "a b c ";
        claim(fitUsername(noSpacesAllowed).equals("a"));
        String normal = "Katie";
        claim(fitUsername(normal).equals("Katie"));
        String tenChars = "abcdefghij";
        claim(fitUsername(tenChars).equals("abcdefghij"));
    }
    */
}
