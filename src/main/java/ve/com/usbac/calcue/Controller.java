package ve.com.usbac.calcue;

import java.util.ArrayList;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public final class Controller {
    
    private final static String NEW_LINE = System.getProperty("line.separator");
    private final static String ERROR_MSG = "ERROR";
    
    static View view;
    static Prefix prefix;
    static ArrayList<String> operationList;
    static String variablesBackup;
    static int operationIndex;
    
    
    public Controller(View v) {
        view = v;
        prefix = new Prefix();
        operationList = new ArrayList<>();
        operationIndex = 0;
    }
    
    
    public static void setLastOperations() {
        operationIndex = operationList.size() - 1;
        view.previousFunction.setText(operationList.get(operationIndex - 1));
        view.function.setText(operationList.get(operationIndex));
    }
    
    
    public static void goPreviousOperation() {
        if (operationIndex <= 0)
            return;
        operationIndex--;
        view.previousFunction.setText(operationIndex != 0? operationList.get(operationIndex - 1): "");
        view.function.setText(operationList.get(operationIndex));
    }
    

    public static void goNextOperation() {
        if (operationIndex >= operationList.size() - 1)
            return;
        operationIndex++;
        view.previousFunction.setText(operationList.get(operationIndex - 1));
        view.function.setText(operationList.get(operationIndex));
    }
    
    
    public static void solveFunction() {
        operationList.add(view.function.getText());
        view.previousFunction.setText(view.function.getText());
        String result;
        try {
            result = Prefix.convertToAndSolvePrefix(view.function.getText(), view.variables.getText());
            operationList.add(result);
        } catch (Exception e) {
            operationList.add(ERROR_MSG);
        }
        
        operationIndex = operationList.size() - 1;
        view.function.setText(operationList.get(operationIndex));
    }
    
        
    public static void minimize() {
        View.stage = (Stage) view.ap.getScene().getWindow();
        View.stage.setIconified(true);
    }
    
    
    public static String getOperationList() {
        String finalList = "";
        return operationList.stream()
                            .map((list) -> list + NEW_LINE)
                            .reduce(finalList, String::concat);
    }
   
    
    public static void ProcessKeys(KeyCode key) {
        if (key == KeyCode.ENTER)
            solveFunction();
        if (key == KeyCode.UP)
            goPreviousOperation();
        if (key == KeyCode.DOWN)
            goNextOperation();
        if (key == KeyCode.ESCAPE)
            minimize();
    }
}