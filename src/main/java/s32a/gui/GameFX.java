/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.gui;

import com.badlogic.gdx.math.Vector2;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lombok.Getter;
import s32a.airhockey.*;
import s32a.timers.GameTimer;

/**
 * NOTES: - SetBatPosition in DrawEdges should probably be moved to game
 *
 * @author Luke, Bob
 */
public class GameFX extends AirhockeyGUI implements Initializable
{

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

    private DoubleProperty width, height;
    private IntegerProperty customSpeed;
    private GraphicsContext graphics;
    private boolean gameStart = false;
    @Getter
    private boolean actionTaken = true;
    private GameTimer gameTimer;
    private Game myGame;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
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
    public void setUp()
    {
        Player me = null;
        Lobby lobby = Lobby.getSingle();
        if (lobby.getCurrentPerson() instanceof Player)
        {
            // Player
            myGame = lobby.getPlayedGame();
            me = (Player) lobby.getCurrentPerson();
            btnStopSpec.setVisible(false);
            lblName.setText(me.getName());
            // bind custom difficulty indicators
            this.customSpeed = new SimpleIntegerProperty(15);
            this.customSpeed.bindBidirectional(this.sldCustomDifficulty.valueProperty());
            this.cbxCustomDifficulty.textProperty().bind(
                    Bindings.concat("Use custom Speed: ", customSpeed.asString()));

            // bot 10 and 11 were added in lobby.populate, and are currently not busy
            Person bot = lobby.getActivePersons().get("bot10");
            lobby.joinGame(myGame, bot);
            bot = lobby.getActivePersons().get("bot11");
            lobby.joinGame(myGame, bot);
            
            // adds listeners governing custom difficulty
            this.addDifficultyListeners();

        } else if (lobby.getCurrentPerson() instanceof Spectator)
        {
            // Spectator
            myGame = lobby.getSpectatedGames().get(Lobby.getSingle()
                    .getSpectatedGames().size() - 1);
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
        this.lblRound.textProperty().bind(myGame.getRoundNo().asString());

        // Chatbox
        this.lvChatbox.setItems(myGame.getMyChatbox().chatProperty());

        // Difficulty 
        this.lblDifficulty.textProperty().bind(myGame.getMyPuck().getSpeed().asString());

        // binds width / height for redrawing to canvas size
        this.width.bind(this.canvas.widthProperty());
        this.height.bind(Bindings.subtract(this.canvas.heightProperty(), 1));

        // initialises graphics object
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());

        // adds chatbox accept event
        this.tfChatbox.setOnKeyPressed((KeyEvent ke) ->
        {
            if (ke.getCode() == KeyCode.ENTER)
            {
                sendMessage(null);
            }
        });

        // draws the canvas
        this.drawEdges();

        /**
         * if currentPerson is spectator, graphics can start now. If he were a
         * player, they start when startGame is called.
         */
        if (lobby.getCurrentPerson() instanceof Spectator)
        {
            this.startGraphics(myGame);
        }
    }

    /**
     * Draw is called every 20ms for a frame rate of 50 frames per second.
     * Redraws all bats and the puck in their correct position.
     *
     * @param g
     */
    public void draw(Game g)
    {
        if (!g.isPaused())
        {
            double bat = (double) width.doubleValue() / 100 * 8;
            this.graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());
            this.drawEdges();
            this.graphics.fillOval(width.doubleValue()/2 - myGame.getMyPlayers().get(0).getBatPos().x - bat/2,
                    height.doubleValue() - myGame.getMyPlayers().get(0).getBatPos().y - bat, bat, bat);
            this.graphics.fillOval(myGame.getMyPlayers().get(1).getBatPos().x,
                    height.doubleValue() - myGame.getMyPlayers().get(1).getBatPos().y, bat, bat);
            this.graphics.fillOval(myGame.getMyPlayers().get(2).getBatPos().x,
                    height.doubleValue() - myGame.getMyPlayers().get(2).getBatPos().y, bat, bat);
            this.drawPuck(g.getMyPuck().getPuckLocation());
        }
    }

    /**
     * Draws puck on given canvas
     *
     * @param location
     */
    private void drawPuck(float[] location)
    {
        float positionX = location[0];
        float positionY = location[1];
        float puckSize = location[2];

        int radius = (int) (puckSize / 2);
        int x = (int) positionX + (int) canvas.getWidth() / 2 - radius;
        int y = (int) canvas.getHeight() - (int) (positionY + puckSize / 2) - radius;
        graphics.fillOval(x, y, (int) puckSize, (int) puckSize);
    }

    /**
     * Draws all edges and goals. Is redrawn along with draw()
     */
    public void drawEdges()
    {
        // Left corner of triangle
        double aX = 0;
        double aY = height.doubleValue();
        // Top corner of triangle
        double bX = width.doubleValue() / 2;
        double bY = height.doubleValue() - (width.doubleValue()
                * Math.sin(Math.toRadians(60)));
        // Right corner of triangle
        double cX = width.doubleValue();
        double cY = height.doubleValue();

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

        graphics.strokeLine(aX, aY, bX, bY);
        graphics.strokeLine(bX, bY, cX, cY);
        graphics.strokeLine(cX, cY, aX, aY);
        graphics.setLineWidth(2);
        graphics.setStroke(Paint.valueOf("RED"));
        graphics.strokeLine(aXY1.x, aXY1.y, aXY2.x, aXY2.y);
        graphics.setStroke(Paint.valueOf("LIME"));
        graphics.strokeLine(bXY1.x, bXY1.y, bXY2.x, bXY2.y);
        graphics.setStroke(Paint.valueOf("BLUE"));
        graphics.strokeLine(cXY1.x, cXY1.y, cXY2.x, cXY2.y);
        graphics.setStroke(Paint.valueOf("BLACK"));

        //Draws and adds players for the first time
        if (!gameStart)
        {
            Lobby lobby = Lobby.getSingle();

            double bat = (double) width.doubleValue() / 100 * 8;
            Player p = myGame.getMyPlayers().get(0);
            this.graphics.fillOval(width.doubleValue()/2 - myGame.getMyPlayers().get(0).getBatPos().x - bat/2,
                    height.doubleValue() - myGame.getMyPlayers().get(0).getBatPos().y - bat, bat, bat);
            this.graphics.fillOval(myGame.getMyPlayers().get(1).getBatPos().x,
                    height.doubleValue() - myGame.getMyPlayers().get(1).getBatPos().y, bat, bat);
            this.graphics.fillOval(myGame.getMyPlayers().get(2).getBatPos().x,
                    height.doubleValue() - myGame.getMyPlayers().get(2).getBatPos().y, bat, bat);
            this.drawPuck(myGame.getMyPuck().getPuckLocation());
            gameStart = true;
        }
    }

    /**
     * Checks if there are enough players to start. Disables start button when
     * clicked and start is successful
     *
     * @param evt
     */
    public void startClick(Event evt)
    {
        if (myGame != null && myGame.getMyPlayers().size() == 3)
        {
            if (myGame.beginGame())
            {
                addEvents();
                btnStart.setDisable(true);
                this.sldCustomDifficulty.setDisable(true);
                this.cbxCustomDifficulty.setDisable(true);
                this.startGraphics(myGame);
            } else
            {
                super.showDialog("Error", "Failed to begin game");
            }

        } else
        {
            super.showDialog("Warning", "Not enough players to begin game.");
        }
    }

    /**
     * Starts display timer and binds score displays
     *
     * @param myGame
     */
    private void startGraphics(Game myGame)
    {
        // binds score labels to player scores
        this.lblScoreP1.textProperty().bind(myGame.getMyPlayers()
                .get(0).getScore().asString());
        this.lblScoreP2.textProperty().bind(myGame.getMyPlayers()
                .get(1).getScore().asString());
        this.lblScoreP3.textProperty().bind(myGame.getMyPlayers()
                .get(2).getScore().asString());

        gameTimer = new GameTimer(this, myGame);
        gameTimer.start();
    }

    /**
     * Selects or unselect custom difficulty
     *
     * @param evt
     */
    public void customDifficultySelect(Event evt)
    {
        if (cbxCustomDifficulty.isSelected())
        {
            myGame.adjustDifficulty((float) Math.round(sldCustomDifficulty.getValue()));
        } else
        {
            myGame.adjustDifficulty();
        }
    }

    /**
     * Pauses and unpauses game
     *
     * @param evt
     */
    public void pauseClick(Event evt)
    {
        myGame.pauseGame(!myGame.isPaused());
        actionTaken = true;
    }

    /**
     * Calls lobby.endGame(). Currently causing some errors.
     *
     * @param evt
     */
    public void quitClick(Event evt)
    {
        Lobby lobby = Lobby.getSingle();
        if (gameTimer != null)
        {
            gameTimer.stop();
        }
        if (lobby.getCurrentPerson() instanceof Spectator)
        {
            lobby.stopSpectating(myGame, lobby.getCurrentPerson());
        } 
        else if (myGame.isGameOver() || myGame.getRoundNo().get() == 0)
        {
            lobby.endGame(myGame, null);
        } 
        else
        {
            lobby.endGame(myGame,(Player) lobby.getCurrentPerson());
        }
        getThisStage().close();
    }

    /**
     * Send message in chatbox
     *
     * @param evt
     */
    public void sendMessage(Event evt)
    {
        Lobby lobby = Lobby.getSingle();
        Person currentPerson = lobby.getCurrentPerson();

        if (currentPerson instanceof Player)
        {
            myGame.addChatMessage(tfChatbox.getText(), currentPerson);
        } else if (currentPerson instanceof Spectator)
        {
            myGame.addChatMessage(tfChatbox.getText(), currentPerson);
        }
        tfChatbox.setText("");
    }

    /**
     * Closes window, removes spectator from game, and returns spectator to
     * Personhood.
     *
     * @param evt
     */
    public void stopSpectating(Event evt)
    {
        Person person = Lobby.getSingle().getCurrentPerson();
        Lobby.getSingle().stopSpectating(myGame, person);
        getThisStage().close();
    }

    private void addEvents()
    {
        //Moving left or right
        Player me = (Player) Lobby.getSingle().getCurrentPerson();
        final EventHandler<KeyEvent> keyPressed = (final KeyEvent keyEvent) ->
        {
            if (keyEvent.getCode() == KeyCode.A
                    || keyEvent.getCode() == KeyCode.LEFT)
            {
                if (!myGame.isPaused())
                {
                    me.moveBat(-1);
                    actionTaken = true;
                }
            } else if (keyEvent.getCode() == KeyCode.D
                    || keyEvent.getCode() == KeyCode.RIGHT)
            {
                if (!myGame.isPaused())
                {
                    me.moveBat(1);
                    actionTaken = true;
                }
            }
        };
        //Stop moving
        final EventHandler<KeyEvent> keyReleased = (final KeyEvent keyEvent) ->
        {
            if (myGame.isPaused())
            {
                actionTaken = false;
            }
        };

        getThisStage().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        getThisStage().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }

    /**
     * Adds listeners to the slider and checkbox used to govern custom difficulty
     */
    private void addDifficultyListeners()
    {
        // Whenever custom difficulty value is changed, checkbox is unchecked
        this.sldCustomDifficulty.valueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                {
                    if (this.cbxCustomDifficulty.isSelected())
                    {
                        this.cbxCustomDifficulty.setSelected(false);
                    }
                });

        // Whenever custom difficulty checkbox is checked, value is saved
        this.cbxCustomDifficulty.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                {
                    if (cbxCustomDifficulty.isSelected())
                    {
                        myGame.adjustDifficulty((float) customSpeed.get());
                    } else
                    {
                        myGame.adjustDifficulty();
                    }
                });
    }

    private Stage getThisStage()
    {
        return (Stage) lblName.getScene().getWindow();
    }

}
