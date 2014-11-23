/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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

/**
 *
 * @author frankpeeters
 */
public class Dialog extends Stage {

    public Dialog(Stage owner, String header, String message) {
        super();
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(header);

        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 300, 250, Color.LIGHTSKYBLUE);
        setScene(scene);

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
        setRowSpan(taMessage, 4);
        gridPane.add(taMessage, 0, 0);

        Button btClose = new Button("Close");
        btClose.setOnAction((ActionEvent event) -> {
            close();
        });

        BorderPane buttonRegion = new BorderPane();
        buttonRegion.setRight(btClose);
        gridPane.add(buttonRegion, 0, 4);
    }
}
