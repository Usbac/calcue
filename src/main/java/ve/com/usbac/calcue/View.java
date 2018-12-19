package ve.com.usbac.calcue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class View implements Initializable {
    
    final KeyCombination CTRL_O = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    final KeyCombination CTRL_S = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    final int FULLSIZE_WIDTH = 450;
    final int SMALLSIZE_WIDTH = 237;
    final int LOGO_SIZE = 65;
    final String ICON_PATH = "/images/icon.png";
    final String LINK = "https://github.com/Usbac/Calcue";
    final String ABOUT_TITLE = "Calcue Information";
    final String ABOUT_HEADER = "Calcue v0.9.1";
    final String ABOUT_TEXT = "Created by USBAC \n"
                            + "Click the Calcue logo for more information \n"
                            + "about the reserved functions and how to use.";
    final String STYLES_FOLDER = "/styles/";
    final String STYLE_FILE_SUFFIX = "Theme.css";
    final String SHOW_VARIABLES = "SHOW VARIABLES";
    final String SHOW_HISTORY = "SHOW HISTORY";
    final String COMPACT = "Compact";
    final String EXPAND = "Expand";
    final String DARK_THEME = "Dark Theme";
    final String LIGHT_THEME = "Light Theme";
    
    Controller controller;
    static Stage stage;
    boolean showingVariables, showingAbout;
    boolean darkTheme;
    
    @FXML 
    protected AnchorPane ap;
    @FXML
    protected TextField function;
    @FXML
    protected TextArea variables;
    @FXML
    protected Text previousFunction;
    @FXML
    protected MenuItem optionSide, optionTheme;
    @FXML
    protected Button toggleHistory, variablesButton;

        
    @FXML
    private void onClickOpen(ActionEvent event) throws FileNotFoundException {
        controller.file.openFile();
    }
        
    
    @FXML
    private void onClickSave(ActionEvent event) throws IOException {
        controller.file.saveFile();
    }
    
    
    @FXML
    private void onClickSaveAs(ActionEvent event) throws IOException {
        controller.file.saveFileAs();
    }
    
    
    @FXML
    private void onClickShowInfo(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION, ABOUT_TEXT, ButtonType.OK);
        alert.setTitle(ABOUT_TITLE);
        alert.setHeaderText(ABOUT_HEADER);
        alert.getDialogPane().setGraphic(getImg());
        alert.showAndWait();
    }
    
    
    private ImageView getImg() {
        ImageView img = new ImageView(ICON_PATH);
        img.setFitHeight(LOGO_SIZE);
        img.setFitWidth(LOGO_SIZE);
        img.setOnMouseClicked((MouseEvent e) -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(LINK));
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return img;
    }
    
    
    @FXML
    private void add(ActionEvent event) {
        Node node = (Node) event.getSource();
        String newValue = (String) node.getUserData();
        function.setText(function.getText() + newValue);
    }
    
    
    @FXML
    public void onClickDown(ActionEvent event) {
        controller.goNextOperation();
    }
    
    
    @FXML
    public void onClickUp(ActionEvent event) {
        controller.goPreviousOperation();
    }
    
    
    @FXML
    private void onClickEquals(ActionEvent event) {
        controller.solveFunction();
    }
    
    
    @FXML
    private void clearFunction(ActionEvent event) {
        function.clear();
    }
    
    
    @FXML
    private void clearVariables(ActionEvent event) {
        variables.clear();
    }
    
    
    @FXML
    private void onClickTheme(ActionEvent event) {
        darkTheme = !darkTheme;
        stage = (Stage) ap.getScene().getWindow();
        stage.getScene().getStylesheets().add(STYLES_FOLDER + (darkTheme? "Dark":"Light") + STYLE_FILE_SUFFIX);
        stage.getScene().getStylesheets().remove(STYLES_FOLDER + (darkTheme? "Light":"Dark") + STYLE_FILE_SUFFIX);
        optionTheme.setText(darkTheme? LIGHT_THEME : DARK_THEME);
        
    }
    
    
    @FXML
    private void ToggleSide(ActionEvent event) {
        stage = (Stage) ap.getScene().getWindow();
        variables.setVisible(!variables.isVisible());
        toggleHistory.setVisible(!toggleHistory.isVisible());
        optionSide.setText(variables.isVisible()? COMPACT:EXPAND);
        stage.setWidth(variables.isVisible()? FULLSIZE_WIDTH:SMALLSIZE_WIDTH);
    }
    
    
    @FXML
    private void onClickHistory(ActionEvent event) {
        showingVariables = !showingVariables;
        if (showingVariables) {
            variables.setText(controller.variablesBackup);
            toggleHistory.setText(SHOW_HISTORY);
        } else {
            controller.variablesBackup = variables.getText();
            variables.setText(controller.getOperationList());
            toggleHistory.setText(SHOW_VARIABLES);
        }
    }
    
    
    @FXML
    public void minimize(ActionEvent event) {
        stage = (Stage) ap.getScene().getWindow();
        stage.setIconified(true);
    }
    
    
    @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = new Controller(this);
        optionTheme.setText(LIGHT_THEME);
        darkTheme = true;
        showingVariables = true;
        initializeInput();
    }
    
    
    public void initializeInput() {
        function.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.UP)
                onClickUp(null);
            if (event.getCode() == KeyCode.DOWN)
                onClickDown(null);
        });
        
        ap.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER)
                onClickEquals(null);
            if (event.getCode() == KeyCode.UP)
                onClickUp(null);
            if (event.getCode() == KeyCode.DOWN)
                onClickDown(null);
            //Save
            if (CTRL_S.match(event)) {
                try {
                    controller.file.saveFile();
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Open
            if (CTRL_O.match(event)) {
                try {
                    controller.file.openFile();
                } catch (IOException ex) {
                    Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        });
    }
}