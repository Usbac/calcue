package ve.com.usbac.calcue;

import java.util.ArrayList;
import javafx.scene.input.*;

public final class Controller {

    private final String NEW_LINE = System.getProperty("line.separator");
    private final String ERROR_MSG = "ERROR";
    private final String EMPTY = "";
    private final View view;
    private final Prefix prefix;
    
    public FileManager file;
    private final ArrayList<String> operationList;
    String variablesBackup;
    int operationIndex;
    
    
    public Controller(View v) {
        view = v;
        prefix = new Prefix();
        file = new FileManager(view, this);
        operationList = new ArrayList<>();
        operationIndex = 0;
    }
    
    
    public void setLastOperations() {
        operationIndex = operationList.size() - 1;
        view.previousFunction.setText(operationList.get(operationIndex - 1));
        view.function.setText(operationList.get(operationIndex));
    }
    
    
    public void goPreviousOperation() {
        if (operationIndex <= 0)
            return;
        operationIndex--;
        view.previousFunction.setText(operationIndex != 0? operationList.get(operationIndex - 1): EMPTY);
        view.function.setText(operationList.get(operationIndex));
    }
    

    public void goNextOperation() {
        if (operationIndex >= operationList.size() - 1)
            return;
        operationIndex++;
        view.previousFunction.setText(operationList.get(operationIndex - 1));
        view.function.setText(operationList.get(operationIndex));
    }
    
    
    public void solveFunction() {
        operationList.add(view.function.getText());
        view.previousFunction.setText(view.function.getText());
        operationIndex = operationList.size() - 1;
        String result;
        
        try {
            result = prefix.convertToAndSolvePrefix(view.function.getText(), view.variables.getText());
        } catch (Exception e) {
            view.function.setText(ERROR_MSG);
            return;
        }
        
        if (!operationList.get(operationIndex).matches(result)) {
            addToOperations(result);
            view.function.setText(result);
        }
    }
    
    
    public String getOperationList() {
        String finalList = EMPTY;
        return operationList.stream()
                            .map((list) -> list + NEW_LINE)
                            .reduce(finalList, String::concat);
    }
    
    
    public void addToOperations(String value) {
        operationList.add(value);
    }
   
    public void clearAll() {
        operationList.clear();
        view.variables.setText(EMPTY);
    }
}