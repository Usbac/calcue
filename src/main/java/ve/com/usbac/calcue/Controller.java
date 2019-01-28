package ve.com.usbac.calcue;

import java.io.*;
import java.net.*;
import java.net.URISyntaxException;
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

public class Controller implements Initializable {
    
    final int FULLSIZE_WIDTH = 450;
    final int SMALLSIZE_WIDTH = 237;
    final int LOGO_SIZE = 65;
    final String ICON_PATH = "/images/icon.png";
    final String LINK = "https://github.com/Usbac/Calcue";
    final String ABOUT_TITLE = "Calcue Information";
    final String ABOUT_HEADER = "Calcue v1.0.1";
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
    
    static Stage stage;
    Model model;
    Theme theme;
    Sidebar sidebar;
    
    enum Theme {
        DARK, LIGHT
    }
    
    enum Sidebar {
        VARIABLES, HISTORY
    }
    
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
    
    
    public String getFunction() {
        return function.getText();
    }
    
    
    public String getVariables() {
        return variables.getText();
    }

        
    @FXML
    private void onClickOpen(ActionEvent event) throws FileNotFoundException {
        model.file.openFile();
    }
        
    
    @FXML
    private void onClickSave(ActionEvent event) throws IOException {
        model.file.saveFile();
    }
    
    
    @FXML
    private void onClickSaveAs(ActionEvent event) throws IOException {
        model.file.saveFileAs();
    }
    
    
    @FXML
    private void onClickShowInfo(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION, ABOUT_TEXT, ButtonType.OK);
        alert.setTitle(ABOUT_TITLE);
        alert.setHeaderText(ABOUT_HEADER);
        alert.getDialogPane().setGraphic(getImageIcon());
        alert.showAndWait();
    }
    
    
    @FXML
    private void setDecimals(ActionEvent event) {
        model.setDecimals(((RadioMenuItem)event.getSource()).getText());
    }
    
    
    private ImageView getImageIcon() {
        ImageView img = new ImageView(ICON_PATH);
        img.setFitHeight(LOGO_SIZE);
        img.setFitWidth(LOGO_SIZE);
        img.setOnMouseClicked((MouseEvent e) -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(LINK));
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return img;
    }
    
    
    @FXML
    private void add(ActionEvent event) {
        String newValue = ((Node) event.getSource()).getUserData().toString();
        function.setText(function.getText() + newValue);
    }
    
    
    @FXML
    public void onClickDown(ActionEvent event) {
        model.goNextOperation();
    }
    
    
    @FXML
    public void onClickUp(ActionEvent event) {
        model.goPreviousOperation();
    }
    
    
    @FXML
    private void onClickEquals(ActionEvent event) {
        model.solveFunction();
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
        theme = (theme == Theme.DARK)? Theme.LIGHT: Theme.DARK;
        optionTheme.setText(theme == Theme.DARK? LIGHT_THEME : DARK_THEME);
        stage = (Stage) ap.getScene().getWindow();
        stage.getScene().getStylesheets().add(STYLES_FOLDER + (theme == Theme.DARK? "Dark":"Light") + STYLE_FILE_SUFFIX);
        stage.getScene().getStylesheets().remove(STYLES_FOLDER + (theme == Theme.DARK? "Light":"Dark") + STYLE_FILE_SUFFIX);
    }
    
    
    @FXML
    private void ToggleSide(ActionEvent event) {
        variables.setVisible(!variables.isVisible());
        toggleHistory.setVisible(!toggleHistory.isVisible());
        optionSide.setText(variables.isVisible()? COMPACT:EXPAND);
        ((Stage)ap.getScene().getWindow()).setWidth(variables.isVisible()? FULLSIZE_WIDTH:SMALLSIZE_WIDTH);
    }
    
    
    @FXML
    private void onClickHistory(ActionEvent event) {
        sidebar = (sidebar == Sidebar.VARIABLES)? Sidebar.HISTORY: Sidebar.VARIABLES;
        if (sidebar == Sidebar.VARIABLES) {
            variables.setText(model.variablesBackup);
            toggleHistory.setText(SHOW_HISTORY);
        } else {
            model.variablesBackup = variables.getText();
            variables.setText(model.getOperations());
            toggleHistory.setText(SHOW_VARIABLES);
        }
    }
    
    
    @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
    }
    
    
    @FXML
    public void minimize(ActionEvent event) {
        ((Stage)ap.getScene().getWindow()).setIconified(true);
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new Model(this);
        optionTheme.setText(LIGHT_THEME);
        theme = Theme.DARK;
        sidebar = Sidebar.VARIABLES;
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
            if (event.getCode() == KeyCode.ESCAPE)
                minimize(null);
            if (event.getCode() == KeyCode.ENTER)
                onClickEquals(null);
            if (event.getCode() == KeyCode.UP)
                onClickUp(null);
            if (event.getCode() == KeyCode.DOWN)
                onClickDown(null);
        });
    }
}