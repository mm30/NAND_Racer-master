// this class concerns the leaderboard, recording top scores in Leaderboard.txt
import java.io.*;
import java.util.LinkedList;
import java.lang.String;

public class Leaderboard {
    private final int topFive = 5;
    private LinkedList<Score> highScores = new LinkedList<>();
    private File file;

    Leaderboard(File filename){
        this.file = filename;
        readFile();
    }
    void readFile(){
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                buildScore(sCurrentLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    void buildScore(String line){
        String username = "";
        double time;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' '){
                username = line.substring(0, i);
                time = Double.parseDouble(line.substring(i+1));
                Score currentScore = new Score(username, time);
                update(currentScore);
            }
        }
    }
    void update(Score score){
        if (highScores.size()<topFive){
            for (int i = 0; i < highScores.size(); i++){
                if (score.getTime() < highScores.get(i).getTime()) {
                    highScores.add(i, score);
                    return;
                }
            }
            highScores.add(score);
        }
        else{
            for (int i = 0; i < topFive; i++){
                if (score.getTime() < highScores.get(i).getTime()) {
                    highScores.add(i, score);
                    return;
                }
            }
            highScores.add(score);
        }
    }
    // saves the scores in the relevant text file
    void save(){
        try{
            PrintStream fileStream = new PrintStream(file);
            System.setOut(fileStream);

            if(highScores.size()>topFive){
                for (int i = 0; i < topFive; i++){
                    printScore(highScores.get(i));
                }
            }
            else{
                for (int i = 0; i < highScores.size(); i++){
                    printScore(highScores.get(i));
                }
            }

        } catch (IOException ex) {
            System.out.println("There was a problem writing to the file");
            ex.printStackTrace();
        }
    }
    // check whether a score qualifies to be in the top 5
     boolean highScore(Score score){
        if (highScores.size() < topFive){
            return true;
        }
        for (int i = 0; i < topFive; i++){
            if (score.getTime() < highScores.get(i).getTime()){
                return true;
            }
        }
        return false;
    }
    void printScore(Score score){
        System.out.println(score.getUsername()+ " " + score.getTime());
    }
    Score getScores(int n){
        return highScores.get(n);
    }
    int numScores(){
        return highScores.size();
    }
    String getScoreString(int n){
        String score = "";
        score += (getScores(n).getUsername() + " " + getScores(n).getTime());
        return score;
    }
    /*** TESTING ****/
    /*
    public static void main(String[] args){
        Leaderboard testing = new Leaderboard("Testing.text");
        testing.test();
    }
    private void test(){
        System.out.println("Testing started");
        testOne();
        testHigh();
        testLow();
        System.out.println("Tests passed");
    }
    private void claim(boolean b) {
        if (!b) throw new Error("Test fails");
    }
    // testing new scores
    private void testOne(){
        claim(numScores() == 0);
        Score first = new Score("first", 100); update(first);
        Score second = new Score("second", 62); update(second);
        Score third = new Score("third", 12); update(third);
        claim(numScores() == 3);
        claim(getScores(0).getUsername().equals("first"));
        claim(getScores(0).getComets() == 100);
        claim(getScores(1).getUsername().equals("second"));
        claim(getScores(1).getComets() == 62);
        claim(getScores(2).getUsername().equals("third"));
        claim(getScores(2).getComets() == 12);
    }
    // updating with a new high score
    private void testHigh(){
        Score newFirst = new Score ("winner", 200); update(newFirst);
        claim(numScores() == 4);
        claim(getScores(0).getUsername().equals("winner"));
        claim(getScores(0).getComets() == 200);
        claim(getScores(1).getUsername().equals("first"));
        claim(getScores(1).getComets() == 100);
    }
    // updating with a new low score
    private void testLow(){
        Score newLow = new Score ("lowest", 1); update(newLow);
        claim(numScores() == 5);
        claim(getScores(4).getUsername().equals("lowest"));
        claim(getScores(4).getComets() == 1);
    }
    */
}
