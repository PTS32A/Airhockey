/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import s32a.Shared.enums.GameStatus;
import com.badlogic.gdx.math.Vector2;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import s32a.Client.ClientData.GameClient;
import s32a.Client.timers.AFKTimerTask;
import s32a.Client.timers.GameTimeTask;
import s32a.Shared.*;
import s32a.Shared.enums.Colors;

/**
 *
 * @author Luke, Bob
 */
public class GameFX extends AirhockeyGUI implements Initializable {

    @Getter
    @Setter
    Stage myStage = null;

    @FXML
    Label lblPlayer1Name, lblPlayer2Name, lblPlayer3Name, lblDifficulty, lblScoreP1,
            lblScoreP2, lblScoreP3, lblRound, lblTime, lblCount, lblGameOver
    @FXML
    Button btnStart, btnPause, btnQuit, btnStopSpec;
    @FXML
    ListView lvChatbox;
    @FXML
    TextField tfChatbox;
    @FXML
    Slider sldCustomDifficulty;
    @FXML
    CheckBox cbxCustomDifficulty;
    @FXML
    AnchorPane apGame;

    Arc bat1, bat2, bat3;
    Circle puck;
    Line bottomLine, leftLine, rightLine, bGoal, lGoal, rGoal;

    private DoubleProperty width, height;
    private IntegerProperty customSpeed;
    private boolean gameStart = false;
    @Getter
    private boolean actionTaken = true;

    @Setter
    @Getter
    private GameClient myGame;
    private ScheduledExecutorService gameTimer;
    private AFKTimerTask afkTimerTask = null;
    private GameTimeTask gameTimeTask = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.width = new SimpleDoubleProperty(.0);
        this.height = new SimpleDoubleProperty(.0);
        
        setUp();
    }

    /**
     * Sets all labels up for players and spectators. Button layout for Player
     * and Spectator is different, buttons.setVisible is set to false depending
     * on type of Person. Canvas graphics are set up and initial field is drawn
     * with method drawEdges().
     */
    public void setUp() {
        IPerson myPerson = super.getMe();
        gameTimer = Executors.newScheduledThreadPool(2);

        if (myPerson instanceof IPlayer) {

            try {
                // Player
                btnStopSpec.setVisible(false);
                btnPause.setDisable(true);

                // bind custom difficulty indicators
                this.customSpeed = new SimpleIntegerProperty(15);
                this.customSpeed.bindBidirectional(this.sldCustomDifficulty.valueProperty());
                this.cbxCustomDifficulty.textProperty().bind(
                        Bindings.concat("Use custom Speed: ", customSpeed.asString()));

                // adds listeners governing custom difficulty
                this.addDifficultyListeners();
            } catch (Exception ex) {
                System.out.println("RemoteException on setting player info in setUp: " + ex.getMessage());
                Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (myPerson instanceof ISpectator) {
            btnStart.setVisible(false);
            btnPause.setVisible(false);
            btnQuit.setVisible(false);
            this.sldCustomDifficulty.setVisible(false);
            this.cbxCustomDifficulty.setVisible(false);
        } else {
            showDialog("Error", "myPerson was neither player nor spectator");
        }

        // binds width / height for redrawing to canvas size
        this.width.bind(this.apGame.prefWidthProperty());
        this.height.bind(Bindings.subtract(this.apGame.prefHeightProperty(), 1));

        // adds chatbox accept event
        this.tfChatbox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    sendMessage(null);
                }
            }
        });
    }

    /**
     * Binds gameclient properties to various listeners and bindings in this GameFX.
     * Done so after gameclient is initialized (not on startup gamefx)
     */
    public void bindMyGameProperties() {
        // Disables certain controls if person is not starter
        myGame.getPlayer1NameProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(!myGame.getPlayer1NameProperty().get().equals(me)){
                    cbxCustomDifficulty.setDisable(true);
                    sldCustomDifficulty.setDisable(true);
                    btnStart.setDisable(true);
                    myGame.getPlayer1NameProperty().removeListener(this);
                }
            }
        });

        IPerson myPerson = super.getMe();
        // binds upDateTime property
        this.lblTime.textProperty().bind(myGame.getGameTimeProperty());

        // binds Player name labels
        lblPlayer1Name.textProperty().bind(myGame.getPlayer1NameProperty());
        lblPlayer2Name.textProperty().bind(myGame.getPlayer2NameProperty());
        lblPlayer3Name.textProperty().bind(myGame.getPlayer3NameProperty());

        // binds player Score labels
        this.lblScoreP1.textProperty().bind(myGame.getPlayer1Score().asString());
        this.lblScoreP2.textProperty().bind(myGame.getPlayer2Score().asString());
        this.lblScoreP3.textProperty().bind(myGame.getPlayer3Score().asString());

        // round number
        this.lblRound.textProperty().bind(myGame.getRoundNoProperty().asString());

        // Chatbox
        this.lvChatbox.setItems(myGame.getOChat());

        // Difficulty 
        this.lblDifficulty.textProperty().bind(myGame.getDifficultyProperty());

        this.addGameStatusListeners();
        // draws the canvas
        this.drawEdges();

        if (myPerson instanceof IPlayer) {
            IPlayer myPlayer = (IPlayer) myPerson;
            double bX = width.get() / 2;
            double bY = height.get() - bX * Math.tan(Math.toRadians(30));
            try {
                if (myPlayer.getColor() == Colors.Blue) {
                    this.apGame.getTransforms().add(new Rotate(-120, bX, bY, 0, Rotate.Z_AXIS));
                } else if (myPlayer.getColor() == Colors.Green) {
                    this.apGame.getTransforms().add(new Rotate(120, bX, bY, 0, Rotate.Z_AXIS));
                }
            } catch (RemoteException ex) {
                Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Starts a listener for gamestatus.waiting. Whenever gamestatus becomes
     * Waiting, pulls count down times from game.
     */
    private void addGameStatusListeners() {
        // timer task. self-cancelling once countdown is over
        TimerTask countDownDisplay = new TimerTask() {

            @Override
            public void run() {
                try {
                    int countDown = myGame.getCountDownTime();
                    displayCountDown(countDown);
                    if (countDown <= 0) {
                        this.cancel();
                    }
                } catch (RemoteException ex) {
                    System.out.println("RemoteException retrieving countdown from game: " + ex.getMessage());
                }
            }
        };

        // adds listener to gamestatus property
        myGame.getGameStatusProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (myGame.getGameStatusProperty().get() == GameStatus.Waiting) {
                    try{
                        gameTimer.scheduleAtFixedRate(countDownDisplay, 10, 500, TimeUnit.MILLISECONDS);
                    } catch (RejectedExecutionException ex){
                        System.out.println("rejected execution of countdowndisplay");
                    }
                } 
            }
        });
    }

    /**
     * Threadsafe display of given string in countdown display of given value.
     *
     * @param countDown
     */
    private void displayCountDown(int countDown) {
        final String countDownString;
        if (countDown > 0) {
            countDownString = String.valueOf(countDown);
        } else {
            countDownString = "";
        }

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                lblCount.setText(countDownString);
            }
        });
    }

    /**
     * Generates puck, and binds properties
     */
    private void drawPuck() {
        puck = new Circle();
        puck.radiusProperty().bind(Bindings.multiply(width, 0.02));
        puck.centerXProperty().bind(Bindings.add(myGame.getPuckXProperty(),
                Bindings.divide(width, 2)));
        puck.centerYProperty().bind(Bindings.subtract(height, myGame.getPuckYProperty()));
        apGame.getChildren().add(puck);
    }

    /**
     * Draws all edges and goals. Is redrawn along with draw()
     */
    public void drawEdges() {
        // Left corner of triangle
        double aX = 0;
        double aY = height.get();
        // Top corner of triangle
        double bX = width.get() / 2;
        double bY = height.get() - (width.get()
                * Math.sin(Math.toRadians(60)));
        // Right corner of triangle
        double cX = width.get();
        double cY = height.get();

        // Bottom goal
        Vector2 aXY1 = new Vector2((float) (aX + ((cX - aX) / 100 * 30)),
                (float) (aY + ((cY - aY) / 100 * 30)) - 1);
        Vector2 aXY2 = new Vector2((float) (aX + ((cX - aX) / 100 * 70)),
                (float) (aY + ((cY - aY) / 100 * 70)) - 1);
        // Left goal
        Vector2 bXY1 = new Vector2((float) (aX + ((bX - aX) / 100 * 30)) + 1,
                (float) (aY + ((bY - aY) / 100 * 30)));
        Vector2 bXY2 = new Vector2((float) (aX + ((bX - aX) / 100 * 70)) + 1,
                (float) (aY + ((bY - aY) / 100 * 70)));
        // Right goal
        Vector2 cXY1 = new Vector2((float) (cX + ((bX - cX) / 100 * 30)) - 1,
                (float) (cY + ((bY - cY) / 100 * 30)));
        Vector2 cXY2 = new Vector2((float) (cX + ((bX - cX) / 100 * 70)) - 1,
                (float) (cY + ((bY - cY) / 100 * 70)));

        // LINES EXAMPLE
        leftLine = new Line(aX, aY, bX, bY);
        rightLine = new Line(bX, bY, cX, cY);
        bottomLine = new Line(cX, cY, aX, aY);
        bGoal = new Line(aXY1.x, aXY1.y, aXY2.x, aXY2.y);
        lGoal = new Line(bXY1.x, bXY1.y, bXY2.x, bXY2.y);
        rGoal = new Line(cXY1.x, cXY1.y, cXY2.x, cXY2.y);
        lGoal.setStroke(Color.BLUE);
        rGoal.setStroke(Color.LIMEGREEN);
        bGoal.setStroke(Color.RED);
        lGoal.setStrokeWidth(4.0);
        rGoal.setStrokeWidth(4.0);
        bGoal.setStrokeWidth(4.0);
        apGame.getChildren().add(bottomLine);
        apGame.getChildren().add(rightLine);
        apGame.getChildren().add(leftLine);
        apGame.getChildren().add(lGoal);
        apGame.getChildren().add(rGoal);
        apGame.getChildren().add(bGoal);

        //Draws and adds players for the first time
        if (!gameStart) {
            Vector2 batPos2 = new Vector2((float) (aX + ((bX - aX) / 100 * 50)),
                    (float) ((aY + ((bY - aY) / 100 * 50))));
            Vector2 batPos3 = new Vector2((float) (cX + ((bX - cX) / 100 * 50)),
                    (float) ((cY + ((bY - cY) / 100 * 50))));
            double bat = (double) width.doubleValue() / 100 * 8;
            // bat 1
            bat1 = new Arc(cX / 2, cY, bat / 2, bat / 2, 0, 180);
            bat1.centerXProperty().bind(Bindings.add(myGame.getPlayer1XProperty(), Bindings.divide(width, 2)));
            bat1.centerYProperty().bind(Bindings.subtract(this.height, myGame.getPlayer1YProperty()));
            // bat 2
            bat2 = new Arc(batPos2.x, batPos2.y, bat / 2, bat / 2, 240, 180);
            bat2.centerXProperty().bind(Bindings.add(myGame.getPlayer2XProperty(), Bindings.divide(width, 2)));
            bat2.centerYProperty().bind(Bindings.subtract(this.height, myGame.getPlayer2YProperty()));
            // bat 3
            bat3 = new Arc(batPos3.x, batPos3.y, bat / 2, bat / 2, 120, 180);
            bat3.centerXProperty().bind(Bindings.add(myGame.getPlayer3XProperty(), Bindings.divide(width, 2)));
            bat3.centerYProperty().bind(Bindings.subtract(this.height, myGame.getPlayer3YProperty()));
            // set colors
            bat1.setFill(Color.RED);
            bat2.setFill(Color.BLUE);
            bat3.setFill(Color.LIMEGREEN);
            // add to children
            apGame.getChildren().add(bat1);
            apGame.getChildren().add(bat2);
            apGame.getChildren().add(bat3);

            this.drawPuck();
            gameStart = true;
        }
    }

    /**
     * Checks if there are enough players to start. Disables start button when
     * clicked and start is successful
     *
     * @param evt
     */
    @FXML
    public void startClick(Event evt) {
        if (myGame != null && myGame.getMyPlayers().size() == 3) {

            try {
                if (myGame.beginGame()) {
                    btnStart.setDisable(true);
                    btnPause.setDisable(false);
                    this.sldCustomDifficulty.setDisable(true);
                    this.cbxCustomDifficulty.setDisable(true);
                } else {
                    super.showDialog("Error", "Failed to begin game");
                }
            } catch (RemoteException ex) {
                System.out.println("RemoteException on beginGame: " + ex.getMessage());
                Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            super.showDialog("Warning", "Not enough players to begin game.");
        }
    }

    /**
     * Selects or unselect custom difficulty
     *
     * @param evt
     */
    public void customDifficultySelect(Event evt) {
        try {
            if (cbxCustomDifficulty.isSelected()) {

                myGame.adjustDifficulty((float) Math.round(sldCustomDifficulty.getValue()));

            } else {
                myGame.adjustDifficulty();
            }
        } catch (RemoteException ex) {
            System.out.println("RemoteException in setting difficulty: " + ex.getMessage());
            Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pauses and unpauses game
     *
     * @param evt
     */
    @FXML
    public void pauseClick(Event evt) {
        try {
            myGame.pauseGame(!myGame.getGameStatusProperty().get().equals(GameStatus.Paused));
            actionTaken = true;
        } catch (RemoteException ex) {
            System.out.println("RemoteException on pausing / unpausing the game: " + ex.getMessage());
            Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calls lobby.endGame(). Closes down fx applications.
     *
     * @param evt
     */
    @FXML
    public void quitClick(Event evt) {
        try {
            if (afkTimerTask != null) {
                afkTimerTask.cancel();
            }
            if (gameTimeTask != null) {
                gameTimeTask.cancel();
            }
            if (gameTimer != null) {
                gameTimer.shutdownNow();
            }
            IPerson myPerson = super.getMe();
            this.displayPostGameStats();
            GameStatus status = myGame.getGameStatusProperty().get();
            if (myPerson instanceof ISpectator) {
                lobby.stopSpectating(myGame.getID(), myPerson.getName());
            } else if (status.equals(GameStatus.GameOver) || status.equals(GameStatus.Preparing)) {              
                lobby.endGame(myGame.getID(), null);
            } else {
                lobby.endGame(myGame.getID(), myPerson.getName());
            }
            myGame.addChatMessage("has left the game", super.getMe().getName());
            closeMyStage();
        } catch (RemoteException ex) {
            System.out.println("RemoteException on quitClick: " + ex.getMessage());
            Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send message in chatbox
     *
     * @param evt
     */
    @FXML
    public void sendMessage(Event evt) {
        IPerson currentPerson = super.getMe();

        if (currentPerson != null) {
            try {
                myGame.addChatMessage(tfChatbox.getText(), currentPerson.getName());
            } catch (RemoteException ex) {
                System.out.println("RemoteException in addChatMessage: " + ex.getMessage());
            }
        } else {
            showDialog("Error", "Current Person is null.");
        }
        tfChatbox.setText("");
    }

    /**
     * Closes window, removes spectator from game, and returns spectator to
     * Personhood.
     *
     * @param evt
     */
    @FXML
    public void stopSpectating(Event evt) {
        try {
            lobby.stopSpectating(myGame.getID(), super.getMe().getName());
            closeMyStage();
        } catch (RemoteException ex) {
            System.out.println("RemoteException on stopSpectating: " + ex.getMessage());
            Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds Eventhandlers to the screen for moving the bat
     *
     * @param myPlayer
     */
    public void addEvents(IPlayer myPlayer) {
        // timer for afk timeout - probably should be moved serverside
        afkTimerTask = new AFKTimerTask(this);
        gameTimer.scheduleAtFixedRate(afkTimerTask, 500, 5000, TimeUnit.MILLISECONDS);
        
        gameTimeTask = new GameTimeTask(myGame);
        gameTimer.scheduleAtFixedRate(gameTimeTask, 100, 1000, TimeUnit.MILLISECONDS);
        
        //Moving left or right
        final EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent keyEvent) {
                try {
                    if (keyEvent.getCode() == KeyCode.A
                            || keyEvent.getCode() == KeyCode.LEFT) {
                        if (!myGame.getGameStatusProperty().get().equals(GameStatus.Paused)) {
                            myPlayer.moveBat(-1);
                            actionTaken = true;
                        }
                    } else if (keyEvent.getCode() == KeyCode.D
                            || keyEvent.getCode() == KeyCode.RIGHT) {
                        if (!myGame.getGameStatusProperty().get().equals(GameStatus.Paused)) {
                            myPlayer.moveBat(1);
                            actionTaken = true;
                        }
                    }
                } catch (RemoteException ex) {
                    System.out.println("RemoteException in moveBat: " + ex.getMessage());
                }

            }

        };

        //Stop moving
        final EventHandler<KeyEvent> keyReleased = new EventHandler<KeyEvent>() {

            @Override
            public void handle(final KeyEvent event) {
                if (myGame.getGameStatusProperty().get().equals(GameStatus.Paused)) {
                    actionTaken = false;
                }
            }
        };

        getMyStage().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        getMyStage().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    /**
     * Adds eventhandlers for closing the screen and logging out
     *
     * @param stage
     */
    public void addCloseEvent(Stage stage) {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                quitClick(null);
            }
        });
    }

    /**
     * Adds listeners to the slider and checkbox used to govern custom
     * difficulty
     */
    private void addDifficultyListeners() {
        // Whenever custom difficulty value is changed, checkbox is unchecked
        this.sldCustomDifficulty.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (cbxCustomDifficulty.isSelected()) {
                    cbxCustomDifficulty.setSelected(false);
                }
            }
        });

        // Whenever custom difficulty checkbox is checked, value is saved
        this.cbxCustomDifficulty.selectedProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    if (cbxCustomDifficulty.isSelected()) {
                        myGame.adjustDifficulty((float) customSpeed.get());
                    } else {
                        myGame.adjustDifficulty();
                    }
                } catch (RemoteException ex) {
                    System.out.println("RemoteException on adjustDifficulty changeEventHandler: " + ex.getMessage());
                    Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Displays end of game stats whenever game window is closed.
     */
    private void displayPostGameStats() {
        // Remove this println after implementation
        System.out.println("Display post game stats");
        // open new stage, and chuck all info in here, including whether game was ended before time
        String message = "";
        message += "Round: " + this.getMyGame().getRoundNoProperty().getValue() + "\n";
        try
        {
            message += this.getWinnerText() + "\n";
            
            message += "P1 " + this.getMyGame().getPlayer1Name() + ": " + this.getMyGame().getPlayer1Score().getValue() + "\n";
            message += "P2 " + this.getMyGame().getPlayer2Name() + ": " + this.getMyGame().getPlayer2Score().getValue() + "\n";
            message += "P3 " + this.getMyGame().getPlayer3Name() + ": " + this.getMyGame().getPlayer3Score().getValue() + "\n";
        }
        catch (RemoteException ex)
        {
            System.out.println("RemoteException (setting winning player text): " + ex.getMessage());
            message += "Failed to retreive player info.";
        }        
        
        Dialog d = new Dialog(this.getMyStage(), "Statistics", message);
        d.show();
    }

    private void closeMyStage(){
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if(getMyStage() != null){
                    getMyStage().close();
                }
            }
        });
    }

    private String getWinnerText() throws RemoteException
    {
        String playerWinString = "Player ";
        
        int winningScore = 0;
        int p1Score = this.getMyGame().getPlayer1Score().getValue();
        int p2Score = this.getMyGame().getPlayer2Score().getValue();
        int p3Score = this.getMyGame().getPlayer3Score().getValue();
        
        if (p1Score > p2Score)
        {
            if (p1Score > p3Score)
            {
                playerWinString +=  this.getMyGame().getPlayer1Name();
                winningScore = p1Score;
            }
            else
            {
                playerWinString += this.getMyGame().getPlayer3Name();
                winningScore = p3Score;
            }
        }
        else
        {
            if (p2Score > p3Score)
            {
                playerWinString += this.getMyGame().getPlayer2Name();
                winningScore = p2Score;
            }
            else
            {
               playerWinString += this.getMyGame().getPlayer3Name();
               winningScore = p3Score;
            }
        }
        
        playerWinString += " won with score " + winningScore;
        return playerWinString;
    }
}
