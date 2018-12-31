package ve.com.usbac.calcue;

import java.util.ArrayList;

public final class Controller {

    private final String NEW_LINE = System.getProperty("line.separator");
    private final String ERROR_MSG = "ERROR";
    private final String EMPTY = "";
    private final View view;
    private final Prefix prefix;
    
    public FileManager file;
    private final ArrayList<String> operations;
    String variablesBackup;
    int operationIndex;
    
    
    public Controller(View v) {
        view = v;
        prefix = new Prefix();
        file = new FileManager(view, this);
        operations = new ArrayList<>();
        operationIndex = 0;
    }
    
    
    public void updateLastOperations() {
        operationIndex = operations.size() - 1;
        if (operationIndex >= 1)
            view.previousFunction.setText(operations.get(operationIndex - 1));
        if (operationIndex > 0)
            view.function.setText(operations.get(operationIndex));
    }
    
    
    public void goPreviousOperation() {
        if (operationIndex <= 0)
            return;
        operationIndex--;
        view.previousFunction.setText(operationIndex != 0? operations.get(operationIndex - 1): EMPTY);
        view.function.setText(operations.get(operationIndex));
    }
    

    public void goNextOperation() {
        if (operationIndex >= operations.size() - 1)
            return;
        operationIndex++;
        view.previousFunction.setText(operations.get(operationIndex - 1));
        view.function.setText(operations.get(operationIndex));
    }
    
    
    public void setDecimals(String number) {
        prefix.decimalPlaces = "%." + number + "f";
    }
    
    
    public void solveFunction() {
        if (view.getFunction().isEmpty())
            return;
            
        operations.add(view.getFunction());
        operationIndex = operations.size() - 1;
        view.previousFunction.setText(operations.get(operationIndex));
        
        String result;
        
        try {
            result = prefix.getResult(view.getFunction(), view.getVariables());
        } catch (Exception e) {
            view.function.setText(ERROR_MSG);
            return;
        }
        
        //Don't add the result to the history if that result is the same as the previous
        if (!operations.get(operationIndex).matches(result)) {
            addToOperations(result);
            view.function.setText(result);
        }
    }
    
    
    public String getOperations() {
        String finalList = EMPTY;
        return operations.stream()
                         .map((list) -> list + NEW_LINE)
                         .reduce(finalList, String::concat);
    }
    
    
    public void addToOperations(String value) {
        operations.add(value);
        operationIndex = operations.size() - 1;
    }
   
    
    public void clearAll() {
        operations.clear();
        view.function.setText(EMPTY);
        view.previousFunction.setText(EMPTY);
        view.variables.setText(EMPTY);
    }
}