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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import s32a.Client.ClientData.GameClient;
import s32a.Client.timers.GameTimer;
import s32a.Shared.*;
import s32a.Shared.enums.Colors;

/**
 * NOTES: - SetBatPosition in DrawEdges should probably be moved to game
 *
 * @author Luke, Bob
 */
public class GameFX extends AirhockeyGUI implements Initializable {

    @FXML
    Label lblName, lblDifficulty, lblScoreP1,
            lblScoreP2, lblScoreP3, lblRound, lblTime;
    @FXML
    Button btnStart, btnPause, btnQuit, btnStopSpec;
    @FXML
    ListView lvChatbox;
    @FXML
    TextField tfChatbox;
    @FXML
    Canvas canvas;
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
    private GraphicsContext graphics;
    private boolean gameStart = false;
    @Getter
    private boolean actionTaken = true;
    private GameTimer gameTimer;
    @Getter
    private GameClient myGame;

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
        IPerson myPerson = lobby.getMyPerson(me);
        if (myPerson instanceof IPlayer) {
            // Player
            IPlayer myPlayer = (IPlayer) myPerson;
            btnStopSpec.setVisible(false);
            btnPause.setDisable(true);
            lblName.setText(myPlayer.getName());
            double bX = width.get() / 2;
            double bY = height.get() - bX * Math.tan(Math.toRadians(30));
            if (myPlayer.getColor() == Colors.Blue) {
                this.apGame.getTransforms().add(new Rotate(-120, bX, bY, 0, Rotate.Z_AXIS));
            }
            else if (myPlayer.getColor() == Colors.Green) {
                this.apGame.getTransforms().add(new Rotate(120, bX, bY, 0, Rotate.Z_AXIS));
            }
            
            // bind custom difficulty indicators
            this.customSpeed = new SimpleIntegerProperty(15);
            this.customSpeed.bindBidirectional(this.sldCustomDifficulty.valueProperty());
            this.cbxCustomDifficulty.textProperty().bind(
                    Bindings.concat("Use custom Speed: ", customSpeed.asString()));

            // bot 10 and 11 were added in lobby.populate, and are currently not busy
            IPerson bot = lobby.getActivePersons().get("bot10");
            lobby.joinGame(myGame, bot);
            bot = lobby.getActivePersons().get("bot11");
            lobby.joinGame(myGame, bot);

            // adds listeners governing custom difficulty
            this.addDifficultyListeners();

        } else if (myPerson instanceof ISpectator) {
            // Spectator - TODO: add list of games to ISpectator, and retrieve from there.
            ISpectator mySpectator = (ISpectator) myPerson;
            lblName.setText(myGame.getMyPlayers().get(0).getName());
            btnStart.setVisible(false);
            btnPause.setVisible(false);
            btnQuit.setVisible(false);
            this.sldCustomDifficulty.setVisible(false);
            this.cbxCustomDifficulty.setVisible(false);
        }
        // binds upDateTime property
        this.lblTime.textProperty().bind(myGame.getGameTime());

        // round number
        this.lblRound.textProperty().bind(myGame.getRoundNoProperty().asString());

        // Chatbox
        this.lvChatbox.setItems(myGame.getChat());

        // Difficulty 
        this.lblDifficulty.textProperty().bind(myGame.getPuckSpeedProperty().asString());

        // binds width / height for redrawing to canvas size
        this.width.bind(this.apGame.prefWidthProperty());
        this.height.bind(Bindings.subtract(this.apGame.prefHeightProperty(), 1));

        // initialises graphics object
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());

        // adds chatbox accept event
        this.tfChatbox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    sendMessage(null);
                }
            }
        });

        // draws the canvas
        this.drawEdges();

        /**
         * if currentPerson is spectator, graphics can start now. If he were a
         * player, they start when startGame is called.
         */
        if (myPerson instanceof ISpectator) {
            this.startGraphics(myGame);
        }
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
    public void startClick(Event evt) {
        if (myGame != null && myGame.getMyPlayers().size() == 3) {
            if (myGame.beginGame()) {
                btnStart.setDisable(true);
                btnPause.setDisable(false);
                this.sldCustomDifficulty.setDisable(true);
                this.cbxCustomDifficulty.setDisable(true);
                this.startGraphics(myGame);
            } else {
                super.showDialog("Error", "Failed to begin game");
            }

        } else {
            super.showDialog("Warning", "Not enough players to begin game.");
        }
    }

    /**
     * Starts display timer and binds score displays
     *
     * @param myGame
     */
    private void startGraphics(GameClient myGame) {
        // binds score labels to player scores
        this.lblScoreP1.textProperty().bind(myGame.getPlayer1Score().asString());
        this.lblScoreP2.textProperty().bind(myGame.getPlayer2Score().asString());
        this.lblScoreP3.textProperty().bind(myGame.getPlayer3Score().asString());

        gameTimer = new GameTimer(this, myGame);
        gameTimer.start();
    }

    /**
     * Selects or unselect custom difficulty
     *
     * @param evt
     */
    public void customDifficultySelect(Event evt) {
        if (cbxCustomDifficulty.isSelected()) {
            myGame.adjustDifficulty((float) Math.round(sldCustomDifficulty.getValue()));
        } else {
            myGame.adjustDifficulty();
        }
    }

    /**
     * Pauses and unpauses game
     *
     * @param evt
     */
    public void pauseClick(Event evt) {
        myGame.pauseGame(!myGame.getGameStatusProperty().get().equals(GameStatus.Paused));
        actionTaken = true;
    }

    /**
     * Calls lobby.endGame(). Currently causing some errors.
     *
     * @param evt
     */
    public void quitClick(Event evt) {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        IPerson myPerson = lobby.getMyPerson(me);
        if (myPerson instanceof ISpectator) {
            lobby.stopSpectating(myGame, (ISpectator) myPerson);
        } else if (myGame.getGameStatusProperty().get().equals(GameStatus.GameOver) || myGame.getRoundNoProperty().get() == 0) {
            lobby.endGame(myGame, null);
        } else {
            lobby.endGame(myGame, (IPlayer) myPerson);
        }
        getThisStage().close();
    }

    /**
     * Send message in chatbox
     *
     * @param evt
     */
    public void sendMessage(Event evt) {
        IPerson currentPerson = lobby.getMyPerson(me);
        if (currentPerson != null) {
            myGame.addChatMessage(tfChatbox.getText(), currentPerson.getName());
            tfChatbox.setText("");
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
    public void stopSpectating(Event evt) {
        lobby.stopSpectating(myGame, lobby.getMyPerson(me));
        getThisStage().close();
    }

    public void addEvents() {
        //Moving left or right
        IPlayer myPlayer = (IPlayer) lobby.getMyPerson(me);
        final EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.A
                        || keyEvent.getCode() == KeyCode.LEFT) {
                    if (!myGame.getGameStatusProperty().get().equals(GameStatus.Paused)) {
                        myPlayer.moveBat(-1);
//                    System.out.println(me.getPosX().doubleValue());
//                    System.out.println(myGame.getMyPlayers().get(0).getPosX());
                        actionTaken = true;
                    }
                } else if (keyEvent.getCode() == KeyCode.D
                        || keyEvent.getCode() == KeyCode.RIGHT) {
                    if (!myGame.getGameStatusProperty().get().equals(GameStatus.Paused)) {
                        myPlayer.moveBat(1);
//                    System.out.println(me.getPosX().doubleValue());
//                    System.out.println(myGame.getMyPlayers().get(0).getPosX());
                        actionTaken = true;
                    }
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

        getThisStage().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        getThisStage().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    public void addCloseEvent(Stage stage) {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                System.out.println("closerequest handled");
                IPerson p = lobby.getMyPerson(me);
                if (p instanceof ISpectator) {
                    lobby.stopSpectating(myGame, p);
                } else if (p instanceof IPlayer) {
                    lobby.endGame(myGame, (IPlayer) p);
                }
                stage.close();
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
                if (cbxCustomDifficulty.isSelected()) {
                    myGame.adjustDifficulty((float) customSpeed.get());
                } else {
                    myGame.adjustDifficulty();
                }
            }
        });
    }

    private Stage getThisStage() {
        return (Stage) lblName.getScene().getWindow();
    }

    void setGame(IGame myGame) {
        try {
            this.myGame = new GameClient(myGame);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}