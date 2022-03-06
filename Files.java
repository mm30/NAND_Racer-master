import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class Files {
    private String file;
    private String filename;

    // arguments refer to filename, grid width and grid height
    Files(String filename){
        this.filename = filename;
        this.file = filename;
    }

    // reads data from file, storing it into mapData and objectData
    void loadMap(Map m){
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            m.insertBackground( getBackground(br) );
            m.insertTrack( getTrackData(br) );

            // an empty line in between
            while ((line = br.readLine()) != null){
                m.insertObject( getObjectData(line) );
            }
        }
        catch (IOException e) {
            // TODO throw an error for unexisting file
            e.printStackTrace();
        }
    }

    // read in one-line background info
    private int getBackground(BufferedReader br) throws IOException {
        String line = br.readLine();

        if (line != null) {
            br.readLine(); // skip the empty line between background and track
            return Integer.parseInt(line);
        }
        else throw new Error("Background selection missing");
    }

    // read in tile values till it reaches an empty line
    private int[][] getTrackData(BufferedReader br) throws IOException {
        int[] temp;
        int[][] track = new int[0][0];
        String line;
        
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            temp = new int[line.length()];

            for (int i = 0; i < line.length(); i++) {
                temp[i] = Character.getNumericValue(line.charAt(i));
            }
            track = Arrays.copyOf(track, track.length + 1);

            track[track.length - 1] = new int[temp.length];
            System.arraycopy(temp, 0, track[track.length - 1], 0, temp.length);
        }
        return track;
    }

    private Gameobject getObjectData(String line) {
        String[] data = line.split(" ");
        
        Gameobject ob = new Gameobject(Double.parseDouble(data[0]),
                                       Double.parseDouble(data[1]),
                                       Double.parseDouble(data[2]),
                                       ObjectType.valueOf(data[3]));
        return ob;
    }
    String getFilename(){
        return filename;
    }

    /*Testing*/ //TODO modification for background and object direction
    void claim(boolean b) { if (!b) throw new Error("Test failure"); }

    // check first layer content
    private boolean checkFirst(Map m, String ... ctrl) {
        for (int i = 0; i < ctrl.length; i++) {
            for (int j = 0; j < ctrl[0].length(); j++) {

                int current = Character.getNumericValue(ctrl[i].charAt(j));
                if (m.getTile(i, j) != current) return false;
            }
        }
        return true;
    }

    // second layer
    private boolean checkSec(Map m, String ... ctrl) {
        LinkedList<Gameobject> exp = m.getObjects();
        
        for (int i = 0; i < ctrl.length; i++) {
            String[] c = ctrl[i].split(" ");
            Gameobject current =  exp.get(i);

            if (current.getX() != Integer.parseInt(c[0]) ||
                current.getY() != Integer.parseInt(c[1]) ||
                current.getObjectType() != ObjectType.valueOf(c[2]) ) return false;
        }
        return true;
    }

    private void test() {
        Files fileOne = new Files("MapFiles/Map1.txt");
        Leaderboard lbOne = new Leaderboard(new File("Database/Leaderboard.text"));
        Map m = new Map(1280, 720, 80, fileOne, lbOne);
        loadMap(m);
        
        claim(
            checkFirst(m, "0000000000000000",
                          "0015555555555200",
                          "0060000000000600",
                          "0060000000000600",
                          "0060000000000600",
                          "0060000000000600",
                          "0060000000000600",
                          "0035555555555400",
                          "0000000000000000") 
        );
        claim(
            checkSec(m , "530 340 LINE",
                         "890 170 NAND",
                         "1100 650 OBSTACLE")
        );

        System.out.println("All tests passed");
    }

    private void run() {
        test();
    }

    public static void main(String[] args) {
        Files f = new Files("MapFiles/test.txt");
        f.run();
    }
}
