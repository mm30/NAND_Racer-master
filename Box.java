import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.layout.*;

public class Box {
    public void show( String title,String message )
    {
        Stage window = new Stage();
        window.setTitle(title);
        window.setMinHeight(150);
        window.setMinWidth(300);

        window.initModality(Modality.APPLICATION_MODAL);
        Label label =new Label(message);

        Button button =new Button("Ok");
        button.setOnAction( e -> {
            window.close();
        });

        VBox layout =new VBox(10);
        layout.getChildren().addAll(label,button);
        layout.setAlignment(Pos.CENTER);

        Scene scene=new Scene(layout);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);
        window.showAndWait();
    }

}
