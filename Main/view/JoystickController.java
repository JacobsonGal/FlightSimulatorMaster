package view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class JoystickController {

	ViewModel viewModel;
	public Circle Joystick, border;
	public Slider rudder, throttle;
	public DoubleProperty aileron, elevator;
	double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    boolean manual;
    

	public JoystickController(Circle innerCircle, Circle outerCircle, Slider rudder, Slider throttle,ViewModel viewModel) {
	    this.viewModel=viewModel;
		this.Joystick = innerCircle;
		this.border = outerCircle;
		this.rudder = rudder;
		this.throttle = throttle;
		this.aileron = new SimpleDoubleProperty();
		this.elevator = new SimpleDoubleProperty();
	    aileron.bindBidirectional(viewModel.aileron);
        elevator.bindBidirectional(viewModel.elevator);
		orgSceneX = orgSceneY = 0;
	}

	public void innerReleased(MouseEvent e) {              	// When inner circle is released event handler 
		  ((Circle)(e.getSource())).setTranslateX(orgTranslateX);
          ((Circle)(e.getSource())).setTranslateY(orgTranslateY);
	}

	public void innerPressed(MouseEvent e) {				// When inner circle is pressed event handler 
		 orgSceneX = e.getSceneX();
         orgSceneY = e.getSceneY();
         orgTranslateX = ((Circle)(e.getSource())).getTranslateX();
         orgTranslateY = ((Circle)(e.getSource())).getTranslateY();
	}

	public void innerDragged(MouseEvent e) {				// When inner circle is dragged event handler 

		   double offsetX = e.getSceneX() - orgSceneX;
           double offsetY = e.getSceneY() - orgSceneY;
           double newTranslateX = orgTranslateX + offsetX;
           double newTranslateY = orgTranslateY + offsetY;
           if(isInCircle(newTranslateX,newTranslateY)) {
               ((Circle) (e.getSource())).setTranslateX(newTranslateX);
               ((Circle) (e.getSource())).setTranslateY(newTranslateY);
               if(manual) {
                   aileron.setValue(normalizationX(newTranslateX));
                   elevator.setValue(normalizationY(newTranslateY));
                   viewModel.setJoystick();
               }
           }
	}
    private  boolean isInCircle(double x,double y){
        return (Math.pow((x-border.getCenterX()),2)+Math.pow((y-border.getCenterY()),2))<=Math.pow(border.getRadius()-Joystick.getRadius(),2);
    }
    private double normalizationX(double num){
        double max=(border.getRadius()-Joystick.getRadius())+border.getCenterX();
        double min=border.getCenterX()-(border.getRadius()-Joystick.getRadius());
        double new_max=1;
        double new_min=-1;
        return (((num-min)/(max-min)*(new_max-new_min)+new_min));
    }
    private double normalizationY(double num){
        double min=(border.getRadius()-Joystick.getRadius())+border.getCenterY();
        double max=border.getCenterY()-(border.getRadius()-Joystick.getRadius());
        double new_max=1;
        double new_min=-1;
        return (((num-min)/(max-min)*(new_max-new_min)+new_min));
    }
}
