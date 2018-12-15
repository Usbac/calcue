package ve.com.usbac.calcue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Calcue extends Application {
    
    final static int WIDTH = 450;
    final String MAIN_STYLE = "/styles/Styles.css";
    final String DARK_THEME = "/styles/DarkTheme.css";
    final String SCENE = "/fxml/Scene.fxml";
    final String TITLE = "Calcue";
    
    private double x, y;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(SCENE));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MAIN_STYLE);
        scene.getStylesheets().add(DARK_THEME);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setWidth(WIDTH);
        stage.show();
        initializeKeys(scene);
        initializeMouseMovement(root, stage);
    }
    

    public void initializeMouseMovement(Parent root, Stage stage) {
        //Grab
        root.setOnMousePressed((MouseEvent event) -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        
        //Move
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });
    }
    
    
    public void initializeKeys(Scene scene) {
        scene.setOnKeyPressed(e -> {
            Controller.ProcessKeys(e.getCode());
        });
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    
}