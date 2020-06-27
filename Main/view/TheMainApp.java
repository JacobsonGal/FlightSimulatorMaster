package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import model.interpreter.commands.*;
import model.server.MySerialServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.MainModel;

public class TheMainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
    	//openFlightGear();
    	//MySerialServer.main(null);
    	System.out.println("Welcome to Flight Simulator Controller !");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Flight.fxml"));
        Parent root = loader.load();
        FlightController ctrl = loader.getController();
        ViewModel viewModel=new ViewModel();
        MainModel model=new MainModel();
        model.addObserver(viewModel);
        viewModel.setModel(model);
        viewModel.addObserver(ctrl);
        ctrl.setViewModel(viewModel);
        primaryStage.setTitle("FlightGear Simulator Controller");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("./images/logo.png")));
	    primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            DisconnectCommand command=new DisconnectCommand();
            String[] disconnect={""};
            command.executeCommand(disconnect);
            //AutoPilotParser.thread1.interrupt();
            model.stopAll();
            System.out.println("Exit Flight Simulator Controller");
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void openFlightGear() throws IOException {
		final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			// target FlightGear application in the current system
		    desktop.open(new File("/Applications/FlightGear.app"));
		} else {
		    throw new UnsupportedOperationException("Browse action not supported");
		}
	
	}
}
