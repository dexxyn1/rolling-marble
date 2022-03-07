import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.awt.*;

/**
 * this class add the isSelected functionality for rectangle selection
 */
public class Rectangle extends javafx.scene.shape.Rectangle {
    boolean isSelected;
    private Paint toColor = Color.RED;
    private Circle circleTopLeft;
    private Circle circleBottomLeft;
    private Circle circleTopRight;
    private Circle circleBottomRight;
    private static final int CIRCLE_RADIUS = 5;
    private double rotateSave;
    public boolean wasDragged;

    Rectangle(Circle circleTopLeft, Circle circleBottomLeft, Circle circleTopRight, Circle circleBottomRight, double x, double y, double r) {
        this.setX(x);
        this.setY(y);
        this.setRotate(r);
        this.circleTopLeft = circleTopLeft;
        this.circleTopRight = circleTopRight;
        this.circleBottomLeft = circleBottomLeft;
        this.circleBottomRight = circleBottomRight;
    }

    public Rectangle getCopy() {
        return new Rectangle(circleTopLeft, circleBottomLeft, circleTopRight, circleBottomRight, getX(), getY(), getRotate());
    }

    enum CircleEditor {
        TOP_LEFT,
        BOTTOM_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT
    }

    class Circle extends javafx.scene.shape.Circle {
        double xOffset;
        double yOffset;
        CircleEditor circleEditor;
        Circle(double x, double y, double r, double xOffset, double yOffset, CircleEditor circleEditor) {
            super(x, y, r);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.circleEditor = circleEditor;
        }

        public void setX(double centerX) {
            setCenterX(centerX + xOffset);
        }
        public void setY(double centerY) {
            setCenterY(centerY + yOffset);
        }
        public void setLocation(double centerX, double centerY) {
            setX(centerX);
            setY(centerY);

        }
    }

    Rectangle(double x, double y, double w, double h, Pane canvas) {
        super(x, y, w, h);
        isSelected = false;
        circleTopLeft = createCircle(x, y, w, h, CircleEditor.TOP_LEFT);
        circleBottomLeft = createCircle(x, y, w, h, CircleEditor.BOTTOM_LEFT);
        circleTopRight = createCircle(x, y, w, h, CircleEditor.TOP_RIGHT);
        circleBottomRight = createCircle(x, y, w, h, CircleEditor.BOTTOM_RIGHT);
        circleBottomRight.setOpacity(0);
        circleBottomLeft.setOpacity(0);
        circleTopRight.setOpacity(0);
        circleTopLeft.setOpacity(0);
        circleTopLeft.setOnMouseDragged(e-> {
            if (Controller.editorModeOn)
                if (isSelected) {
                circleTopLeft.setLocation(e.getX() - circleTopLeft.xOffset, e.getY() - circleTopLeft.yOffset);
                circleBottomLeft.setX(e.getX() - circleTopLeft.xOffset);
                circleTopRight.setY(e.getY() - circleTopLeft.yOffset);
                updateRectangleDueToCircleLocations();
            }

        });

        circleBottomLeft.setOnMouseDragged(e-> {
            if (Controller.editorModeOn)
                if (isSelected) {
                circleBottomLeft.setLocation(e.getX() - circleBottomLeft.xOffset, e.getY() - circleBottomLeft.yOffset);
                circleTopLeft.setX(e.getX() - circleBottomLeft.xOffset);
                circleBottomRight.setY(e.getY() - circleBottomLeft.yOffset);
                updateRectangleDueToCircleLocations();
            }
        });

        circleTopRight.setOnMouseDragged(e-> {
            if (Controller.editorModeOn)
                if (isSelected) {
                circleTopRight.setLocation(e.getX() - circleTopRight.xOffset, e.getY() - circleTopRight.yOffset);
                circleBottomRight.setX(e.getX() - circleTopRight.xOffset);
                circleTopLeft.setY(e.getY() - circleTopRight.yOffset);
                updateRectangleDueToCircleLocations();
            }

        });

        circleBottomRight.setOnMouseDragged(e-> {
            if (Controller.editorModeOn)
                if (isSelected) {
                circleBottomRight.setLocation(e.getX() - circleBottomRight.xOffset, e.getY() - circleBottomRight.yOffset);
                circleTopRight.setX(e.getX() - circleBottomRight.xOffset);
                circleBottomLeft.setY(e.getY() - circleBottomRight.yOffset);
                updateRectangleDueToCircleLocations();
            }
        });

        canvas.getChildren().addAll(circleTopLeft, circleBottomRight, circleBottomLeft, circleTopRight);

        setOnMouseClicked(e-> {
            if (Controller.editorModeOn) {
                if (wasDragged) {
                    wasDragged = false;
                    return;
                }
                if (e.getButton() == MouseButton.PRIMARY) {
                    showUnshowCircles();
                } else if (e.getButton() == MouseButton.SECONDARY && !isSelected) {
                    if (getRotate() + 5 < 89)
                        setRotate(getRotate() + 5);
                } else if (e.getButton() == MouseButton.MIDDLE && !isSelected) {
                    if (getRotate() - 5 > -89)
                        setRotate(getRotate() - 5);
                }
            }
        });

        setOnMouseDragged(e-> {
            if (Controller.editorModeOn) {
                if (isSelected) {
                    setX(e.getX() - getWidth() / 2);
                    setY(e.getY() - getHeight() / 2);
                    setAllCircleCoordinates();
                    wasDragged = true;
                }
            }
        });

        setFill(new ImagePattern(new Image("Resources/blocks.png")));
    }

    public void showUnshowCircles() {
        isSelected = !isSelected;
        Paint oldColor = this.getFill();
        setFill(toColor);
        toColor = oldColor;
        if (isSelected) {
            rotateSave = getRotate();
            setRotate(0);
            circleBottomRight.setOpacity(1);
            circleBottomLeft.setOpacity(1);
            circleTopRight.setOpacity(1);
            circleTopLeft.setOpacity(1);
            circleBottomRight.toFront();
            circleBottomLeft.toFront();
            circleTopRight.toFront();
            circleTopLeft.toFront();
        } else {
            setRotate(rotateSave);
            circleBottomRight.setOpacity(0);
            circleBottomLeft.setOpacity(0);
            circleTopRight.setOpacity(0);
            circleTopLeft.setOpacity(0);
        }
    }

    private void updateRectangleDueToCircleLocations() {
        double x = circleTopLeft.getCenterX();
        double y = circleTopLeft.getCenterY();
        double h = Math.abs(circleTopRight.getCenterY() - circleBottomRight.getCenterY());
        double w = Math.abs(circleBottomLeft.getCenterX() - circleBottomRight.getCenterX());
        setX(x);
        setY(y);
        setWidth(w);
        setHeight(h);
    }

    private Circle createCircle(double x, double y, double w, double h, CircleEditor circleEditor) {
        Circle circle;
        switch (circleEditor) {
            case TOP_LEFT:
                circle = new Circle(x, y, CIRCLE_RADIUS, 0, 0, circleEditor);
                break;
            case BOTTOM_LEFT:
                circle = new Circle(x, y+h, CIRCLE_RADIUS, 0, h, circleEditor);
                break;
            case TOP_RIGHT:
                circle = new Circle(x+w, y, CIRCLE_RADIUS, w, 0, circleEditor);
                break;
            case BOTTOM_RIGHT:
                circle = new Circle(x+w, y+h, CIRCLE_RADIUS,w, h, circleEditor);
                break;
            default:
                throw new RuntimeException();
        }
        circle.setFill(Color.DARKMAGENTA);
        return circle;
    }
    public void setAllCircleCoordinates() {
        double h = getHeight();
        double w = getWidth();
        circleTopRight.xOffset = w;
        circleBottomLeft.yOffset = h;
        circleBottomRight.xOffset = w;
        circleBottomRight.yOffset = h;

        circleTopLeft.setLocation(getX(), getY());
        circleBottomLeft.setLocation(getX(), getY());
        circleTopRight.setLocation(getX(), getY());
        circleBottomRight.setLocation(getX(), getY());
    }

    public void setAllCircleCoordinates(double x, double y) {
        circleTopLeft.setLocation(x, y);
        circleBottomLeft.setLocation(x, y);
        circleTopRight.setLocation(x, y);
        circleBottomRight.setLocation(x, y);
    }

}