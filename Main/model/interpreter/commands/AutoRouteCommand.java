package model.interpreter.commands;

import model.MainModel;

public class AutoRouteCommand implements Command {
    @Override
    public void executeCommand(String[] array) {
        MainModel.turn=false;
    }
}
