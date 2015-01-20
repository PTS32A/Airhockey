/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import static javafx.scene.layout.GridPane.setRowSpan;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author frankpeeters
 */
public class Dialog {

    private static Map<Long, Dialog> dialogs = new HashMap<>();

    public static void showDialog(String header, String message) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                Long id = System.currentTimeMillis();
                dialogs.put(id, new Dialog(id, header, message));
            }
        });
    }

    private Stage stage;
    private Long dialogID;

    /**
     * this application's equivalent of a mbox in C#
     *
     * @param owner
     * @param header
     * @param message
     */
    private Dialog(Long id, String header, String message) {
        this.dialogID = id;
        stage = new Stage();

        try
        {
            stage.getIcons().add(new Image("file:GamePNG.png"));
        }
        catch (Exception ex)
        {
            System.out.println("Exception in setting the stage icon: " + ex.getMessage());
        }
        
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 300, 250, Color.LIGHTSKYBLUE);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        root.getChildren().add(gridPane);
        setBottomAnchor(gridPane, 10.0);
        setTopAnchor(gridPane, 10.0);
        setRightAnchor(gridPane, 10.0);
        setLeftAnchor(gridPane, 10.0);

        TextArea taMessage = new TextArea();
        taMessage.setText(message);
        taMessage.setWrapText(true);
        taMessage.editableProperty().setValue(false);
        setRowSpan(taMessage, 4);
        gridPane.add(taMessage, 0, 0);

        Button btClose = new Button("Close");
        btClose.setDefaultButton(true);
        btClose.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        taMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    btClose.fire();
                }
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Dialog.dialogs.remove(dialogID);
            }
        });

        BorderPane buttonRegion = new BorderPane();
        buttonRegion.setRight(btClose);
        gridPane.add(buttonRegion, 0, 4);

        stage.setScene(scene);
        stage.setTitle(header);
        stage.show();
    }
}
