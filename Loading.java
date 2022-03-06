import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Loading extends Application {

    private Image lightCycle = new Image("Game_Assets/lightCycle.jpg", 800, 640, true, true);
    private Image notGate = new Image("Game_Assets/NOT_Gate.png");
    private Image orGate = new Image("Game_Assets/OR_Gate.png");
    private Image andGate = new Image("Game_Assets/AND_Gate_Right.png");
    private Image nandGate = new Image("Game_Assets/NAND_Gate.png");
    private Media notIntroductionVideo = new Media(getClass().getResource("Game_Assets/NOT_tutorial.mp4").toExternalForm());
    private Media orIntroductionVideo = new Media(getClass().getResource("Game_Assets/OR_tutorial.mp4").toExternalForm());

    public void start(Stage stage){

        AnchorPane aPane = new AnchorPane();
        Button continueButton = new Button();
        Label explainGateInfo = new Label("This is a NOT gate. When you pass through it, \n the Boolean value will change from 0 to 1 \n or vice versa.");
        ImageView iV = new ImageView();
        ImageView gateView = new ImageView();
        Button nextScreen = new Button(" -> ");
        MediaPlayer notTutorialPlayer = new MediaPlayer(notIntroductionVideo);
        MediaPlayer orTutorialPlayer = new MediaPlayer(orIntroductionVideo);
        MediaView mediaView = new MediaView(notTutorialPlayer);

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
        nextScreen.setLayoutX(470);
        nextScreen.setLayoutY(65);
        // Label settings
        explainGateInfo.setLayoutX(42);
        explainGateInfo.setLayoutY(30);
        explainGateInfo.setWrapText(true);
        explainGateInfo.setFont(Font.loadFont(getClass().getResourceAsStream("/Game_Assets/Marvel-Regular.ttf"),24));
        explainGateInfo.setTextFill(Color.SNOW);
        // MediaPlayer settings
        notTutorialPlayer.setAutoPlay(true);
        notTutorialPlayer.setCycleCount(99);
        // MediaView settings
        mediaView.setSmooth(true);
        mediaView.setCache(true);
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(280);
        mediaView.setFitHeight(210);
        mediaView.setX(42);
        mediaView.setY(150);

        aPane.getChildren().addAll(iV, explainGateInfo, mediaView, nextScreen);
        stage.setScene(loadingScene);
        stage.show();
    }
}
