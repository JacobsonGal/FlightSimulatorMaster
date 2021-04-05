# Advanced Software Development - Final Project
[![Presentation Project](/UML/main.png)](https://www.youtube.com/watch?v=NhhZrdrjUVs)

# Flight Simulator Controller 
![Presentation Project](/UML/example.png)

## Server

In this section we wrote a general server, which can be used over and over again in various projects.
In order for the server to be re-usable, there must be a seperation between the server's functionality and the rest of the code. 

Therefore, we defined the functionality of the server as an interface,
and each project can have different classes that will implement the same functionality in different ways.
Thus, the **Open / Close principle** has been applied.

Now the ```Server``` interface has a quite simple functionality:
* A method that receives a port for listening and its function will be to open the server and wait for clients.
* A method to close the server.

For this project we will use a class called ```MySerialServer``` that will be a type of ```Server```.

### ClientHandler

Imagine a situation in which the ```MySerialServer``` class would also define the client-server call protocol.
In different projects, there might be different conversations in different formats and with different expectations between the client and the server.
Therefore, we won't be able to use this class in other projects. 

To solve that issue, we had to separate the server mechanism implemented in ```MySerialServer``` from different forms of conversation with possible clients.
For that reason we created an interface called ```ClientHandler``` to determine the type of call with the client and its handling.
Now ```MySerialServer``` class can inject any desired implementation for ```ClientHandler```.


For example, for every implementation of a ```Server``` we can inject a call of inversion of strings or solving equations.
In the same way, if one day we would like to implement additional protocols then we will only need to add the implementation of ```ClientHandler``` without changing or copying again the code of the various implementations to the ```Server```.

In this method, we maintained both the **Single Responsibility** and **Open / Close** principles.

## Caching

The project also has a caching system,
for it might take a lot of time to calculate some solutions.
It would be redundant to calculate a solution for a problem that we already solved.
Instead, we can save solutions that were already calculated in an external file, or a database.
Upon receiving a new problem, we will first check the cache to see if we have already solved it.
If so, we will extract the solution from the disk instead of calculating it.

 
We created the ```CacheManager``` interface to manage the cache for us, with the following functionalities:
* Checks whether the solution already exists in the database.
* Extracts the data from the database (If a solution already exists).
* Saves the solution for the problem.

## UML

![ServerClient Java UML](/UML/project_uml.png "ServerClient Java UML")

### The Problem Server
Given a graph, it could solve it using [A-star](https://en.wikipedia.org/wiki/A*_search_algorithm) algorithm ( which is already implemented in this project based on djkistra algorithm using manhattan distances ) or any other search algorithm.

<p align="center">
  <img src="/UML/server_bridgepattern.png" width="600">
</p>
In our concrete server, given a weighted graph, it will run the search algorithm, and as an output it will return the cheapest route to the target.

You can see that the Bridge Design Pattern was implemented, as we created a separation between the problem, and what solves the problem. That way we can solve various problems through different solutions.

The specific problem and solution in this project, is that when given a matrix the server will be able to solve it and return the quickest path from point A to point B using **A-star** algorithm as said before.

---
##   Interpreter 

As stated at the beginning of the repository, the project is a GUI of a flight simulator by which you can control the plane and get information from it.

One of its features is running a script, which is basically a kind of custom programming language that can handle the plane.

As in the following example:

```scala
openDataServer 5400 10
connect 127.0.0.1 5402
var breaks = bind "/controls/flight/speedbrake"
var throttle = bind "/controls/engines/current-engine/throttle"
var heading = bind "/instrumentation/heading-indicator/indicated-heading-deg"
var roll = bind "/instrumentation/attitude-indicator/indicated-roll-deg"
var pitch = bind "/instrumentation/attitude-indicator/internal-pitch-deg"
var rudder = bind "/controls/flight/rudder"
var aileron = bind "/controls/flight/aileron"
var elevator = bind "/controls/flight/elevator"
var alt = bind "/instrumentation/altimeter/indicated-altitude-ft"
breaks = 0
throttle = 1
var h0 = heading
sleep 5000
while alt < 1000 {
	rudder = (h0 - heading)/180
	aileron = - roll / 70
	elevator = pitch / 50
	sleep 150
}

```
For this purpose, we wrote a code reader, an interpreter, which allows you to connect to the simulator, open a server, and run various commands that control the plane and sample its data.

In the text above, we see a while loop that will take place as long as the plane’s altitude is less than a 1000 meters, the loop content will give orders to the plane's acceleration and elevation.
In this part:
```scala
rudder = (h0 - heading)/180
```
We can see that arithmetic expressions are supported as well, and to interpret them we use [Dijkstra's Shunting Yard algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm).

---

### Command Pattern

<p align="center">
  <img src="/UML/CommandPattern.png" width="600">
</p>

In this project there is an extensive use of commands, the plane needs to receive a lot of instructions in a short period of time 
in order to fly correctly. For that matter, the most suitable design pattern for the task is the Command Pattern. 
The Command Pattern implementation can be seen in our ```CompParser``` - each command in the program is receiving its own Command Object.

It is important that all commands will implement the same interface, because we want them to have a common polymorphic denominator.

Another reason to use the Command Pattern is for when we need an assembly of commands at once. For example, we needed a command that holds other different commands inside of it. In that case, we combined the **Command Pattern** with **Composite Pattern**.

So if, for example, we take a look at the "loop" command or "if" command, then we can see that each contains a list of commands which in turn can be either a standard single command or a list of commands.

---
### Interpreter stages

![Presentation Project](/UML/interpreter.png)


The first stage that happens in the interpretation process is ``Lexer``.

The Lexer takes the string as it is, and converts it to a logical distribution according to commands and parameters that can run later on with a Scanner.

The next stage is the ``Parser`` stage, which begins converting the "array" created by the Lexer into commands and executes them.

The last stage is the ``Send`` stage that occurred after the intrpreter finish to intrpret, which send the commands to the simulator.

---
## MVVM Architecture

<p align="center">
  <img src="/UML/mvvm.png" width="600">
</p>

In this project we chose to use the **MVVM architecture**.

We have the View layer that is responsible for the presentation, for example the 
input from the user. The View is also responsible for producing the graphics and has the code-
behind - for example, functions that are activated when we press a button, which are called
event-oriented programming.

* **Model** – Responsible for our business logic, such as algorithms and data access.
* **View Model** – It passes commands from the View to the Model, and its purpose is to
separate the View from the Model.
* **Data Binding** – We can wrap variables such as those in the View, and then when we change
something in the text, it will automatically changed in the ViewModel.

![Presentation Project](/UML/databind.png)

For the MVVM architecture to work, we'll have to wrap the different components together. 
This is done by the Observer Pattern, which binds the different components together, and notify them about changes that are made or needs to be made as required by the operator. 

```java
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
```

## Implements
* Java | XML | CSS | 

## Built With
* [Eclipse](https://www.eclipse.org/downloads/packages/release/kepler/sr1/eclipse-ide-java-developers) - Java IDE
* [Scene Builder](https://gluonhq.com/products/scene-builder/)  - Scene Builder 8.5.0


## Authors
* **[Gal Jacobson](https://www.linkedin.com/in/jacobsongal/)** & **[Roey Miller](https://www.linkedin.com/in/roey-miller-046b68199/)**
