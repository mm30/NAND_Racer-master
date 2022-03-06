// this class saves integer values corresponding to the tile type

import java.util.LinkedList;

class Map {
    // leaderboard and file details
    private Files file;
    private Leaderboard leaderboard;

    // final lap# = maxLaps
    private int maxLaps = 3;
    private int currentLap;

    // size of map and window
    private double mapWidth; // window width in pixels
    private double mapHeight; // window height
    private int tileSize;
    private int gridWidth;
    private int gridHeight;

    // data of the map
    private int background; // first layer
    private int[][] grid; // second layer (the track)
    private LinkedList<Gameobject> objects; // third layer (objects)
    
    // map without leaderboard nor leaderboard file
    Map(int screenSizeW, int screenSizeH, int tileSize){
        this.mapWidth = screenSizeW;
        this.mapHeight = screenSizeH;
        this.tileSize = tileSize;
        this.gridWidth = screenSizeW/tileSize;
        this.gridHeight = screenSizeH/tileSize;
        this.currentLap = 0;
        grid = new int[gridHeight][gridWidth];
        objects = new LinkedList<>();
    }
    // arguments refer to width, height, and tilesize (in pixels)
    Map(int screenSizeW, int screenSizeH, int tileSize, Files file, Leaderboard leaderboard){
        this.file = file;
        this.leaderboard = leaderboard;
        this.mapWidth = screenSizeW;
        this.mapHeight = screenSizeH;
        this.tileSize = tileSize;
        this.gridWidth = screenSizeW/tileSize;
        this.gridHeight = screenSizeH/tileSize;
        this.currentLap = 0;
        grid = new int[gridHeight][gridWidth];
        objects = new LinkedList<>();
    }

    // import map
    void insertBackground(int b) { background = b; }

    void insertTrack(int[][] t) {
        for(int i = 0; i < gridHeight; i++){
            for(int j = 0; j < gridWidth; j++){
                grid[i][j] = t[i][j];
            }
        }
    }

    void insertObject(Gameobject ob) { objects.add(ob); }

    // getters for size fields
    int getGridHeight(){ return gridHeight;}
    int getGridWidth(){ return gridWidth;}
    int getTileSize(){ return tileSize;}
    double getMapWidth(){ return mapWidth;}
    double getMapHeight(){ return mapHeight;}

    int getCurrentLap() { return currentLap; }
    void resetLaps(){ currentLap = 0;}
    int getMaxLaps() { return maxLaps; }
    void addLap() { currentLap++; }

    int getBackground() { return background; }

    int getTile(int i, int j){
        if((i < 0 || i > gridHeight) ||
           (j < 0 || j > gridWidth)) {
            throw new Error("Invalid integer value for array");
        }
        return grid[i][j];
    }
   
    LinkedList<Gameobject> getObjects() { return objects; }

    Gameobject startLine() {
        for (Gameobject ob : objects) {
            if (ob.getObjectType() == ObjectType.LINE) return ob;
        }
        throw new Error("Starting line is missing");
    }

    Files getFile(){
        return file;
    }
    Leaderboard getLeaderboard(){
        return leaderboard;
    }
}
