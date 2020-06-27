package view;

import javafx.beans.property.*;
import model.InterpreterModel;
import model.MainModel;
import model.SimulatorModel;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ViewModel extends Observable implements Observer {
	// -------------- Models Members ----------------- //
	private  MainModel mainModel;
	private  InterpreterModel interpreterModel;
	private SimulatorModel simulatorModel;
	
	// -------------- Airplane Properties ----------------- //
	public DoubleProperty throttle;
    public DoubleProperty rudder;
    public DoubleProperty aileron;
    public DoubleProperty elevator;
    public DoubleProperty heading;
    public DoubleProperty airplaneX;
    public DoubleProperty airplaneY;
    
	// -------------- Map Properties ----------------- //
    public StringProperty ip;
    public StringProperty port;
    public DoubleProperty startX;
    public DoubleProperty startY;
    public DoubleProperty offset;
    public StringProperty script;
    public DoubleProperty markSceneX, markSceneY;
    public BooleanProperty path;
    private int data[][];

    public ViewModel() {
        throttle=new SimpleDoubleProperty();
        rudder=new SimpleDoubleProperty();
        aileron=new SimpleDoubleProperty();
        elevator=new SimpleDoubleProperty();
        ip=new SimpleStringProperty();
        port=new SimpleStringProperty();
        airplaneX=new SimpleDoubleProperty();
        airplaneY=new SimpleDoubleProperty();
        startX=new SimpleDoubleProperty();
        startY=new SimpleDoubleProperty();
        offset=new SimpleDoubleProperty();
        script=new SimpleStringProperty();
        heading=new SimpleDoubleProperty();
        markSceneX=new SimpleDoubleProperty();
        markSceneY=new SimpleDoubleProperty();
        path=new SimpleBooleanProperty();
    }

    public void setData(int[][] data)
    {
        this.data = data;
        mainModel.GetPlane(startX.getValue(),startY.doubleValue(),offset.getValue());
    }
    public void setModels(MainModel model,SimulatorModel simulatorModel){
        this.mainModel=model;
        this.simulatorModel=simulatorModel;
        this.interpreterModel=new InterpreterModel();
    }
    public void setThrottle(){
        String[] data={"set /controls/engines/current-engine/throttle "+throttle.getValue()};
        simulatorModel.Send(data);
    }
    public void setRudder(){
        String[] data={"set /controls/flight/rudder "+rudder.getValue()};
        simulatorModel.Send(data);
    }
    public void setJoystick(){
        String[] data = {
                "set /controls/flight/aileron " + aileron.getValue(),
                "set /controls/flight/elevator " + elevator.getValue(),
        };
        simulatorModel.Send(data);
    }
    public void connectManual(String ip,int port){
    	simulatorModel.Connect(ip,port);
    }
    public void connect(){
    	simulatorModel.Connect(ip.getValue(),Integer.parseInt(port.getValue()));
    }
    public void parse(){
        Scanner scanner=new Scanner(script.getValue());
        ArrayList<String> list=new ArrayList<>();
        while(scanner.hasNextLine())
        {
            list.add(scanner.nextLine());
        }
        String[] tmp=new String[list.size()];
        for(int i=0;i<list.size();i++)
        {
            tmp[i]=list.get(i);
        }
        interpreterModel.interpet(tmp);
    }
    public void execute(){
        interpreterModel.execute();
    }
    public void stopAutoPilot(){
        interpreterModel.stop();
    }
    public void findPath(double h,double w) {
        if (!path.getValue())
        {
            mainModel.connectPath(ip.getValue(), Integer.parseInt(port.getValue()));
        }
        mainModel.findPath((int) (airplaneY.getValue()/-1), (int) (airplaneX.getValue() +15),Math.abs( (int) (markSceneY.getValue() / h)) ,
               Math.abs((int) (markSceneX.getValue() / w)), data );
    }
    public void stopAll()
    {
    	simulatorModel.stop();
    	mainModel.stopAll();
    }
    @Override
    public void update(Observable o, Object arg) {
        if(o==mainModel)
        {
            String[] tmp=(String[])arg;
            if(tmp[0].equals("plane")) {
                double x = Double.parseDouble(tmp[1]);
                double y = Double.parseDouble(tmp[2]);
                x = (x - startX.getValue() + offset.getValue());
                x /= offset.getValue();
                y = (y - startY.getValue() + offset.getValue()) / offset.getValue();
                airplaneX.setValue(x);
                airplaneY.setValue(y);
                heading.setValue(Double.parseDouble(tmp[3]));
                setChanged();
                notifyObservers();
            }
            else if(tmp[0].equals("path"))
            {
                setChanged();
                notifyObservers(tmp);
            }
        }
    }
}
