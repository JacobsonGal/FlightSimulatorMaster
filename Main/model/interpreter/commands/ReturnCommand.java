package model.interpreter.commands;

import model.interpreter.expressions.*;
import model.interpreter.interpret.*;

public class ReturnCommand implements Command {

    @Override
    public void executeCommand(String[] array) {

        StringBuilder exp = new StringBuilder();
        for (int i = 1; i < array.length; i++)
            exp.append(array[i]);
        CompParser.returnValue = MathExpression.calc(exp.toString());
    }

}
