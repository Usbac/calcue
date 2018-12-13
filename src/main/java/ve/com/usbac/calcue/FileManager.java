package ve.com.usbac.calcue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;

public final class FileManager {
    
    private final static String NEW_LINE = System.getProperty("line.separator");
    private final static String ERROR_FILE = "ERROR: Bad file structure";
    private final static String ERROR_IO = "ERROR: IO Exception";
    private final static String VARIABLES_TAG = "<Variables>";
    private final static String OPERATIONS_TAG = "<Operations>";
    private final static String FILE_FORMAT = "*.calc";
    public final static FileChooser FILE_CHOOSER = new FileChooser();
    
    static String filePath = "";
    static View view = Controller.view;

    public static void setFilterFile() {
        FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(" + FILE_FORMAT + ")", FILE_FORMAT));
    }
    
    public static void openFile() throws FileNotFoundException {
        setFilterFile();
        File file = FILE_CHOOSER.showOpenDialog(View.stage);
        if (file != null) {
            filePath = file.toString();
            readFromFile(file);
        }
    }
    
    
    public static void saveFile() throws IOException {
        setFilterFile();
        File saveFile = new File(filePath);
        if (saveFile.exists()) {
            saveFile.delete();
            writeToFile(saveFile);
        } else {
            saveFileAs();
        }
    }
    
    
    public static void saveFileAs() throws IOException {
        writeToFile(FILE_CHOOSER.showSaveDialog(View.stage));
    }
    
    
    public static void writeToFile(File file) throws IOException {
        FileWriter out;
        try {
            filePath = file.toString();
        } catch (Exception e) {
            return;
        }
        out = new FileWriter(file.toString());
        out.write(VARIABLES_TAG);
        out.write(NEW_LINE);
        out.write(view.variables.getText().replace("\n", NEW_LINE));
        out.write(NEW_LINE);
        out.write(OPERATIONS_TAG);
        out.write(NEW_LINE);
        out.write(Controller.getOperationList());
        out.close();
    }
    
    
    public static void readFromFile(File file) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        String auxText;
        try {
            if (VARIABLES_TAG.equals(auxText = br.readLine())) {
                while((auxText = br.readLine()) != null && !OPERATIONS_TAG.equals(auxText))
                    view.variables.setText(view.variables.getText() + auxText + NEW_LINE);
                
                if (OPERATIONS_TAG.equals(auxText)) {
                    while((auxText = br.readLine()) != null)
                        Controller.operationList.add(auxText);
                    Controller.setLastOperations();
                    return;
                }
            }
            view.variables.setText(ERROR_FILE);
        } catch (IOException e) {
            view.variables.setText(ERROR_IO);
        }
    }
}