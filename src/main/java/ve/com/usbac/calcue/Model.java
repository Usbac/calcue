package ve.com.usbac.calcue;

import java.util.ArrayList;

public final class Model {

    private final String NEW_LINE = System.getProperty("line.separator");
    private final String ERROR_MSG = "ERROR";
    private final String EMPTY = "";
    private final Controller controller;
    private final Prefix prefix;
    
    public FileManager file;
    private final ArrayList<String> operations;
    String variablesBackup;
    int operationIndex;
    
    
    public Model(Controller c) {
        controller = c;
        prefix = new Prefix();
        file = new FileManager(controller, this);
        operations = new ArrayList<>();
        operationIndex = 0;
    }
    
    
    public void updateLastOperations() {
        operationIndex = operations.size() - 1;
        if (operationIndex >= 1)
            controller.previousFunction.setText(operations.get(operationIndex - 1));
        if (operationIndex > 0)
            controller.function.setText(operations.get(operationIndex));
    }
    
    
    public void goPreviousOperation() {
        if (operationIndex <= 0)
            return;
        operationIndex--;
        controller.previousFunction.setText(operationIndex != 0? operations.get(operationIndex - 1): EMPTY);
        controller.function.setText(operations.get(operationIndex));
    }
    

    public void goNextOperation() {
        if (operationIndex >= operations.size() - 1)
            return;
        operationIndex++;
        controller.previousFunction.setText(operations.get(operationIndex - 1));
        controller.function.setText(operations.get(operationIndex));
    }
    
    
    public void setDecimals(String number) {
        prefix.decimalPlaces = "%." + number + "f";
    }
    
    
    public void solveFunction() {
        if (controller.getFunction().isEmpty())
            return;
            
        operations.add(controller.getFunction());
        operationIndex = operations.size() - 1;
        controller.previousFunction.setText(operations.get(operationIndex));
        
        String result;
        
        try {
            result = prefix.getResult(controller.getFunction(), controller.getVariables());
        } catch (Exception e) {
            controller.function.setText(ERROR_MSG);
            return;
        }
        
        //Don't add the result to the history if that result is the same as the previous
        if (!operations.get(operationIndex).matches(result)) {
            addToOperations(result);
            controller.function.setText(result);
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
        controller.function.setText(EMPTY);
        controller.previousFunction.setText(EMPTY);
        controller.variables.setText(EMPTY);
    }
}