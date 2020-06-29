package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import model.interpreter.commands.*;
import model.interpreter.interpret.AutoPilotParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.MainModel;
import model.SimulatorModel;

public class TheMainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
    	//openFlightGear();
    	System.out.println("Welcome to Flight Simulator Controller !");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Flight.fxml"));
        Parent root = loader.load();
        
        FlightController ctrl = loader.getController();
        ViewModel viewModel=new ViewModel();
        SimulatorModel simulator=new SimulatorModel();
        MainModel model=new MainModel();
        
        model.addObserver(viewModel);
        viewModel.setModels(model,simulator);
        viewModel.addObserver(ctrl);
        ctrl.setViewModel(viewModel);
        
        primaryStage.setTitle("FlightGear Simulator Controller");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("logo.png")));
	    primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            DisconnectCommand command=new DisconnectCommand();
            String[] disconnect={""};
            command.executeCommand(disconnect);
            AutoPilotParser.thread1.interrupt();
            viewModel.stopAll();
            System.out.println("Exit Flight Simulator Controller");
        });

    }
    public static void main(String[] args) {
        launch(args);
		Platform.exit();// safe exit
		System.exit(0);
    }
    public static void openFlightGear() throws IOException {
		final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			// Target FlightGear application in the current system
		    desktop.open(new File("/Applicaitions/FlightGear.app"));
		} else {
		    throw new UnsupportedOperationException("Open action not supported");
		}
	
	}
}
