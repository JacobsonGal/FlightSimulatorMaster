package view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.SimulatorModel;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.*;
import java.net.URL;
import java.util.*;

public class FlightController implements Initializable, Observer {

	// -------------- Data Members ----------------- //
	private ViewModel viewModel;
	public JoystickController joystickController;
	
	// -------------- GUI Members ----------------- //
    @FXML
    private Canvas airplane;
    @FXML
    private Canvas markX;
    @FXML
    private  TextArea TextArea;
    @FXML
    private TextField port;
    @FXML
    private TextField ip;
	@FXML
	private TextArea logBar;
    @FXML
    private Slider throttle;
    @FXML
    private Slider rudder;
    @FXML
    private RadioButton auto;
    @FXML
    private MapController map;
    @FXML
    private RadioButton manual;
    @FXML
    private Circle border;
    @FXML
    private Circle Joystick;
    @FXML
    private TitledPane background;

	// ------------- Plane ----------------- //
    
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    public DoubleProperty markSceneX, markSceneY;
    public DoubleProperty aileron;
    public DoubleProperty elevator;
    public DoubleProperty airplaneX;
    public DoubleProperty airplaneY;
    public DoubleProperty startX;
    public DoubleProperty startY;
    public DoubleProperty offset;
    public DoubleProperty heading;
	
	// ------------- Map ----------------- //
    
    public double lastX;
    public double lastY;
    public int mapData[][];
    private Image plane[];
    private Image mark;
    private String[] solution;
    
	// ------------- Properties ----------------- //
    
    private BooleanProperty path;
	public BooleanProperty isConnectedToSimulator;
	private boolean mapOn=false;


    //Data binding between View and the ViewModel 
    public void setViewModel(ViewModel viewModel){
        this.viewModel=viewModel;
        throttle.valueProperty().bindBidirectional(viewModel.throttle);
        rudder.valueProperty().bindBidirectional(viewModel.rudder);
        joystickController = new JoystickController(Joystick, border, rudder, throttle,viewModel);
        aileron=new SimpleDoubleProperty();
        elevator=new SimpleDoubleProperty();
        viewModel.aileron.bindBidirectional(joystickController.aileron);
        viewModel.elevator.bindBidirectional(joystickController.elevator);
        airplaneX=new SimpleDoubleProperty();
        airplaneY=new SimpleDoubleProperty();
        startX=new SimpleDoubleProperty();
        startY=new SimpleDoubleProperty();
        airplaneX.bindBidirectional(viewModel.airplaneX);
        airplaneY.bindBidirectional(viewModel.airplaneY);
        startX.bindBidirectional(viewModel.startX);
        startY.bindBidirectional(viewModel.startY);
        offset=new SimpleDoubleProperty();
        offset.bindBidirectional(viewModel.offset);
        viewModel.script.bindBidirectional(TextArea.textProperty());
        heading=new SimpleDoubleProperty();
        heading.bindBidirectional(viewModel.heading);
        markSceneX=new SimpleDoubleProperty();
        markSceneY=new SimpleDoubleProperty();
        markSceneY.bindBidirectional(viewModel.markSceneY);
        markSceneX.bindBidirectional(viewModel.markSceneX);
        path=new SimpleBooleanProperty();
        path.bindBidirectional(viewModel.path);
        path.setValue(false);
		logBar.setEditable(false);
		isConnectedToSimulator = new SimpleBooleanProperty();
		isConnectedToSimulator.set(false);
        plane=new Image[8];
        try {
            plane[0]=new Image(new FileInputStream("./resources/plane0.png"));
            plane[1]=new Image(new FileInputStream("./resources/plane45.png"));
            plane[2]=new Image(new FileInputStream("./resources/plane90.png"));
            plane[3]=new Image(new FileInputStream("./resources/plane135.png"));
            plane[4]=new Image(new FileInputStream("./resources/plane180.png"));
            plane[5]=new Image(new FileInputStream("./resources/plane225.png"));
            plane[6]=new Image(new FileInputStream("./resources/plane270.png"));
            plane[7]=new Image(new FileInputStream("./resources/plane315.png"));
            mark=new Image(new FileInputStream("./resources/mark.png"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @FXML
    //Load the map 
    public void LoadMap() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Load MAP");
		fc.setInitialDirectory(new File("./Resources"));
		FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fc.getExtensionFilters().add(fileExtensions);
		File selectedFile = fc.showOpenDialog(null);
		
        if (selectedFile != null) {
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            ArrayList<String[]> numbers = new ArrayList<>();
            try {

                br = new BufferedReader(new FileReader(selectedFile));
                String[] start=br.readLine().split(cvsSplitBy);
                startX.setValue(Double.parseDouble(start[0]));
                startY.setValue(Double.parseDouble(start[1]));
                start=br.readLine().split(cvsSplitBy);
                offset.setValue(Double.parseDouble(start[0]));
                while ((line = br.readLine()) != null) {
                    numbers.add(line.split(cvsSplitBy));
                }
                mapData = new int[numbers.size()][];

                for (int i = 0; i < numbers.size(); i++) {
                    mapData[i] = new int[numbers.get(i).length];

                    for (int j = 0; j < numbers.get(i).length; j++) {
                        String tmp=numbers.get(i)[j];
                        mapData[i][j] = Integer.parseInt(tmp);

                    }
                }
                this.viewModel.setData(mapData);
                this.drawAirplane();
                map.setMapData(mapData);
                logBar.appendText("Map loaded succesfully!\n");
                mapOn=true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    //Connect to FlightGear simulator
    public void Connect(){
		Stage window = new Stage();
		GridPane grid = new GridPane();
		TextField ipInput = new TextField();
		TextField portInput = new TextField();
		ipInput.appendText("127.0.0.1");
		//ipInput.setPromptText("127.0.0.1");
		portInput.appendText("5402");
		//portInput.setPromptText("5402");
		Label ipCommentlabel = new Label("FlightGear simulator's IP:");
		Label portCommentlabel = new Label("FlightGear simulator's Port:");
		Button b = new Button("Connect");
		b.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			b.setCursor(Cursor.HAND);
			b.setEffect(new DropShadow());
		});
		b.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
			b.setCursor(null);
			b.setEffect(null);
		});
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Text connect = new Text("Connect");
		connect.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(connect, 0, 0);
		grid.add(ipCommentlabel, 0, 1);
		grid.add(ipInput, 1, 1);
		grid.add(portCommentlabel, 0, 2);
		grid.add(portInput, 1, 2);
		HBox hbButton = new HBox(10);
		hbButton.setAlignment(Pos.BOTTOM_CENTER);
		hbButton.getChildren().add(b);
		grid.add(hbButton, 1, 4);
		window.setTitle("Connect to FlighGear");
		window.setScene(new Scene(grid, 400, 250));
		window.show();
		b.setOnAction(e -> {
			if (!ipInput.getText().equals("") && !portInput.getText().equals("")) {
			this.viewModel.ip.bindBidirectional(ipInput.textProperty());
		    this.viewModel.port.bindBidirectional(portInput.textProperty());
            viewModel.connect();
            if (SimulatorModel.on==true) {
				isConnectedToSimulator.setValue(true);
				logBar.appendText("You are connected to the FlightGear simulator!\n");
            }
            else {
            	System.out.println("Failed connecting to FlightGear!");
            	logBar.appendText("Failed connecting to FlightGear!\n");
			}
			window.close();
			} else {
				logBar.appendText("Invalid parameters!\n");
			}
		});

    }
    @FXML
    //Connect to Solver and calculate the shortest path
    public void CalculatePath(){
    	Stage window = new Stage();
		GridPane grid = new GridPane();
		TextField ipInput = new TextField();
		TextField portInput = new TextField();
		ipInput.appendText("127.0.0.1");
		ipInput.setPromptText("127.0.0.1");
		portInput.appendText("5000");
		portInput.setPromptText("5000");
		Label ipCommentlabel = new Label("Enter IP of the solver server:");
		Label portCommentlabel = new Label("Enter Port of the solver server:");
		Button b = new Button("Connect");
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Text connect = new Text("Connect to the Solver Server");
		connect.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(connect, 0, 0);
		grid.add(ipCommentlabel, 0, 1);
		grid.add(ipInput, 1, 1);
		grid.add(portCommentlabel, 0, 2);
		grid.add(portInput, 1, 2);
		HBox hbButton = new HBox(10);
		hbButton.setAlignment(Pos.BOTTOM_CENTER);
		hbButton.getChildren().add(b);
		grid.add(hbButton, 1, 4);
		b.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			b.setCursor(Cursor.HAND);
			b.setEffect(new DropShadow());
		});
		b.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			b.setCursor(null);
			b.setEffect(null);
		});
		window.setTitle("Calculate Path");
		window.setScene(new Scene(grid, 400, 250));
		window.show();
		b.setOnAction(e -> {
			if (!ipInput.getText().equals("") && !portInput.getText().equals("")) {
			this.viewModel.ip.bindBidirectional(ipInput.textProperty());
		    this.viewModel.port.bindBidirectional(portInput.textProperty());
			double H = markX.getHeight();
            double W = markX.getWidth();
            double h = H / mapData.length;
            double w = W / mapData[0].length;
            viewModel.findPath(h,w);
            //boolean variable which indicates if this the first time you needed to find the shortest path
            path.setValue(true);
            logBar.appendText("You are connected to a solver server!\n");
            logBar.appendText("Displaying shortest path\n");
			window.close();
			} else {
				logBar.appendText("Invalid parameters!\n");}
		});
    }
    @FXML
    //Set Autopilot ON
    public void AutoPilot(){
    	if(manual.isSelected())
        {
            manual.setSelected(false);
            joystickController.manual=false;
            auto.setSelected(true);
            this.TextArea.deleteText(0,TextArea.getLength());
            
    		FileChooser fc = new FileChooser();
			fc.setTitle("Load Script File to interpret automatically");
			fc.setInitialDirectory(new File("./Resources"));
			FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
			fc.getExtensionFilters().add(fileExtensions);
			File selectedFile = fc.showOpenDialog(null);
			try {
				if (selectedFile != null) {
					Scanner sc = new Scanner(selectedFile); // Display chosen file in text area
					while (sc.hasNextLine()) {
						TextArea.appendText(sc.nextLine());
						TextArea.appendText("\n");
					}
					sc.close();
					viewModel.parse();	
				}
			} catch (FileNotFoundException e) {e.getStackTrace();}
    	

        	logBar.appendText("Autopilot Mode Activated!\n");
        }
        else if(auto.isSelected())
        {
            this.TextArea.deleteText(0,TextArea.getLength());
	    	FileChooser fc = new FileChooser();
			fc.setTitle("Load Script File to interpret automatically");
			fc.setInitialDirectory(new File("./Resources"));
			FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
			fc.getExtensionFilters().add(fileExtensions);
			File selectedFile = fc.showOpenDialog(null);
			try {
				if (selectedFile != null) {
					Scanner sc = new Scanner(selectedFile); // Display chosen file in text area
					while (sc.hasNextLine()) {
						TextArea.appendText(sc.nextLine());
						TextArea.appendText("\n");
					}
					sc.close();
					viewModel.parse();	
				}
			} catch (FileNotFoundException e) {e.getStackTrace();}
    	
        	auto.setSelected(true);
        	if(isConnectedToSimulator.getValue()) {
		        viewModel.execute();
	            logBar.appendText("Autopilot Mode Activated !\n");
        	}
        	else{
        		Connect();
        		logBar.appendText("You need to connect to FlightGear!\n");
        	}
        		
        }
        else {
	        auto.setSelected(false);
	        viewModel.stopAutoPilot();
	        logBar.appendText("Autopilot Mode diActivated!\n");
        }
    }
    @FXML
    //Set Manual Joystick ON
    public void Manual()
    {
        if(auto.isSelected())
        {
            auto.setSelected(false);
            manual.setSelected(true);
            joystickController.manual=true;
            viewModel.stopAutoPilot();
            logBar.appendText("Manual Mode Activated !\n");
        }
        else if(manual.isSelected())
        {
            manual.setSelected(true);
            joystickController.manual=true;
            logBar.appendText("Manual Mode Activated !\n");
        }
        else {
            manual.setSelected(false);
            joystickController.manual=false;
            logBar.appendText("Manual Mode diActivated !\n");
        }

    }
    //Draws an airplane on the map according to its position of flight
    public void drawAirplane(){
        if(airplaneX.getValue()!=null&&airplaneY.getValue()!=null)
        {

            double H = airplane.getHeight();
            double W = airplane.getWidth();
            double h = H / mapData.length;
            double w = W / mapData[0].length;
            GraphicsContext gc = airplane.getGraphicsContext2D();
            lastX=airplaneX.getValue();
            lastY=airplaneY.getValue()*-1;
            gc.clearRect(0,0,W,H);

            if(heading.getValue()>=0&&heading.getValue()<39) 
                gc.drawImage(plane[0], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=39&&heading.getValue()<80)
                gc.drawImage(plane[1], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=80&&heading.getValue()<129)
                gc.drawImage(plane[2], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=129&&heading.getValue()<170)
                gc.drawImage(plane[3], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=170&&heading.getValue()<219)
                gc.drawImage(plane[4], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=219&&heading.getValue()<260)
                gc.drawImage(plane[5], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=260&&heading.getValue()<309)
                gc.drawImage(plane[6], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=309)
                gc.drawImage(plane[7], w*lastX, lastY*h, 25, 25);
        }

    }
    //Draw the shortest path 
    public void drawMark(){
        double H = markX.getHeight();
        double W = markX.getWidth();
        double h = H / mapData.length;
        double w = W / mapData[0].length;
        GraphicsContext gc = markX.getGraphicsContext2D();
        gc.clearRect(0,0,W,H);
        gc.drawImage(mark, markSceneX.getValue()-13,markSceneY.getValue() , 25, 25);
        if(path.getValue())
            viewModel.findPath(h,w);
    }
    //Draw the line from airplane to target
    public void drawLine(){
        double H = markX.getHeight();
        double W = markX.getWidth();
        double h = H / mapData.length;
        double w = W / mapData[0].length;
        GraphicsContext gc=markX.getGraphicsContext2D();
        String move=solution[1];
        double x= airplaneX.getValue()*w+10*w;
        double y=airplaneY.getValue()*-h+6*h;
        for(int i=2;i<solution.length;i++)
        {
            switch (move) {
                case "Right":
                    gc.setStroke(Color.BLACK.darker());
                    gc.strokeLine(x, y, x + w, y);
                    x +=  w;
                    break;
                case "Left":
                    gc.setStroke(Color.BLACK.darker());
                    gc.strokeLine(x, y, x -  w, y);
                    x -=  w;
                    break;
                case "Up":
                    gc.setStroke(Color.BLACK.darker());
                    gc.strokeLine(x, y, x, y - h);
                    y -=  h;
                    break;
                case "Down":
                    gc.setStroke(Color.BLACK.darker());
                    gc.strokeLine(x, y, x, y +  h);
                    y += h;
            }
            move=solution[i];
        }
    }
    //Event - pressing on the map
    EventHandler<MouseEvent> mapClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
        	if(mapOn) {
	            markSceneX.setValue(e.getX());
	            markSceneY.setValue(e.getY());
	            drawMark();
        	}
        	else
        	{
        		System.out.println("You didnt load the map!");
        		logBar.appendText("You didnt load the map!\n");
        	}
        }
    };
    //Event - Pressing on the joystick
    EventHandler<MouseEvent> joystickClick =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                	if (!isConnectedToSimulator.get())
            			logBar.appendText("You are not connected to the Simulator!\n");
                	else 
                		joystickController.innerPressed(t);
                }
            };
    //Event - Dragging the joystick
    EventHandler<MouseEvent> joystickMove =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                	if (!isConnectedToSimulator.get())
            			logBar.appendText("You are not connected to the Simulator!\n");
                	else {
                		joystickController.innerDragged(t);
                		logBar.appendText("Set Aileron: "+joystickController.aileron.get()+" || "+"Elevator: "+joystickController.elevator.get()+"\n");    
                	}
                }
            };
    //Event - Releasing the joystick
    EventHandler<MouseEvent> joystickRelease = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                	if (!isConnectedToSimulator.get())
            			logBar.appendText("You are not connected to the Simulator!\n");
                	else {
                		joystickController.innerReleased(t);
                		logBar.appendText("Set Aileron: "+joystickController.aileron.get()+" || "+"Elevator: "+joystickController.elevator.get()+"\n");
                	}
                }
            };
    //Get data from the mouse 
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if(location.getPath().contains("Flight.fxml")) {

            throttle.valueProperty().addListener((observable, oldValue, newValue) -> {
            	if (!isConnectedToSimulator.get())
    				logBar.appendText("You are not connected to the Simulator!\n");
    			else if (manual.isSelected()) {
                    viewModel.setThrottle();
            		logBar.appendText("Set Rudder: "+throttle.getValue()+"\n");
    			}
            });

            rudder.valueProperty().addListener((observable, oldValue, newValue) -> {
            	if (!isConnectedToSimulator.get())
    				logBar.appendText("You are not connected to the Simulator!\n");
    			else if (manual.isSelected()) {
                    viewModel.setRudder();
                	logBar.appendText("Set Rudder: "+rudder.getValue()+"\n");
    			}
            });
            Joystick.setOnMousePressed(joystickClick);
            Joystick.setOnMouseDragged(joystickMove);
            Joystick.setOnMouseReleased(joystickRelease);
            markX.setOnMouseClicked(mapClick);
        }
    }
    //Update observers
    @Override
    public void update(Observable o, Object arg) {
        if(o==viewModel)
        {
            if(arg==null)
                drawAirplane();
            else
            {
                solution=(String[])arg;
                this.drawLine();
            }
        }
    }
}
