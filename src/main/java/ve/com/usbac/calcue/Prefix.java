package ve.com.usbac.calcue;

import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author Usbac
 */
public final class Prefix {
    
    private final String EMPTY = "";
    private final char NEW_LINE = '\n';
    
    private int index = 0;
    
    public String decimalPlaces = "%.4f";
    
    
    /**
     * Moves a number to the stack
     * @param main the stack
     * @param string the string where the number is
     */
    public void processNumberIntoStack(Stack main, String string) {
        String number = EMPTY;
        
        if (isNumberSign(string, index)) {
            number += string.charAt(index++);
        }
        
        while (index < string.length() && (Character.isDigit(string.charAt(index)) || string.charAt(index) == '.')) {
            number += string.charAt(index++);
        }
        main.push(number);
    }
    

    /**
     * Returns <code>true</code> if the char in the specified index is the sign of a number
     * @param string the operation string
     * @param i the index
     * @return <code>true</code> if the char in the specified index is the sign of a number
     */
    public boolean isNumberSign(String string, int i) {
        return (string.charAt(i) == '+' || string.charAt(i) == '-') && i > 0 && 
               (isOperand(string.charAt(i-1)) || string.charAt(i-1) == '(') && isNumber(string.charAt(i+1));
    }
    
    
    /**
     * Moves a word to the stack
     * @param main the main stack
     * @param operators the operators stack
     * @param string the string where the word is
     */
    public void processWordIntoStack(Stack main, Stack operators, String string) {
        String word = EMPTY;
        while(index < string.length() && Character.isLetter(string.charAt(index))) {
            word += string.charAt(index++);
        }
        while (operators.size() > 0 && (hasMoreHierarchy(operators.lastElement(), word) || 
                                        hasSameHierarchy(operators.lastElement(), word))) {
            main.push(operators.pop());
        }
        if (isFunction(word))
            operators.push(word);
        else
            main.push(word);
    }
    
    
    /**
     * Moves everything from the operators stack to the main stack until a closed parenthesis is found
     * @param main the main stack
     * @param operators the operators stack
     * @param string the string where the parenthesis is
     */
    public void processParenthesisIntoStack(Stack main, Stack operators, String string) {
        while (!operators.isEmpty() && !operators.lastElement().toString().equals("(")) {
            main.push(operators.pop());
        }
        
        if (operators.lastElement().toString().equals("(")) {
            operators.pop();
        }
        
        index++;
    }        

    
    /**
     * Moves everything from the operators stack to the main stack
     * @param origin the stack which values will be move from
     * @param destiny the stack which values will be move to
     */
    public void moveStackFromTo(Stack origin, Stack destiny) {
        while (!origin.isEmpty()) {
            destiny.push(origin.pop());
        }
    }
    
    
    /**
     * Returns <code>true</code> if the first operator has more hierarchy than the second, <code>false</code> otherwhise
     * @param first the first operator
     * @param second the second operator
     * @return <code>true</code> if the first operator has more hierarchy than the second, <code>false</code> otherwhise
     */
    public boolean hasMoreHierarchy(Object first, Object second) {
        if (first.toString().equals("(") || second.toString().equals("("))
            return false;
         
        if (isFunction(first) && !isFunction(second))
            return true;

        if (Arrays.asList("^", "sqrt", "%").contains(first.toString())) {
            return !(Arrays.asList("^", "sqrt", "%").contains(second.toString()));
        }
        
        if (Arrays.asList("/", "*").contains(first.toString())) {
            return (Arrays.asList("+", "-").contains(second.toString()));
        }
        
        if (!Arrays.asList("+", "-").contains(first.toString())) {
            return (Arrays.asList("+", "-").contains(second.toString()));
        }
        
        return false;
    }

    
    /**
     * Returns <code>true</code> if the first operator has the same hierarchy than the second, <code>false</code> otherwhise
     * @param first the first operator
     * @param second the second operator
     * @return <code>true</code> if the first operator has the same hierarchy than the second, <code>false</code> otherwhise
     */
    public boolean hasSameHierarchy(Object first, Object second) {
        if (first.toString().equals("(") && second.toString().equals("("))
            return false;
         
        if (isFunction(first) && isFunction(second))
            return true;
        
        if (Arrays.asList("^", "sqrt", "%").contains(first.toString())) {
            return (Arrays.asList("^", "sqrt", "%").contains(second.toString()));
        }
      
        if (Arrays.asList("/", "*").contains(first.toString())) {
            return (Arrays.asList("/", "*").contains(second.toString()));
        }
                
        if (Arrays.asList("+", "-").contains(first.toString())) {
            return (Arrays.asList("+", "-").contains(second.toString()));
        }
        
        return false;
    }
    
    
    /**
     * Removes all the concurrences with the string in the stack
     * @param stack the stack
     * @param element the element which will be compared to
     */
    public void removeElements(Stack stack, String element) {
        for (int i = 0; i < stack.size(); i++) {
            if (stack.elementAt(i).toString().equals(element))
                stack.remove(i);
        }
    }
    
    
    /**
     * Adds the missing multiplication symbols in the function
     * @param string the function
     * @return the function with the extra multiply symbols added
     */
    public String addMultiplication(String string) {
        String newString = string;
        for (int i = 0; i < newString.length(); i++) {
            if (i+1 < newString.length() && Character.isDigit(newString.charAt(i)) &&
               (Character.isLetter(newString.charAt(i+1)) || newString.charAt(i+1) == '(')) {
                newString = new StringBuilder(newString).insert(++i, "*").toString();
            }
        }
        return newString;
    }
    
    
    /**
     * Converts a string in infix notation to prefix notation
     * @param string the string which is in infix notation
     * @return the string converted to prefix notation
     */
    public Stack convertToPrefix(String string) {
        Stack main = new Stack(), 
              operators = new Stack();
        
        for (index = 0; index < string.length();) {
            //Number
            if (isNumber(string.charAt(index)) || isNumberSign(string, index)) {
                processNumberIntoStack(main, string);
                
            //Word (Variable/Function)
            } else if (Character.isLetter(string.charAt(index))) {
                processWordIntoStack(main, operators, string);
                
            //Operator
            } else if (string.charAt(index) != ')') {
                while (!operators.isEmpty() && (hasMoreHierarchy(operators.lastElement(), string.charAt(index)) || 
                                                hasSameHierarchy(operators.lastElement(), string.charAt(index)))) {
                    main.push(operators.pop());
                }
                operators.push(string.charAt(index++));
                
            //Parenthesis
            } else {
                processParenthesisIntoStack(main, operators, string);
            }
        }
        
        moveStackFromTo(operators, main);
        //Remove unwanted parenthesis
        removeElements(main, "(");
        return main;
    }
    
    
    /**
     * Returns <code>true</code> is the object is a valid number, <code>false</code> otherwhise
     * @param object the object to evaluate
     * @return <code>true</code> is the object is a valid number, <code>false</code> otherwhise
     */
    public boolean isNumber(Object object) {
        try {
            Float.parseFloat(object.toString());
        } catch (NumberFormatException e) { 
            return false;
        }
        return true;
    }
    
    
    /**
     * Returns <code>true</code> is the object is a operand, <code>false</code> otherwhise
     * @param o the object to evaluate
     * @return <code>true</code> is the object is a operand, <code>false</code> otherwhise
     */
    public boolean isOperand(Object o) {
        return (Arrays.asList("+", "-", "*", "/", "^").contains(o.toString()));
    }
    
    
    /**
     * Returns <code>true</code> is the object is a reserved function, <code>false</code> otherwhise
     * @param o the object to evaluate
     * @return <code>true</code> is the object is a reserved function, <code>false</code> otherwhise
     */
    public boolean isFunction(Object o) {
        return (Arrays.asList("sqrt", "sin", "cos", "tan", "asin", "acos", "atan", "log", "floor", "ceil", "abs", "rand", "%")
                      .contains(o.toString()));
    }
    
    
    /**
     * Returns <code>true</code> is the object is a variable, <code>false</code> otherwhise
     * @param o the object to evaluate
     * @return <code>true</code> is the object is a variable, <code>false</code> otherwhise
     */
    public boolean isVariable(Object o) {
        return (!isNumber(o) && !isOperand(o) && !isFunction(o));
    }
    
    
    /**
     * Solves an arithmetic operation
     * @param operand the operand
     * @param a the first number
     * @param b the second number
     * @return the result of the operation
     */
    public double solveOperation(Object operand, double a, double b) {
        switch (operand.toString()) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            case "^": return Math.pow(a, b);
        }
        return 0;
    }
    
    
    /**
     * Solves a function
     * @param operand the operand/function
     * @param a the number
     * @return the result of the operation
     */
    public double solveFunction(Object operand, double a) {
        switch (operand.toString()) {
            case "abs": return Math.abs(a);
            case "rand": return Math.random()*a;
            case "sqrt": return Math.sqrt(a);
            case "sin": return Math.sin(a);
            case "cos": return Math.cos(a);
            case "tan": return Math.tan(a);
            case "acos": return Math.acos(a);
            case "atan": return Math.atan(a);
            case "asin": return Math.asin(a);
            case "log": return Math.log(a);
            case "floor": return Math.floor(a);
            case "ceil": return Math.ceil(a);
            case "%": return a / 100f;
        }
        return 0;
    }
    
    
    /**
     * Solves a operation in the indicated index of the stack
     * @param stack the stack where the operation is
     * @param i the index where the operation is
     */
    public void solveOperationInPrefix(Stack stack, int i) {        
        if (stack.size() > 2) {
            double a = Double.parseDouble(stack.remove(i - 2).toString());
            double b = Double.parseDouble(stack.remove(i - 2).toString());
            Object operand = stack.remove(i - 2);
            stack.add(i - 2, solveOperation(operand, a, b));
        } else {
            float b = Float.parseFloat(stack.remove(0).toString());
            Object operand = stack.remove(0);
            stack.push(solveOperation(operand, 0, b));
        }
    }
    
    
    /**
     * Solves a function in the indicated index of the stack
     * @param stack the stack where the function is
     * @param i the index where the function is
     */
    public void solveFunctionInPrefix(Stack stack, int i) {
        double a = Double.parseDouble(stack.remove(i - 1).toString());
        Object operand = stack.remove(i - 1);
        stack.add(i - 1, solveFunction(operand, a));
    }
    
    
    /**
     * Replaces a variable with its respective value
     * @param stack the stack where the variable is
     * @param values the string with the variable declared
     * @param i the index where the variable is
     */
    public void replaceVariableWithValue(Stack stack, String values, int i) {
        String variableName = stack.elementAt(i).toString(),
               newValue = EMPTY;
        if (!values.contains(variableName + "=")) {
            stack.set(i, "0");
            return;
        }
        
        int variablePosition = values.lastIndexOf(variableName + "=") + variableName.length() + 1;
        while (variablePosition < values.length() && values.charAt(variablePosition) != NEW_LINE) {
            newValue += values.charAt(variablePosition++);
        }
        
        stack.set(i, getResult(newValue, values));
    }

    
    /**
     * Solves a prefix function
     * @param stack the prefix operation
     * @param values the string with variables declarated
     * @return a one size stack with the result
     */
    public Stack solvePrefix(Stack stack, String values) {
        for (int i = 0; i < stack.size(); i++) {
            if (isOperand(stack.elementAt(i))) {
                solveOperationInPrefix(stack, i);
                break;
            }
            
            if (isFunction(stack.elementAt(i))) {
                solveFunctionInPrefix(stack, i);
                break;
            }
            
            if (isVariable(stack.elementAt(i))) {
                replaceVariableWithValue(stack, values, i);
                break;
            }
        }
        //Recursive until all the operations are done
        if (stack.size() > 1)
            solvePrefix(stack, values);
        return stack;
    }
    
    
    /**
     * Returns the number without decimal point if it doesn't have decimals
     * or the number rounded to 4 decimals if it has it
     * @param number the number
     * @return the number without decimal point if it doesn't have decimals 
     * or the number rounded to 4 decimals if it has it
     */
    public String getFormatedNumber(Double number) {
        if (number % 1 == 0)
            return String.valueOf(number.intValue());
        return String.format(decimalPlaces, number);
    }
    
    
    /**
     * Returns the function fixed (No empty spaces and with multiplication symbols)
     * @param function the function
     * @return the function fixed (No empty spaces and with multiplication symbols)
     */
    public String fixFunction(String function) {
        return addMultiplication(function.replaceAll(" ", EMPTY));
    }
    
    
    /**
     * Returns the variables fixed (No empty spaces and with semicolons instead of line breaks)
     * @param variables the variables
     * @return the variables fixed (No empty spaces and with semicolons instead of line breaks)
     */
    public String fixVariables(String variables) {
        return variables.replaceAll(" ", EMPTY).replaceAll(";", String.valueOf(NEW_LINE));
    }
    
    
    /**
     * Converts a function from Infix to Prefix and solves it
     * @param function the Infix function
     * @param variables the variables list
     * @return the result of the function
     */
    public String getResult(String function, String variables) {
        Stack operation = convertToPrefix(fixFunction(function));
        String result = solvePrefix(operation, fixVariables(variables)).firstElement().toString();
        
        return getFormatedNumber(Double.parseDouble(result));
    }
    
}