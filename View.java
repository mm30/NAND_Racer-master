import javafx.application.*;
import javafx.animation.AnimationTimer;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import javafx.scene.media.AudioClip;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.util.LinkedList;
import java.util.Random;
import java.io.File;
import java.net.URL;

public class View extends Application {
    // final fields for sizes
    private final int numberOfMaps = 6;
    private final int pixelWidth = 1280;
    private final int pixelHeight = 720;
    private final int tileSize = 80;

    // popup box for question windows
    private Box box = new Box();

    // gamemode
    private GameMode gamemode;

    // individual map names
    private String mapOneName = "NOT";
    private String mapTwoName = "OR";
    private String mapThreeName = "AND";
    private String mapFourName = "NAND";
    private String mapFiveName = "APPRENTICE";
    private String mapSixName = "MASTER";

    // leaderboard fields
    private String username;
    private double time;
    private Leaderboard scores;
    private String currentLeaderboard;

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
    private Image boolInput = new Image("Game_Assets/0&1.png");

    // object layer
    private Image line = new Image("Game_Assets/line.png");
    private Image not = new Image("Game_Assets/NOT_Gate.png");
    private Image and = new Image("Game_Assets/AND_Gate_Right.png");
    private Image nand = new Image("Game_Assets/NAND_Gate.png");
    private Image or = new Image("Game_Assets/OR_Gate.png");

    //instance declared for the rotation of needle
    private Rotate setRotation = new Rotate();

    // game backgrounds
    private Image tron = new Image("Game_Assets/loading_screen.jpg");
    private Image leaderboardBackground = new Image("Game_Assets/imgpausegame.jpg");
    private Image normalGameBackground = new Image("Game_Assets/img2.jpg");
    private Image popupMenuBackground = new Image("Game_Assets/menuback.jpg");
    private Image displayEndMessageBackground = new Image("Game_Assets/mainmenu.jpeg");
    private Image finishedGameBackground = new Image("Game_Assets/finishedGame.jpeg");
    private Image scoreboardbg = new Image("Game_Assets/scoreboardbg.jpg");
    private Image lightCycle = new Image("Game_Assets/lightCycle.jpg", 800, 640, true, true);

    // question scenes
    private Image Q_Car1 = new Image("Game_Assets/Question_carBig1.png");
    private Image Q_Car2 = new Image("Game_Assets/Question_carBig2.png");
    private Image Q_Car3 = new Image("Game_Assets/Question_carBig3.png");
    private Image Q_Car4 = new Image("Game_Assets/Question_carBig4.png");
    // answers for the questions
    private boolean answerOne = false;
    private boolean answerTwo = false;
    private boolean answerThree = false;
    private boolean answerFour = false;

    //music file location
    final URL bgm = getClass().getResource("/Game_Assets/RACER.wav");
    // videos
    private Media notIntroductionVideo = new Media(getClass().getResource("Game_Assets/NOT_tutorial.mp4").toExternalForm());
    private Media orIntroductionVideo = new Media(getClass().getResource("Game_Assets/OR_tutorial.mp4").toExternalForm());
    // labels for videos
    private Label explainGateInfo = new Label("This is a NOT gate. When you pass through it, \n the Boolean value will change from 0 to 1 \n or vice versa.");
    private Label explaintSpacebarOrGate = new Label("This is a OR gate. When you pass through it, \n you can press the spacebar \n to change the SPACE value from 0 to 1 \n ");

    /*-------------------------------Object models and views------------------------------------*/
    // models
    private Vehicle car;
    // maps
    private LinkedList<Map> maps = new LinkedList<Map>();
    private int currentMap = 0;

    private Clock clk = initClock(60, 0);
    private LineDetector ld;
    private Speedometer speedometer = new Speedometer();

    // views
    private Rectangle c;
    private Text raceTimer = new Text();
    private Button testButton = new Button();
    private Label timerLabel = new Label();

    //bool value labels
    private Text carBool = new Text();
    private Text spaceBool = new Text();
    private Text lapCount = new Text();
    private Label carBoolLab = new Label();
    private Label spaceLabel = new Label();
    private Label lapLabel = new Label();

    private ImageView meterView = new ImageView(meter);
    private ImageView needleView = new ImageView(needle);

    // widgets
    private AnchorPane root = new AnchorPane();
    private Button startButton = new Button();
    private Button highScoreButton = new Button();

    // scene building fields
    private Stage window;
    private Scene currentScene;
    private AnchorPane root0;

    // music
    private AudioClip music;

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

    // create the starting scene
    public void start(Stage stage){
        getMapData();
        createStartScene(stage);
        getMusic();
        getBackground(root0, tron);
        startButtonsAndText();
        window.show();
    }

    // gets the data for all of the maps
    private void getMapData() {
        Files fileOne = new Files("MapFiles/Map1.txt");
        Leaderboard lbOne = new Leaderboard(new File("Database/Leaderboard.text"));
        Map mapOne = new Map(pixelWidth, pixelHeight, tileSize, fileOne, lbOne);
        maps.add(mapOne);

        Files fileTwo = new Files("MapFiles/Map2.txt");
        Leaderboard lbTwo = new Leaderboard(new File("Database/Leaderboard2.text"));
        Map mapTwo = new Map(pixelWidth, pixelHeight, tileSize, fileTwo, lbTwo);
        maps.add(mapTwo);

        Files fileThree = new Files("MapFiles/Map3.txt");
        Leaderboard lbThree = new Leaderboard(new File("Database/Leaderboard3.text"));
        Map mapThree = new Map(pixelWidth, pixelHeight, tileSize, fileThree, lbThree);
        maps.add(mapThree);

        Files fileFour = new Files("MapFiles/Map4.txt");
        Leaderboard lbFour = new Leaderboard(new File("Database/Leaderboard4.text"));
        Map mapFour = new Map(pixelWidth, pixelHeight, tileSize, fileFour, lbFour);
        maps.add(mapFour);

        Files fileFive = new Files("MapFiles/Map5.txt");
        Leaderboard lbFive = new Leaderboard(new File("Database/Leaderboard5.text"));
        Map mapFive = new Map(pixelWidth, pixelHeight, tileSize, fileFive, lbFive);
        maps.add(mapFive);

        Files fileSix = new Files("MapFiles/Map6.txt");
        Leaderboard lbSix = new Leaderboard(new File("Database/Leaderboard6.text"));
        Map mapSix = new Map(pixelWidth, pixelHeight, tileSize, fileSix, lbSix);
        maps.add(mapSix);
    }

    // create the start scene
    private void createStartScene(Stage stage){
        window = stage;
        window.setTitle("NAND Racer");
        root0 = new AnchorPane();
        currentScene = new Scene(root0, 600, 400);
        window.setScene(currentScene);
        window.setResizable(false);
    }

    // get music data
    private void getMusic(){ music = new AudioClip(bgm.toString()); }

    // image view for the first scene
    private void getBackground(Pane pane, Image image){
        ImageView iV = new ImageView();
        iV.setImage(image);
        // improves performance
        iV.setSmooth(true);
        iV.setCache(true);
        // maintains image integrity
        iV.setPreserveRatio(true);
        iV.setFitWidth(800);
        iV.setFitHeight(600);
        iV.setLayoutX(-70);
        iV.setLayoutY(-30);
        pane.getChildren().add(iV);
    }

    // buttons for the start scene
    private void startButtonsAndText(){
        Text title = createHeading("> NAND Racer");
        // quick game
        Button quick = new Button("Quick Game");
        quick.setOnAction( e->{
            gamemode = GameMode.QUICK;
            randomMap();
            playGame();
        });
        // normal game
        Button start = new Button("Normal Game");
        start.setOnAction( e->{ normalGame(); } );

        // high scores
        Button highScores = new Button("High Scores");
        highScores.setOnAction( e->{
            whichLeaderboard();
        });
        // organise with VBox
        VBox box = new VBox();
        box.getChildren().addAll(quick, start, highScores);
        box.setSpacing(14);
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(254);
        box.setLayoutY(200);
        root0.getChildren().addAll(title, box);
    }
    private void questionWindow(){
        AnchorPane anchorpane = new AnchorPane();
        switch(currentMap){
            case 0: setPane1(anchorpane); break;
            case 1: setPane2(anchorpane); break;
            case 2: setPane3(anchorpane); break;
            case 3: setPane4(anchorpane); break;
            case 4: setPane5(anchorpane); break;
            case 5: setPane6(anchorpane); break;
        }
        currentScene = new Scene(anchorpane, 600, 400);
        currentScene.getStylesheets().add("style.css");
        window.setScene(currentScene);
        window.show();
    }

    // transition panes
    private void setPane1(AnchorPane questionList){

        HBox buttonsRow = new HBox(30);
        Button wrongAns = new Button();
        Button correctAns = new Button();
        Label statementCongrats = new Label("Congratulations you have passed level 1!");
        Label statementGate = new Label("This level was about NOT gates");
        Label QuestionNotGate1 = new Label("If your car has value of 1 before driving through this gate");
        Label QuestionNotGate2 = new Label("What value do you have after?");
        Label QuestionNotGate3 = new Label("click the correct answer below to proceed...");
        VBox vboxList = makeQuestionLabelVbox(correctAns, wrongAns, not, statementCongrats, statementGate,
                                              QuestionNotGate1, QuestionNotGate2, QuestionNotGate3);
        vboxList.setLayoutX(80);
        vboxList.setLayoutY(30);
        buttonsRow.setLayoutX(230);
        buttonsRow.setLayoutY(300);
        wrongAns.setOnAction(e->{
            box.show("Oops!", "Incorrect, try again");
            wrongAnswer(wrongAns);
        });
        correctAns.setOnAction(e->{
            correctAnswer(correctAns);
            resetButton(wrongAns);
            displayEndMessage();
        });
        buttonsRow.getChildren().addAll(wrongAns, correctAns);
        getBackground(questionList, bg1);
        questionList.getChildren().addAll(vboxList, buttonsRow);
    }

    private void setPane2(AnchorPane questionList){

        HBox buttonsRow = new HBox(30);
        Button wrongAns = new Button();
        Button correctAns = new Button();
        Label statementCongrats = new Label("Congratulations you have passed level 2!");
        Label statementGate = new Label("This level was about OR Gates");
        Label QuestionORGate1 = new Label("If your car has value of 1 and spacebar has value 0");
        Label QuestionORGate2 = new Label("What value do you have after the OR Gate?");
        Label QuestionORGate3 = new Label("click the correct answer below to proceed...");
        VBox vboxList = makeQuestionLabelVbox(wrongAns, correctAns, or, statementCongrats, statementGate,
                                              QuestionORGate1, QuestionORGate2, QuestionORGate3);
        vboxList.setLayoutX(95);
        vboxList.setLayoutY(30);
        buttonsRow.setLayoutX(230);
        buttonsRow.setLayoutY(300);
        correctAns.setOnAction(e->{
            resetButton(wrongAns);
            correctAnswer(correctAns);
            displayEndMessage();
        });
        wrongAns.setOnAction(e->{
            box.show("Oops!", "Incorrect, try again");
            wrongAnswer(wrongAns);
        });
        buttonsRow.getChildren().addAll(wrongAns, correctAns);
        getBackground(questionList, bg1);
        questionList.getChildren().addAll(vboxList, buttonsRow);
    }

    private void setPane3(AnchorPane questionList){

        HBox buttonsRow = new HBox(30);
        Button correctAns = new Button();
        Button wrongAns = new Button();
        Label statementCongrats = new Label("Congratulations you have passed level 3!");
        Label statementGate = new Label("This level was about an AND GATE");
        Label QuestionAndGate1 = new Label("If your car has value of 1 and spacebar has value 0");
        Label QuestionAndGate2 = new Label("What value do you have after the AND Gate?");
        Label QuestionAndGate3 = new Label("click the correct answer below to proceed...");
        VBox vboxList = makeQuestionLabelVbox(correctAns, wrongAns, and, statementCongrats,
                                              statementGate, QuestionAndGate1, QuestionAndGate2, QuestionAndGate3);
        vboxList.setLayoutX(95);
        vboxList.setLayoutY(30);
        buttonsRow.setLayoutX(230);
        buttonsRow.setLayoutY(300);
        wrongAns.setOnAction(e->{
            box.show("Oops!", "Incorrect, try again");
            wrongAnswer(wrongAns);
        });
        correctAns.setOnAction(e->{
            correctAnswer(correctAns);
            resetButton(wrongAns);
            displayEndMessage();
        });
        buttonsRow.getChildren().addAll(wrongAns, correctAns);
        getBackground(questionList, bg1);
        questionList.getChildren().addAll(vboxList, buttonsRow);
    }


    private void setPane4(AnchorPane questionList){

        HBox buttonsRow = new HBox(30);
        Button correctAns = new Button();
        Button wrongAns = new Button();
        Label statementCongrats = new Label("Congratulations you have passed level 4!");
        Label statementGate = new Label("This level was about a NAND GATE");
        Label QuestionNandGate1 = new Label("If your car has value of 1 and spacebar has value 0");
        Label QuestionNandGate2 = new Label("What value do you have after the NAND gate?");
        Label QuestionNandGate3 = new Label("click the correct answer below to proceed...");
        VBox vboxList = makeQuestionLabelVbox(wrongAns, correctAns, nand, statementCongrats, statementGate,
                                              QuestionNandGate1, QuestionNandGate2, QuestionNandGate3);
        vboxList.setLayoutX(95);
        vboxList.setLayoutY(30);
        buttonsRow.setLayoutX(230);
        buttonsRow.setLayoutY(300);
        wrongAns.setOnAction(e->{
            box.show("Oops!", "Incorrect, try again");
            wrongAnswer(wrongAns);
        });
        correctAns.setOnAction(e->{
            correctAnswer(correctAns);
            resetButton(wrongAns);
            displayEndMessage();
        });
        buttonsRow.getChildren().addAll(wrongAns, correctAns);
        getBackground(questionList, bg1);
        questionList.getChildren().addAll(vboxList, buttonsRow);
    }

    private void setPane5(AnchorPane questionList){
        resetAnswers();

        HBox questionsRow = new HBox(150);
        Label question1 = new Label("Q1");
        Label question2 = new Label("Q2");
        Label question3 = new Label("Q3");
        Label question4 = new Label("Q4");
        Label statementAnswer = new Label("Answer these questions to become a NAND Racer!");
        Label statementGate = new Label("What is the name of the following gates?");

        setLableStyle(question1);
        setLableStyle(question2);
        setLableStyle(question3);
        setLableStyle(question4);
        setLableStyle(statementAnswer);
        setLableStyle(statementGate);

        GridPane.setConstraints(statementAnswer, 0,0);
        GridPane.setConstraints(statementGate, 0,1);
        questionsRow.setPadding(new Insets(10,10,100,10));
        questionsRow.getChildren().addAll(question1, question2, question3, question4);
        GridPane.setConstraints(questionsRow, 0,2);

        Button NOTAns1 = new Button("NOT GATE");
        Button ORAns1 = new Button("OR GATE");
        Button ANDAns1 = new Button("AND GATE");
        Button NANDAns1 = new Button("NAND GATE");

        NOTAns1.setOnAction(e->{ answerOne = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(NOTAns1);
            resetButton(ORAns1);
            resetButton(ANDAns1);
            resetButton(NANDAns1);
        });
        ORAns1.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ORAns1);
        });
        ANDAns1.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ANDAns1);
        });
        NANDAns1.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NANDAns1);
        });
        VBox vboxList1 = new VBox(10);
        vboxList1.getChildren().addAll(NOTAns1, ORAns1, ANDAns1, NANDAns1);

        Button NOTAns2 = new Button("NOT GATE");
        Button ORAns2 = new Button("OR GATE");
        Button ANDAns2 = new Button("AND GATE");
        Button NANDAns2 = new Button("NAND GATE");

        NOTAns2.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NOTAns2);
        });
        ORAns2.setOnAction(e->{ answerTwo = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(ORAns2);
            resetButton(NOTAns2);
            resetButton(ANDAns2);
            resetButton(NANDAns2);
        });
        ANDAns2.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ANDAns2);
        });
        NANDAns2.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NANDAns2);
        });
        VBox vboxList2 = new VBox(10);
        vboxList2.getChildren().addAll(NOTAns2, ORAns2, ANDAns2, NANDAns2);

        Button NOTAns3 = new Button("NOT GATE");
        Button ORAns3 = new Button("OR GATE");
        Button ANDAns3 = new Button("AND GATE");
        Button NANDAns3 = new Button("NAND GATE");
        NOTAns3.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NOTAns3);
        });
        ORAns3.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ORAns3);
        });
        ANDAns3.setOnAction(e->{ answerThree = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(ANDAns3);
            resetButton(NOTAns3);
            resetButton(ORAns3);
            resetButton(NANDAns3);
        });
        NANDAns3.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NANDAns3);
        });
        VBox vboxList3 = new VBox(10);
        vboxList3.getChildren().addAll(NOTAns3, ORAns3, ANDAns3, NANDAns3);

        Button NOTAns4 = new Button("NOT GATE");
        Button ORAns4 = new Button("OR GATE");
        Button ANDAns4 = new Button("AND GATE");
        Button NANDAns4 = new Button("NAND GATE");
        NOTAns4.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NOTAns4);
        });
        ORAns4.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ORAns4);
        });
        ANDAns4.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ANDAns4);
        });
        NANDAns4.setOnAction(e->{ answerFour = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(NANDAns4);
            resetButton(NOTAns4);
            resetButton(ORAns4);
            resetButton(ANDAns4);
        });

        VBox vboxList4 = new VBox(10);
        vboxList4.getChildren().addAll(NOTAns4, ORAns4, ANDAns4, NANDAns4);

        HBox buttonsAll = new HBox(70);
        buttonsAll.getChildren().addAll(vboxList1, vboxList2, vboxList3, vboxList4);

        GridPane.setConstraints(buttonsAll, 0,5);
        getBackground(questionList, bg1);
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(0,0,0,0));
        layout.setVgap(0);
        layout.setHgap(0);
        layout.getChildren().addAll(statementAnswer, statementGate, questionsRow, buttonsAll);

        setImageLocation(questionList, not,20, 110);
        setImageLocation(questionList, or,180, 110);
        setImageLocation(questionList, and,340, 110);
        setImageLocation(questionList, nand,520, 110);

        questionList.getChildren().addAll(layout);
    }
    private void resetAnswers(){
        answerOne = false;
        answerTwo = false;
        answerThree = false;
        answerFour = false;
    }
    private void checkAnswers(boolean one, boolean two, boolean three, boolean four){
        if ((one == true) && (two == true) && (three == true) && (four == true)){
            if (currentMap == 4){
                displayEndMessage();
            }
            if (currentMap == 5){
                finishedGameMessage();
            }
        }
    }

    private void setPane6(AnchorPane questionList){
        // reset booleans for the answers
        resetAnswers();
        Label introLabel1 = new Label("Answer these questions to become a NAND Racer!");
        Label introLabel2 = new Label("What is the output result of the following gates?");
        Label q1 = new Label("Q1");
        Label q2 = new Label("Q2");
        Label q3 = new Label("Q3");
        Label q4 = new Label("Q4");
        VBox vboxList1 = new VBox(30);
        VBox vboxList2 = new VBox(30);
        VBox vboxList3 = new VBox(30);
        VBox vboxList4 = new VBox(30);
        ImageView bool1 = new ImageView(boolInput);
        ImageView bool2 = new ImageView(boolInput);
        ImageView bool3 = new ImageView(boolInput);
        ImageView bool4 = new ImageView(boolInput);
        Button NOTAns1 = new Button();
        Button NOTAns2 = new Button();
        Button NOTAns3 = new Button();
        Button NOTAns4 = new Button();
        Button ORAns1 = new Button();
        Button ORAns2 = new Button();
        Button ORAns3 = new Button();
        Button ORAns4 = new Button();
        HBox setOfQuestions = new HBox();
        HBox booleanIn = new HBox(102);
        setOfQuestions.getChildren().addAll(
        makeQuestionVBox(vboxList1, not, NOTAns1, ORAns1, q1),
        makeQuestionVBox(vboxList2, or, NOTAns2, ORAns2, q2),
        makeQuestionVBox(vboxList3, and, NOTAns3, ORAns3, q3),
        makeQuestionVBox(vboxList4, nand, NOTAns4, ORAns4, q4) );
        setOfQuestions.setAlignment(Pos.CENTER);
        setOfQuestions.setSpacing(90);
        setOfQuestions.setLayoutY(120);
        setOfQuestions.setLayoutX(70);
        booleanIn.getChildren().addAll(bool1, bool2, bool3, bool4);
        booleanIn.setLayoutX(50);
        booleanIn.setLayoutY(177);
        setLable(introLabel1, 110, 20);
        setLable(introLabel2, 115, 50);

        NOTAns1.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NOTAns1);
        });
        ORAns1.setOnAction(e->{ answerOne = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(ORAns1);
            resetButton(NOTAns1);
        });
        NOTAns2.setOnAction(e->{ answerTwo = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(NOTAns2);
            resetButton(ORAns2);
        });
        ORAns2.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ORAns2);
        });
        NOTAns3.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(NOTAns3);
        });
        ORAns3.setOnAction(e->{ answerThree = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(ORAns3);
            resetButton(NOTAns3);
        });
        NOTAns4.setOnAction(e->{ answerFour = true; checkAnswers(answerOne, answerOne, answerThree, answerFour);
            correctAnswer(NOTAns4);
            resetButton(ORAns4);
        });
        ORAns4.setOnAction(e->{ box.show("Oops!", "Incorrect, try again");
            wrongAnswer(ORAns4);
        });
        // questionList is argument in for this method; bg1 = racing game background
        getBackground(questionList, bg1);
        questionList.getChildren().addAll(introLabel1, introLabel2, booleanIn, setOfQuestions);
    }

    //makes vbox from each coloumn
    private VBox makeQuestionVBox(VBox questionVBox, Image gate, Button btn1, Button btn0, Label question){
        ImageView imageView = new ImageView(gate);
        btn0.setText("  0  ");
        btn1.setText("  1  ");
        questionVBox.getChildren().addAll(question, imageView, btn1, btn0);
        questionVBox.setAlignment(Pos.CENTER);
        return questionVBox;
    }

    //function to make vbox for q1-4
    private VBox makeQuestionLabelVbox(Button btn0, Button btn1, Image gate, Label state1, Label state2, Label quesPt1, Label quesPt2, Label quesPt3){
        VBox vboxQues = new VBox(15);
        ImageView imageView = new ImageView(gate);
        btn0.setText("  0  ");
        btn1.setText("  1  ");
        setLableStyle(state1);
        setLableStyle(state2);
        setLableStyle(quesPt1);
        setLableStyle(quesPt2);
        setLableStyle(quesPt3);
        vboxQues.getChildren().addAll(state1, state2, imageView, quesPt1, quesPt2, quesPt3);
        vboxQues.setAlignment(Pos.CENTER);
        return vboxQues;
    }

    //function to format questions - improves clarity/asthetics
    private void setLable(Label label, int x, int y){
        label.getStyleClass().removeAll("label");
        label.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Bold.ttf"),22));
        label.setTextFill(Color.SNOW);
        label.setLayoutX(x);
        label.setLayoutY(y);
    }

    //function to simply apply style properties to a label
    private void setLableStyle(Label label){
        label.getStyleClass().removeAll("label");
        label.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Bold.ttf"),22));
        label.setTextFill(Color.SNOW);
    }

    private void setImageLocation(Pane pane, Image image, int i, int j){
        ImageView iV = new ImageView();
        iV.setImage(image);
        // improves performance
        iV.setSmooth(true);
        iV.setCache(true);
        // maintains image integrity
        iV.setFitWidth(60);
        iV.setFitHeight(50);
        iV.setLayoutX(i);
        iV.setLayoutY(j);
        pane.getChildren().add(iV);
    }
    // first tutorial method
    private void tutorialScreen(Media video, Label messageForVideo) {

        AnchorPane aPane = new AnchorPane();
        Button continueButton = new Button(" -> ");
        ImageView iV = new ImageView();
        MediaPlayer tutorialPlayer = new MediaPlayer(video);
        MediaView mediaView = new MediaView(tutorialPlayer);
        // set fixed size 600x400 for start game screen
        Scene loadingScene = new Scene(aPane, 600, 400);
        // settings for background
        iV.setImage(lightCycle);
        iV.setSmooth(true);
        iV.setCache(true);
        iV.setPreserveRatio(true);
        iV.setFitWidth(800);
        iV.setFitHeight(600);
        // Button settings
        continueButton.setLayoutX(470);
        continueButton.setLayoutY(65);
        // Label settings
        messageForVideo.setLayoutX(42);
        messageForVideo.setLayoutY(30);
        messageForVideo.setWrapText(true);
        messageForVideo.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Regular.ttf"),24));
        messageForVideo.setTextFill(Color.SNOW);
        // MediaPlayer settings
        tutorialPlayer.setAutoPlay(true);
        tutorialPlayer.setCycleCount(99);
        // MediaView settings
        mediaView.setSmooth(true);
        mediaView.setCache(true);
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(280);
        mediaView.setFitHeight(210);
        mediaView.setX(42);
        mediaView.setY(150);

        continueButton.setOnAction( e -> {
            playGame();
        });

        aPane.getChildren().addAll(iV, messageForVideo, mediaView, continueButton);
        window.setScene(loadingScene);
        window.show();
    }
    private void finishedGameMessage(){
        Score result = new Score(username, time);
        VBox message = new VBox();
        message.setSpacing(14);
        Label endgame = new Label("Congratulations, you are the NAND racer!");
        message.getChildren().add(endgame);
        if (scores.highScore(result)){
            Label congrats = new Label("Well done " + time + " is a top five score for this map!");
            message.getChildren().add(congrats);
        }
        else{
            Label gameover = new Label("You scored " + time);
            message.getChildren().add(gameover);
        }
        // adds two buttons to the vbox
        Button mainmenu = new Button("Main menu");
        mainmenu.setOnAction(e-> start(window));
        // button to restart game
        Button again = new Button("Play again");
        again.setOnAction(e-> playGame());
        // button to get leaderboard
        Button highScores = new Button("Leaderboard");
        highScores.setOnAction(e-> getLeaderboard(true));
        // organising
        message.getChildren().addAll(mainmenu, again, highScores);
        message.setAlignment(Pos.CENTER);
        BorderPane bp = new BorderPane();
        getBackground(bp, finishedGameBackground);
        bp.setPadding(new Insets(0, 10, 0, 10));
        bp.setTop(message);
        BorderPane.setAlignment(message, Pos.CENTER);
        Scene endMessage = new Scene(bp, 600, 400);
        endMessage.getStylesheets().add("style.css");
        scores.update(result);
        scores.save();
        window.setScene(endMessage);
        window.show();
    }


    // image view for the first scene
    private void getImage(Pane pane, Image image){
        ImageView iV = new ImageView();
        iV.setImage(image);
        // improves performance
        iV.setSmooth(true);
        iV.setCache(true);
        // maintains image integrity
        iV.setFitWidth(60);
        iV.setFitHeight(50);
        iV.setLayoutX(400);
        iV.setLayoutY(50);
        pane.getChildren().add(iV);
    }
    // creates a heading
    private Text createHeading(String heading){
        Text title = new Text(heading);
        title.setLayoutX(31);
        title.setLayoutY(84);
        title.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/ScifiAdventure.otf"), 36));
        title.setFill(Color.rgb(54,  196,  225));
        return title;
    }
    // picks a random map
    private void randomMap(){
        Random rand = new Random();
        currentMap = rand.nextInt(numberOfMaps);
    }

    private void playGame() {
        // reset map current lap and clock
        maps.get(currentMap).resetLaps();
        clk.reset();
        setRotation.setAngle(0);
        /*game scene*/
        Canvas canvas = new Canvas(maps.get(currentMap).getMapWidth(), maps.get(currentMap).getMapHeight());
        GraphicsContext g = canvas.getGraphicsContext2D();
        Group root = new Group(canvas);
        currentScene = new Scene(root);
        getMap();

        timerLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Regular.ttf"), 26));
        timerLabel.setTextFill(Color.rgb(54,  196,  225));
        timerLabel.textProperty().bind(raceTimer.textProperty());

        //Car boolean value lable
        carBoolLab.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/alarm_clock.ttf"), 32));
        carBoolLab.setTextFill(Color.rgb(255, 214, 51));/*255, 192, 45*/
        carBoolLab.textProperty().bind(carBool.textProperty());
        carBoolLab.setLayoutX(950);
        carBoolLab.setLayoutY(686);

        //SpaceBar boolean lable
        spaceLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/alarm_clock.ttf"), 32));
        spaceLabel.setTextFill(Color.rgb(255, 214, 51));
        spaceLabel.textProperty().bind(spaceBool.textProperty());
        spaceLabel.setLayoutX(950);
        spaceLabel.setLayoutY(654);

        //lapcounter lable
        lapLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Regular.ttf"), 26));
        lapLabel.setTextFill(Color.rgb(54,196,225));
        lapLabel.textProperty().bind(lapCount.textProperty());
        lapLabel.setLayoutY(30);

        // Speedometer: meter and needle
        meterView.setLayoutX(1100);
        meterView.setLayoutY(640);
        needleView.setLayoutX(1175);
        needleView.setLayoutY(718);
        setRotation.setPivotX(0);
        needleView.getTransforms().addAll(setRotation);

        root.getChildren().add(timerLabel);
        root.getChildren().add(meterView);
        root.getChildren().add(needleView);
        root.getChildren().add(carBoolLab);
        root.getChildren().add(spaceLabel);
        root.getChildren().add(lapLabel);

        drawMap(g, root);
        detectorInit(maps.get(currentMap).startLine());

        root.getChildren().add(CreateCar());
        drawVehicle();
        window.setScene(currentScene);

        // music and mute functionality
        music.play(0.5);
        window.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()==KeyCode.M) {
                checkMute();
            }
        });
        // check for menu
        window.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()==KeyCode.ESCAPE) {
                start(window);
            }
        });

        // gameplay controls
        window.getScene().setOnKeyPressed( event-> {
            if (event.getCode() == KeyCode.UP) car.accelerator("stepped on", "forward");
            else if (event.getCode() == KeyCode.DOWN) car.accelerator("stepped on", "backward");
            else if (event.getCode() == KeyCode.LEFT) car.wheel("left");
            else if (event.getCode() == KeyCode.RIGHT) car.wheel("right");
            else if (event.getCode() == KeyCode.SPACE) car.setSpace("true");//pass true as arg
        });
        window.getScene().setOnKeyReleased( event-> {
            if (event.getCode() == KeyCode.UP) car.accelerator("released", "forward");
            else if (event.getCode() == KeyCode.DOWN) car.accelerator("released", "backward");
            else if (event.getCode() == KeyCode.LEFT) car.wheel("released");
            else if (event.getCode() == KeyCode.RIGHT) car.wheel("released");
            else if (event.getCode() == KeyCode.SPACE) car.setSpace("false");//pass false as arg
        });
        window.setResizable(true);
        timer.start();
        window.show();
    }
    private void checkMute(){
        if (music.isPlaying()){
            music.stop();
        }
        else{
            music.play(0.5);
        }
    }
    //function to change button colour when right answer
    void correctAnswer(Button button){
        button.getStyleClass().removeAll();
        button.getStyleClass().add("correctAnswer");
    }

    //function to change button colour when wrong Answer
    void wrongAnswer(Button button){
        button.getStyleClass().removeAll();
        button.getStyleClass().add("incorrectAnswer");
    }
    //function to reset the colour of buttons
    void resetButton(Button button){
        button.getStyleClass().removeAll("incorrectAnswer");
        button.getStyleClass().add("button");
    }

    private void ingameMenu(){
        Stage popup = new Stage();
        popup.setResizable(false);
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(14, 0, 0, 14));
        getBackground(borderPane,popupMenuBackground);
        popup.setMinHeight(200);
        popup.setMinWidth(300);

        Button resume =new Button("Resume");
        resume.setOnAction( e -> {
            popup.close();
        });
        Button mainmenu =new Button("Main menu");
        mainmenu.setOnAction( e -> {
            popup.close();
            music.stop();
            start(window);
        });

        VBox layout =new VBox(14);
        layout.getChildren().addAll(resume, mainmenu);
        layout.setAlignment(Pos.CENTER);
        borderPane.setTop(layout);
        borderPane.setId("menu");
        Scene scene=new Scene(borderPane);
        scene.getStylesheets().add("style.css");
        popup.setScene(scene);
        popup.showAndWait();
    }
    // scene to get username information, prior to starting a 'normal game'
    private void normalGame(){
        // set map to first map and gamemode to normal
        currentMap = 0;
        gamemode = GameMode.NORMAL;
        // scene building
        AnchorPane anchorPane = new AnchorPane();
        getBackground(anchorPane, normalGameBackground);
        Text title = createHeading("Normal Game");
        title.setLayoutX(52);
        TextField text = new TextField("Enter username here");
        Button play = new Button("Play game");
        play.setOnAction(e->{
            username = text.getText();
            tutorialScreen(notIntroductionVideo,explainGateInfo);
        });
        Button mainMenu = new Button("Main menu");
        mainMenu.setOnAction(e->{
            start(window);
        });
        VBox box = new VBox();
        box.setSpacing(14);
        box.getChildren().addAll(text, play, mainMenu);
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(220);
        box.setLayoutY(197);
        anchorPane.getChildren().addAll(title, box);
        currentScene = new Scene(anchorPane, 600, 400);
        window.setScene(currentScene);
        window.show();
    }

    // message displayed after a game
    private void displayEndMessage(){
        Score result = new Score(username, time);
        VBox message = new VBox();
        message.setSpacing(14);
        if (scores.highScore(result)){
            Label congrats = new Label("Congratulations! " + time + " is a top five score for this map!");
            message.getChildren().add(congrats);
        }
        else{
            Label gameover = new Label("You scored " + time);
            message.getChildren().add(gameover);
        }
        // adds three buttons to the vbox
        addEndButtons(message);
        BorderPane bp = new BorderPane();
        getBackground(bp, displayEndMessageBackground);
        bp.setPadding(new Insets(0, 10, 0, 10));
        bp.setTop(message);
        BorderPane.setAlignment(message, Pos.CENTER);
        Scene endMessage = new Scene(bp, 600, 400);
        endMessage.getStylesheets().add("style.css");
        scores.update(result);
        scores.save();
        window.setScene(endMessage);
        window.show();
    }

    // adds buttons to the end game screen
    private void addEndButtons(VBox box){
        // button to play next map
        Button next = new Button("Next track");
        next.setOnAction(e-> {
            currentMap++;
            if (currentMap==1) {
                tutorialScreen(orIntroductionVideo, explaintSpacebarOrGate);
            }
            else playGame();
        });
        // button to return to main menu
        Button mainmenu = new Button("Main menu");
        mainmenu.setOnAction(e-> start(window));
        // button to restart game
        Button again = new Button("Play again");
        again.setOnAction(e-> playGame());
        // button to get leaderboard
        Button highScores = new Button("Leaderboard");
        highScores.setOnAction(e-> getLeaderboard(true));
        // organising
        box.getChildren().addAll(next, mainmenu, again, highScores);
        box.setAlignment(Pos.CENTER);
    }

    // window to ask which leaderboard the player wishes to see
    private void whichLeaderboard(){
        AnchorPane anchorPane = new AnchorPane();
        getBackground(anchorPane, leaderboardBackground);
        Text title = createHeading("Leaderboards");

        VBox vbox = new VBox();
        Button one = new Button(mapOneName);
        one.setOnAction(e->{ currentMap = 0; getLeaderboard(false);});
        Button two = new Button(mapTwoName);
        two.setOnAction(e->{ currentMap = 1; getLeaderboard(false);});
        Button three = new Button(mapThreeName);
        three.setOnAction(e->{ currentMap = 2; getLeaderboard(false);});
        Button four = new Button(mapFourName);
        four.setOnAction(e->{ currentMap = 3; getLeaderboard(false);});
        Button five = new Button(mapFiveName);
        five.setOnAction(e->{ currentMap = 4; getLeaderboard(false);});
        Button six = new Button(mapSixName);
        six.setOnAction(e->{ currentMap = 5; getLeaderboard(false);});

        vbox.getChildren().addAll(title, one, two, three, four, five, six);
        vbox.setSpacing(14);
        vbox.setAlignment(Pos.CENTER);
        vbox.setLayoutX(4);
        vbox.setLayoutY(50);

        anchorPane.getChildren().add(vbox);
        Scene which = new Scene(anchorPane, 600, 400);
        window.setScene(which);
        window.show();
    }

    // creates leaderboard
    private void getLeaderboard(boolean ingame){
        AnchorPane anchorPane = new AnchorPane();
        getBackground(anchorPane, leaderboardBackground);
        // css needed for labels
        anchorPane.getStylesheets().add("style.css");
        // update scores and current leaderboard
        updateCurrentLeaderboard();
        scores = maps.get(currentMap).getLeaderboard();
        // heading
        Text title = new Text(currentLeaderboard);
        title.setLayoutX(131);
        title.setLayoutY(84);
        title.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/ScifiAdventure.otf"), 36));
        title.setFill(Color.rgb(54,  196,  225));
        // create two VBoxes to hold the names and scores
        VBox nameList = new VBox();
        VBox scoreList = new VBox();
        for (int i = 0; i < 5; i++){
            if (scores.numScores() > i){
                Label usernameLabel = new Label(scores.getScores(i).getUsername());
                usernameLabel.setId("leaderboardLabel");
                Label scoreLabel = new Label(" " + scores.getScores(i).getTime());
                scoreLabel.setId("leaderboardLabel");
                nameList.getChildren().add(usernameLabel);
                scoreList.getChildren().add(scoreLabel);
            }
        }
        // HBox to hold score/username VBoxes
        HBox hbox = new HBox();
        hbox.getChildren().addAll(nameList, scoreList);
        hbox.setAlignment(Pos.CENTER);
        // button to return to main menu
        Button mainMenu = new Button("Main menu");
        mainMenu.setOnAction(e->{
            start(window);
        });
        // VBox to organise scores and button
        VBox vbox = new VBox();
        vbox.getChildren().addAll(title, hbox, mainMenu);
        vbox.setSpacing(14);
        vbox.setAlignment(Pos.CENTER);
        // additional button if we are in the middle of a normal game
        if (ingame){
            Button nextGame = new Button("Next game");
            nextGame.setOnAction(e->{
                currentMap++;
                playGame();
            });
            vbox.getChildren().add(nextGame);
        }
        organiseLeaderboardVBox(vbox);
        // organising
        anchorPane.getChildren().addAll(vbox);
        Scene highscores = new Scene(anchorPane, 600, 400);
        window.setScene(highscores);
        window.show();
    }


    private void organiseLeaderboardVBox(VBox vbox){
        vbox.setLayoutY(50);
        switch(currentMap){
            case 0: vbox.setLayoutX(246); break;
            case 1: vbox.setLayoutX(264); break;
            case 2: vbox.setLayoutX(246); break;
            case 3: vbox.setLayoutX(228); break;
            case 4: vbox.setLayoutX(94); break;
            case 5: vbox.setLayoutX(156); break;
        }
    }

    private void updateCurrentLeaderboard(){
        switch(currentMap){
            case 0: currentLeaderboard = mapOneName; break;
            case 1: currentLeaderboard = mapTwoName; break;
            case 2: currentLeaderboard = mapThreeName; break;
            case 3: currentLeaderboard = mapFourName; break;
            case 4: currentLeaderboard = mapFiveName; break;
            case 5: currentLeaderboard = mapSixName; break;
        }
    }
    // gets map from linked list
    private void getMap(){
        Files f = new Files(maps.get(currentMap).getFile().getFilename());
        f.loadMap(maps.get(currentMap));
        scores = maps.get(currentMap).getLeaderboard();
    }

    /*Graphics*/

    // gets background
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
        g.drawImage(getBackImage(maps.get(currentMap).getBackground()), 0, 0);
    }

    // changes currentImage
    private Image getTileImage(int n){
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
        for (int i = 0; i < maps.get(currentMap).getGridHeight(); i++){
            for (int j = 0; j < maps.get(currentMap).getGridWidth(); j++){

                // if tile value isn't 0, draw relevant image
                if (maps.get(currentMap).getTile(i, j) != 0){
                    g.drawImage(
                        getTileImage(maps.get(currentMap).getTile(i, j)),
                        j * maps.get(currentMap).getTileSize(),
                        i * maps.get(currentMap).getTileSize()
                    );
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
            /*case OBSTACLE:
                result = obstacle; break;*/
            default:
                throw new Error("Object type needed for assigning image");
        }
        return result;
    }

    // draws object layer
    private void drawObjects(Group r){
        LinkedList<Gameobject> objects = maps.get(currentMap).getObjects();

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
        Gameobject startLine = maps.get(currentMap).startLine();
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

    // gets map and object data from specific file
    private void assignTrack(String filename){
        Files f = new Files(filename);
        f.loadMap(maps.get(currentMap));
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
        Map map = maps.get(currentMap);

        if (map.getCurrentLap() >= 1 && map.getCurrentLap() < map.getMaxLaps() + 1) clk.tick(dt);
        if (map.getCurrentLap() == map.getMaxLaps()){
            endGame();
        }
        speedometer.setSpeed(car);

        car.collision(map);
        car.motion(dt);

        if (ld.detect(car)) {
            map.addLap();
            ld.reset();
            for (Gameobject ob : map.getObjects()) ob.activate();
        }
    }

    private void endGame(){
        music.stop();
        timer.stop();
        if (gamemode == GameMode.NORMAL){
            try{
                time = Double.parseDouble(clk.getTime());
            }
            catch(NumberFormatException e){
                e.printStackTrace();
            }
            questionWindow();
        }
        else{
            start(window);
        }
    }

    /*rendering*/
    private void render() {
        render_car();
        render_clk();
        render_speedo();
        render_boolRes();
        render_spaceBool();
        render_lapCount();
    }

    private void render_car() {
        c.setRotate(car.direction());
        c.setX(car.position().x() - car.length() / 2);
        c.setY(car.position().y() - car.length() / 2);
    }

    // uni-direction binding to timerLabel
    private void render_clk() { raceTimer.setText(clk.getTime()); }
    private void render_speedo() { setRotation.setAngle(speedometer.getAngle()+180); }
    private void render_boolRes() { int boolVal = car.getBool() ? 1:0;  carBool.setText("BOOL: " + Integer.toString(boolVal));}
    private void render_spaceBool(){ int boolVal = car.getSpace() ? 1:0; spaceBool.setText("SPACE: " + Integer.toString(boolVal));}
    private void render_lapCount() {int currentLap = (maps.get(currentMap)).getCurrentLap(); lapCount.setText("Lap: " + Integer.toString(currentLap) +"/3");}
}
