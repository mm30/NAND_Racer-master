import javafx.application.*;
import javafx.animation.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.media.AudioClip;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.transform.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.LinkedList;

public class Test extends Application {
    /*----------------------------------------Assets--------------------------------------------*/
    private Image carImage = new Image("Game_Assets/Car.png");
    //speedometer
    private Image meter = new Image("Game_Assets/Speedometer.png");
    private Image needle = new Image("Game_Assets/Needle.png");

    // track layer
    private Image toplcorner = new Image("Game_Assets/topLeftCorner.png");
    private Image toprcorner = new Image("Game_Assets/topRightCorner.png");
    private Image botlcorner = new Image("Game_Assets/bottomLeftCorner.png");
    private Image botrcorner = new Image("Game_Assets/bottomRightCorner.png");
    private Image horizontal = new Image("Game_Assets/horizontal.png");
    private Image vertical = new Image("Game_Assets/vertical.png");

    // background layer
    private Image bg1 = new Image("Game_Assets/img1.jpg");
    private Image bg2 = new Image("Game_Assets/img2.jpg");

    // object layer
    private Image line = new Image("Game_Assets/line.png");
    private Image not = new Image("Game_Assets/NOT_Gate.png");
    private Image and = new Image("Game_Assets/AND_Gate_Right.png");
    private Image nand = new Image("Game_Assets/NAND_Gate.png");
    private Image or = new Image("Game_Assets/OR_Gate.png");

    //TODO assets needed for obstacles
    private Image obstacle = new Image("Game_Assets/line.png");

    //instance declared for the rotation of needle
    private Rotate setRotation = new Rotate();

    //pause game background
    private Image lightCycle = new Image("http://www.zgjm-org.com/data/out/12/IMG_581124.jpg", 800, 640, true, true);

    //start game background
    private Image tron = new Image("Game_Assets/loading_screen.jpg");

    //music file location
    final URL bgm = getClass().getResource("/Game_Assets/RACER.wav");

    /*-------------------------------Object models and views------------------------------------*/
    // models
    private Vehicle car;
    private Map map = new Map(1280, 720, 80);
    private Clock clk = initClock(60, 0);
    private LineDetector ld;
    private Speedometer speedometer = new Speedometer();

    // views
    private Rectangle c;

    private Text raceTimer = new Text("I won the last game");
    private Button testButton = new Button();
    private Label timerLabel = new Label();
    private ImageView meterView = new ImageView(meter);
    private ImageView needleView = new ImageView(needle);

    // widgets
    private AnchorPane root = new AnchorPane();
    private Button startButton = new Button();
    private Button highScoreButton = new Button();

    AnimationTimer timer = new AnimationTimer() {
        long prevTime = 0, elapsed;
        double dt;

        @Override
        public void handle(long now) {
            if (prevTime == 0) prevTime = now;
            elapsed = now - prevTime; //nano seconds
            dt = (double) elapsed / Math.pow(10, 9);
            update(dt);
            render();

            prevTime = now;
        }
    };

    public void start(Stage stage){
        // audio clip
        AudioClip clip = new AudioClip(bgm.toString());

        // set window bar title
        stage.setTitle("NAND Racer");
        stage.setScene(loadingScene());
        stage.setResizable(false);

        startButton.setOnAction(
                e->{
                    stage.setScene(gameScene());
                    stage.setResizable(true);

                    // plays audio at 50% volume
                    clip.play(0.5);

                    stage.getScene().setOnKeyPressed( event-> {
                        if (event.getCode() == KeyCode.UP) car.accelerator("stepped on", "forward");
                        else if (event.getCode() == KeyCode.DOWN) car.accelerator("stepped on", "backward");
                        else if (event.getCode() == KeyCode.LEFT) car.wheel("left");
                        else if (event.getCode() == KeyCode.RIGHT) car.wheel("right");
                        //else if (event.getCode() == KeyCode.SPACE) car.setSpace();
                    });
                    stage.getScene().setOnKeyReleased( event-> {
                        if (event.getCode() == KeyCode.UP) car.accelerator("released", "forward");
                        else if (event.getCode() == KeyCode.DOWN) car.accelerator("released", "backward");
                        else if (event.getCode() == KeyCode.LEFT) car.wheel("released");
                        else if (event.getCode() == KeyCode.RIGHT) car.wheel("released");
                        //else if (event.getCode() == KeyCode.SPACE) car.setSpace();
                    });
                    timer.start();
                    stage.show();
                }
        );
        stage.show();
    }

    /*Scenes*/
    private Scene loadingScene() {
        Text title = new Text();
        ImageView iV = new ImageView(tron);

        // set fixed size 600x400 for start game screen
        Scene scene = new Scene(root, 600, 400);

        // sets image; can be changed to lightCycle for pause game screen or alternative start game screen
        // improves performance
        iV.setSmooth(true);
        iV.setCache(true);
        // maintains image integrity
        iV.setPreserveRatio(true);
        iV.setFitWidth(800);
        iV.setFitHeight(600);
        iV.setLayoutX(-70);
        iV.setLayoutY(-30);

        title.setLayoutX(31);
        title.setLayoutY(84);
        title.setText("> NAND Racer");
        title.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/ScifiAdventure.otf"), 36));
        title.setFill(Color.rgb(54,  196,  225));

        // (x,y) position for start button
        startButton.setLayoutX(259);
        startButton.setLayoutY(252);
        startButton.setText("Start Race");
        highScoreButton.setLayoutX(253);
        highScoreButton.setLayoutY(294);
        highScoreButton.setText("High Scores");

        // add nodes
        root.getChildren().add(iV);
        root.getChildren().add(title);
        root.getChildren().add(startButton);
        root.getChildren().add(highScoreButton);
        return scene;
    }

    private Scene gameScene() {
        /*game scene*/
        Canvas canvas = new Canvas(map.getMapWidth(), map.getMapHeight());
        GraphicsContext g = canvas.getGraphicsContext2D();
        Group root = new Group(canvas);
        Scene scene = new Scene(root);

        timerLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Regular.ttf"), 24));
        timerLabel.setTextFill(Color.rgb(54,  196,  225));
        // binding (TextField) raceTimer time to timerLabel...
        timerLabel.textProperty().bind(raceTimer.textProperty());

        // Speedometer: meter and needle
        meterView.setLayoutX(1100);
        meterView.setLayoutY(640);
        needleView.setLayoutX(1175);
        needleView.setLayoutY(718);
        //sets piviot point for needle
        setRotation.setPivotX(0);
        needleView.getTransforms().addAll(setRotation);

        // add timer to game scene here
        root.getChildren().add(timerLabel);
        root.getChildren().add(meterView);
        root.getChildren().add(needleView);

        assignTrack("MapFiles/Map1.txt");
        drawMap(g, root);
        detectorInit(map.startLine());

        root.getChildren().add(CreateCar());
        drawVehicle();
        return scene;
    }

    /*Graphics*/
    private Image getBackImage(int b) {
        Image result;
        switch (b) {
            case 1:
                result = bg1; break;
            case 2:
                result = bg2; break;
            default: throw new Error("Required background doesn't exist");
        }
        return result;
    }

    private void drawBackground(GraphicsContext g) {
        g.drawImage(getBackImage(map.getBackground()), 0, 0);
    }

    // changes currentImage
    private Image getImage(int n){
        Image result;

        switch (n) {
            case 1:
                result = toplcorner;
                break;
            case 2:
                result = toprcorner;
                break;
            case 3:
                result = botlcorner;
                break;
            case 4:
                result = botrcorner;
                break;
            case 5:
                result = horizontal;
                break;
            case 6:
                result = vertical;
                break;
            default:
                throw new Error("Tile can't be recognised");
        }
        return result;
    }

    // draws images from map onto window
    private void drawTrack(GraphicsContext g){
        for (int i = 0; i < map.getGridHeight(); i++){
            for (int j = 0; j < map.getGridWidth(); j++){

                // if tile value isn't 0, draw relevant image
                if (map.getTile(i, j) != 0){
                    g.drawImage(getImage(map.getTile(i, j)),
                            j * map.getTileSize(),
                            i * map.getTileSize());
                }
            }
        }
    }

    //gets images for objects
    private Image getObImage(ObjectType t) {
        Image result;
        switch (t) {
            case LINE:
                result = line; break;
            case NOT:
                result = not; break;
            case AND:
                result = and; break;
            case NAND:
                result = nand; break;
            case OR:
                result = or; break;
            case OBSTACLE:
                result = obstacle; break;
            default:
                throw new Error("Object type needed for assigning image");
        }
        return result;
    }

    // draws object layer
    private void drawObjects(Group r){
        LinkedList<Gameobject> objects = map.getObjects();

        for (Gameobject ob : objects){
            Image temp = getObImage(ob.getObjectType());

            ImageView obIV = new ImageView(temp);
            obIV.setX(ob.getX() - temp.getWidth() / 2);
            obIV.setY(ob.getY() - temp.getHeight() / 2);
            obIV.setRotate(ob.getDirection());
            r.getChildren().add(obIV);
        }
    }

    private void drawMap(GraphicsContext g, Group r) {
        drawBackground(g);
        drawTrack(g);
        drawObjects(r);
    }

    private void drawVehicle() { c.setFill(new ImagePattern(carImage)); }

    /*models*/
    private Rectangle CreateCar() {
        Gameobject startLine = map.startLine();
        double startDir = Math.toRadians(startLine.getDirection());

        double startX = startLine.getX() + (-1) * 40 * Math.cos(startDir);
        double startY = startLine.getY() + (-1) * 40 * Math.sin(startDir);

        car = new Vehicle(startX, startY, startLine.getDirection(), 30, 40, 130, 70);
        c = new Rectangle();

        //Rectangle position is set with the upper left corner rather than the centre
        c.setX(car.position().x() - car.length() / 2);
        c.setY(car.position().y() - car.width() / 2);
        c.setWidth(car.length()); // Car rectangle lies horizontally in the beginning
        c.setHeight(car.width());
        return c;
    }

    private void assignTrack(String filename) {
        Files f = new Files(filename);
        f.loadMap(map);
    }

    // initialise time
    private Clock initClock(double endT, double startT) {
        Clock clk = null;
        clk = new Clock(endT, startT, clk);
        return clk;
    }

    // initialise line crossing detector
    private void detectorInit(Gameobject l) { ld = new LineDetector(l); }

    /*--------------------------------------Game loop-------------------------------------------*/
    private void update(double dt) {
        if (map.getCurrentLap() >= 1 && map.getCurrentLap() < map.getMaxLaps() + 1) clk.tick(dt);
        
        speedometer.setSpeed(car);

        car.collision(map);
        car.motion(dt);

        if (ld.detect(car)) {
            map.addLap();
            ld.reset();
            for (Gameobject ob : map.getObjects()) ob.activate();
        }
        // Testing: prints timer while race is active during laps 1-3
        System.out.println(map.getCurrentLap() + "," + clk.getTime());
    }

    /*rendering*/
    private void render() {
        render_car();
        render_clk();
        render_speedo();
    }

    private void render_car() {
        c.setRotate(car.direction());
        c.setX(car.position().x() - car.length() / 2);
        c.setY(car.position().y() - car.length() / 2);
    }

    // uni-direction binding to timerLabel
    private void render_clk() { raceTimer.setText(clk.getTime()); }
    private void render_speedo() { setRotation.setAngle(speedometer.getAngle()); }
}
