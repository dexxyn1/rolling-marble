import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private double width;
    private double height ;
    private boolean play;
    private Mover mover;
    public Pane canvas;
    public Button playButton;
    public Button resetButton;
    public Button addBallMetal;
    public Button addBallPlastic;
    public Button addRectangle;
    public Button addMagnet;
    public Button editorModeButton;
    public HBox topBox;
    static boolean editorModeOn;
    public MenuItem changeBackground;
    public VBox rightPane;
    public VBox root;

    private void changeMenuBackground() {
        FileChooser fileChooser = new FileChooser();
        File returned = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (returned!= null) {
            System.out.println(returned.toURI().toString());
           changeBackground(canvas, returned.toURI().toString());
        }
    }

    private void changeBackground(Pane pane, String string) {
        Image image = new Image(string);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER,  new BackgroundSize(1.0, 1.0, true, true, false, false));
        pane.setBackground(new Background(backgroundImage));
    }

    private void showUnshowTopBox() {
        ObservableList<Node> nodes = topBox.getChildren();
        for (Node node : nodes) {
            if (editorModeOn)
                node.setOpacity(1);
            else
                node.setOpacity(0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changeBackground(canvas, "Resources/background2.jpg");
        changeBackground(topBox, "Resources/topBackground.png");
        changeBackground(rightPane, "Resources/rightBackground.png");
        width = canvas.getPrefWidth();
        height = canvas.getPrefHeight();
        play = false;
        editorModeOn = false;
        showUnshowTopBox();

        changeBackground.setOnAction(e-> changeMenuBackground());

        editorModeButton.setOnMouseClicked(e-> {
            if (editorModeOn) {
                editorModeOn = false;
                showUnshowTopBox();
                mover.switchEditorModeToOff();
            } else {
                editorModeOn = true;
                showUnshowTopBox();
            }
        });

        //listener for buttons
        playButton.setOnMouseClicked(e-> {
            play = !play;
            if (!play) {
                playButton.setText("PLAY");

            } else {
                playButton.setText("PAUSE");
            }
        });
        resetButton.setOnMouseClicked(e-> {
            if (!play) {
                mover.removeAll();
            }
        });

        addRectangle.setOnMouseClicked(e->mover.addRectangle());

        addMagnet.setOnMouseClicked(e->mover.addMagnet(mover.createMagnet(width/2, height/2, 40)));
        addBallMetal.setOnMouseClicked(e->{
                    Circle circle = new Circle(width/2, height/2, 7, Color.RED);
                    circle.setFill(MaterialType.METAL);
                    MovingObject ball = Mover.createMovingObject(circle, 5, -1, MaterialType.METAL);
                    mover.addMovingObject(ball);
                }
        );
        addBallPlastic.setOnMouseClicked(e->{
                    Circle circle = new Circle(width/2, height/2, 10, Color.RED);
                    circle.setFill(MaterialType.PLASTIC);
                    MovingObject ball = Mover.createMovingObject(circle, 1, -1, MaterialType.PLASTIC);
                    mover.addMovingObject(ball);
                }
        );


        setup();

        Rectangle rectangle2 = new Rectangle(140, 100, 200, 20, canvas);
        rectangle2.setRotate(50);


        Rectangle rectangle3 = new Rectangle(350, 100, 300, 20, canvas);
        rectangle3.setRotate(-10);
        Rectangle rectangle4 = new Rectangle(0, 500, width, 20, canvas);
        Rectangle rectangle5 = new Rectangle(140, 200, 300, 20, canvas);
        rectangle5.setRotate(10);
        mover.addRectangle(rectangle5);

        Circle circle = new Circle(width-270, 0, 10, Color.RED);
        MovingObject ball2 = Mover.createMovingObject(circle, 1, -1, MaterialType.PLASTIC);
        circle.setFill(MaterialType.PLASTIC);
        mover.addMovingObject(ball2);
        mover.addRectangle(rectangle2);
        mover.addRectangle(rectangle3);
        mover.addRectangle(rectangle4);

        Mover.Magnet magnet = mover.createMagnet(600, 250, 80);

        mover.addMagnet(magnet);

        KeyFrame keyFrame = new KeyFrame(new Duration(15), actionEvent -> {
            if (play && !editorModeOn) {

                for (MovingObject movingObject : mover.getMovingObjects()) {
                    mover.gravity(.1, movingObject);
                    mover.checkCollisionWithRectangles(movingObject);
                    if (movingObject.materialType == MaterialType.MAGNET) {
                        mover.horizontalFriction(1, magnet);
                    }
                    mover.friction(0.001);
                }
                mover.checkBallCollision();
                mover.checkMagnets();
                mover.update();
                mover.checkEdges();
                mover.display();
            }
        }
        );

        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(-1);
        timeline.play(); //start playing
    }
    private void setup() {
        canvas.getChildren().clear();
        Circle circle = new Circle(100, 0, 7, Color.RED);
        circle.setFill(MaterialType.METAL);
        MovingObject ball = Mover.createMovingObject(circle, 1, -1, MaterialType.METAL);
        mover = new Mover(width, height,  canvas);
        mover.addMovingObject(ball);
    }
}
