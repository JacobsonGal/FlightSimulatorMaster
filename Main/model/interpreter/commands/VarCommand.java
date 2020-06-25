package model.interpreter.commands;

import model.interpreter.expressions.*;
import model.interpreter.interpret.*;
import model.interpreter.*;


public class VarCommand implements Command {

	@Override
	public void executeCommand(String[] array) {
		Var v=new Var();
		if(array.length>2) {
			if (array[3].equals("bind")) {
				CompParser.symbolTable.put(array[1],CompParser.symbolTable.get(array[4]));
			}
			else {
				StringBuilder exp = new StringBuilder();
				for (int i = 3; i < array.length; i++)
					exp.append(array[i]);
				v.setV(MathExpression.calc(exp.toString()));
				CompParser.symbolTable.put(array[1],v);
			}
		}
		else
			CompParser.symbolTable.put(array[1],new Var());

	}

}
