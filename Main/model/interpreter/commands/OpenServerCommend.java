package model.interpreter.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import model.interpreter.expressions.*;
import model.interpreter.interpret.*;
import model.server.network.*;
import model.server.*;

public class OpenServerCommend implements Command {
	public static volatile boolean stop=false;
	public static Object wait=new Object();
	Server s;
	@Override
	public void executeCommand(String[] array) {
		stop=false;

		s=new MySerialServer();
		s.open(Integer.parseInt(array[1]), new ClientHandler() {
			
			@Override
			public void handleClient(InputStream in, OutputStream out) {
				BufferedReader Bin=new BufferedReader(new InputStreamReader(in));
				synchronized (OpenServerCommend.wait) {
					OpenServerCommend.wait.notifyAll();
				}
				
				while (!stop) {
					try {
						String Line;
						Line = Bin.readLine();
						String[] vars = Line.split(",");
						for (int i=0;i<vars.length;i++)
						{
							if(Double.parseDouble(vars[i])!=CompParser.symbolTable.get(CompParser.vars.get(i)).getV())
								CompParser.symbolTable.get(CompParser.vars.get(i)).setV(Double.parseDouble(vars[i]));

						}



					} catch(IOException e1){
						e1.printStackTrace();
					}
					try {
						long num = (long) MathExpression.calc("1000/" + array[2]);
						Thread.sleep(num);
					} catch (InterruptedException e) {
					}
				}
				s.stop();
			}
		});


	}

}
