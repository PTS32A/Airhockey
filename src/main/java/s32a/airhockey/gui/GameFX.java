/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import com.badlogic.gdx.math.Vector2;
import com.sun.prism.paint.Color;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import s32a.airhockey.*;
import timers.GameTimer;

/**
 * Notes: - Puck and bots are not moving. Bot code has been updated to more
 * efficient usage. -- bots are now properly initialised as bots whenever an
 * activePerson with
 *
 * @author Luke
 */
public class GameFX extends AirhockeyGUI implements Initializable
{

    @FXML
    Label lblName;
    @FXML
    Label lblDifficulty;
    @FXML
    Label lblScoreP1;
    @FXML
    Label lblScoreP2;
    @FXML
    Label lblScoreP3;
    @FXML
    Label lblRound;
    @FXML
    Label lblTime;
    @FXML
    Button btnStart;
    @FXML
    Button btnPause;
    @FXML
    Button btnQuit;
    @FXML
    Button btnStopSpec;
    @FXML
    ListView lvChatbox;
    @FXML
    TextField tfChatbox;
    @FXML
    Canvas canvas;

    private DoubleProperty width;
    private DoubleProperty height;
    private GraphicsContext graphics;
    private boolean gameEnded = false;
    private boolean gameStart = false;
    private int sec = 0;
    private int min = 0;
    private @Getter
    boolean actionTaken = true;
    private GameTimer gameTimer;
    private double top;

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
        Game myGame;
        Player me = null;
        if (Lobby.getSingle().getCurrentPerson() instanceof Player)
        {
            myGame = Lobby.getSingle().getPlayedGame();
            me = (Player) Lobby.getSingle().getCurrentPerson();
            lblName.setText(Lobby.getSingle().getCurrentPerson().getName());
            lblTime.setText("00:00");
            btnStopSpec.setVisible(false);
            lblRound.setText("1");
        } else
        {
            //Spectator
            myGame = Lobby.getSingle().getSpectatedGames().get(Lobby.getSingle()
                    .getSpectatedGames().size() - 1);
            lblName.setText(myGame.getMyPlayers().get(0).getName());
            lblTime.setVisible(false);
            btnStart.setVisible(false);
            btnPause.setVisible(false);
            btnQuit.setVisible(false);
        }
        lblScoreP1.setText("20");
        lblScoreP2.setText("20");
        lblScoreP3.setText("20");
        lblDifficulty.setText(Float.toString(myGame.getMyPuck().getSpeed()));
        this.width.bind(this.canvas.widthProperty());
        this.height.bind(Bindings.subtract(this.canvas.heightProperty(), 1));
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());

        ChangeListener<Number> sizeChanged = new ChangeListener<Number>()
        {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                // REDRAW SHIT - maybe add listener to height 
            }
        };

        this.width.addListener(sizeChanged);
        //this.height.addListener(sizeChanged);

        this.lvChatbox.setItems(myGame.getMyChatbox().chatProperty());

        drawEdges();
    }

    /**
     * If game is not paused then updates all player scores and round number.
     * Currently this is updated every 20ms along with draw().
     */
    public void updateScore()
    {
        if (!Lobby.getSingle().getPlayedGame().isPaused())
        {
            List<Player> p = Lobby.getSingle().getPlayedGame().getMyPlayers();
            int score1 = p.get(0).getScore();
            int score2 = p.get(1).getScore();
            int score3 = p.get(2).getScore();
            lblScoreP1.setText(String.valueOf(score1));
            lblScoreP2.setText(String.valueOf(score2));
            lblScoreP3.setText(String.valueOf(score3));
            lblRound.setText(Integer.toString(Lobby.getSingle().getPlayedGame().getRoundNo()));
        }
    }

    /**
     * If game is not paused then time label is updated every second. Game
     * chatbox is also updated here.
     */
    public void updateTime()
    {
        if (!Lobby.getSingle().getPlayedGame().isPaused())
        {
            sec++;
            if (sec > 59)
            {
                sec = 0;
                min++;
            }
            String second = Integer.toString(sec);
            if (sec < 10)
            {
                second = "0" + Integer.toString(sec);
            }
            String minute = Integer.toString(min);
            if (min < 10)
            {
                minute = "0" + Integer.toString(min);
            }
            
            lblTime.setText(minute + ":" + second);
        }
//        lvChatbox.setItems(FXCollections.observableArrayList(Lobby.getSingle()
//                .getPlayedGame().getMyChatbox().getChat()));
    }

    /**
     * Draw is called every 20ms for a frame rate of 50 frames per second.
     * Redraws all bats and the puck in their correct position.
     */
    public void draw()
    {
        if (!Lobby.getSingle().getPlayedGame().isPaused())
        {
            double bat = (double) width.doubleValue() / 100 * 8;
            graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());
            drawEdges();
            Game g = Lobby.getSingle().getPlayedGame();
            g.getMyPlayers().get(0).draw(graphics, width.doubleValue(), height.doubleValue());
            graphics.fillOval(g.getMyPlayers().get(1).getBatPos().x, g.getMyPlayers().get(1).getBatPos().y, bat, bat);
            graphics.fillOval(g.getMyPlayers().get(2).getBatPos().x, g.getMyPlayers().get(2).getBatPos().y, bat, bat);
            g.getMyPuck().draw(graphics, width.doubleValue(), height.doubleValue());
        }
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
        double bY = top = height.doubleValue() - (width.doubleValue() * Math.sin(Math.toRadians(60)));
        top += height.doubleValue();
        // Right corner of triangle
        double cX = width.doubleValue();
        double cY = height.doubleValue();

        // Bottom goal
        Vector2 aXY1 = new Vector2((float) (aX + ((cX - aX) / 100 * 30)), (float) (aY + ((cY - aY) / 100 * 30)) - 1);
        Vector2 aXY2 = new Vector2((float) (aX + ((cX - aX) / 100 * 70)), (float) (aY + ((cY - aY) / 100 * 70)) - 1);
        // Left goal
        Vector2 bXY1 = new Vector2((float) (aX + ((bX - aX) / 100 * 30)) + 1, (float) (aY + ((bY - aY) / 100 * 30)));
        Vector2 bXY2 = new Vector2((float) (aX + ((bX - aX) / 100 * 70)) + 1, (float) (aY + ((bY - aY) / 100 * 70)));
        // Right goal
        Vector2 cXY1 = new Vector2((float) (cX + ((bX - cX) / 100 * 30)) - 1, (float) (cY + ((bY - cY) / 100 * 30)));
        Vector2 cXY2 = new Vector2((float) (cX + ((bX - cX) / 100 * 70)) - 1, (float) (cY + ((bY - cY) / 100 * 70)));

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
            Player p = (Player) Lobby.getSingle().getCurrentPerson();
            p.draw(graphics, width.doubleValue(), height.doubleValue());
            Lobby.getSingle().getPlayedGame().getMyPuck().draw(graphics, width.doubleValue(), height.doubleValue());
            double bat = (double) width.doubleValue() / 100 * 8;
            Vector2 bat1 = new Vector2((float) (aX + ((bX - aX) / 100 * 50)) + 3,
                    (float) ((aY + ((bY - aY) / 100 * 50)) - bat / 2));
            Vector2 bat2 = new Vector2((float) (cX + ((bX - cX) / 100 * 50))
                    - (float) bat - 3, (float) ((cY + ((bY - cY) / 100 * 50)) - bat / 2));

            Game g = Lobby.getSingle().getPlayedGame();

            // bot 10 and 11 were added in lobby.populate, and are currently not busy
            Person bot = Lobby.getSingle().getActivePersons().get("bot10");
            Lobby.getSingle().joinGame(g, bot);
            bot = Lobby.getSingle().getActivePersons().get("bot11");
            Lobby.getSingle().joinGame(g, bot);

            g.getMyPlayers().get(1).setBatPos(bat2);
            g.getMyPlayers().get(2).setBatPos(bat1);
            graphics.fillOval(g.getMyPlayers().get(1).getBatPos().x, g.getMyPlayers().get(1).getBatPos().y, bat, bat);
            graphics.fillOval(g.getMyPlayers().get(2).getBatPos().x, g.getMyPlayers().get(2).getBatPos().y, bat, bat);
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
        if (Lobby.getSingle().getPlayedGame().getMyPlayers().size() == 3)
        {
            if (Lobby.getSingle().getPlayedGame().beginGame())
            {
                addKeyEvents();
                gameTimer = new GameTimer(this);
                gameTimer.start();
                btnStart.setDisable(true);
            }
            else
            {
                super.showDialog("Error", "Failed to begin game");
            }

        } else
        {
            super.showDialog("Warning", "Not enough players to begin game.");
        }
    }

    /**
     * Pauses and unpauses game
     *
     * @param evt
     */
    public void pauseClick(Event evt)
    {
        Lobby.getSingle().getPlayedGame().pauseGame(
                !Lobby.getSingle().getPlayedGame().isPaused());
        actionTaken = true;
    }

    /**
     * Calls lobby.endGame(). Currently causing some errors.
     *
     * @param evt
     */
    public void quitClick(Event evt)
    {
        if (gameTimer != null)
        {
            gameTimer.stop();
        }
        if(Lobby.getSingle().getPlayedGame().statusProperty().getValue().equals("Game Over"))
        {
            Lobby.getSingle().endGame(Lobby.getSingle().getPlayedGame(), null);
        }
        else
        {
            Lobby.getSingle().endGame(Lobby.getSingle().getPlayedGame(),
                (Player) Lobby.getSingle().getCurrentPerson());
        }       
        getThisStage().close();
    }

    /**
     * Send message in chatbox
     */
    public void sendMessage(Event evt)
    {
        Lobby.getSingle().getPlayedGame().addChatMessage(tfChatbox.getText(),
                Lobby.getSingle().getCurrentPerson());
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
        Game game = Lobby.getSingle().getPlayedGame();
        Person person = Lobby.getSingle().getCurrentPerson();
        Lobby.getSingle().stopSpectating(game, person);
        getThisStage().close();
    }

    private void addKeyEvents()
    {
        //Moving left or right
        Player me = (Player) Lobby.getSingle().getCurrentPerson();
        final EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(final KeyEvent keyEvent)
            {
                if (keyEvent.getCode() == KeyCode.A
                        || keyEvent.getCode() == KeyCode.LEFT)
                {
                    if (!Lobby.getSingle().getPlayedGame().isPaused())
                    {
                        me.moveBat(-1);
                        actionTaken = true;
                    }

                } else if (keyEvent.getCode() == KeyCode.D
                        || keyEvent.getCode() == KeyCode.RIGHT)
                {
                    if (!Lobby.getSingle().getPlayedGame().isPaused())
                    {
                        me.moveBat(1);
                        actionTaken = true;
                    }
                }
            }
        };
        //Stop moving
        final EventHandler<KeyEvent> keyReleased = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(final KeyEvent keyEvent)
            {
                if (!Lobby.getSingle().getPlayedGame().isPaused())
                {
                    actionTaken = false;
                }
            }
        };

        getThisStage().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        getThisStage().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);

        this.tfChatbox.setOnKeyPressed(new EventHandler<KeyEvent>()
        {

            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode() == KeyCode.ENTER)
                {
                    sendMessage(null);
                }
            }
        });
    }

    private Stage getThisStage()
    {
        return (Stage) lblName.getScene().getWindow();
    }

}
