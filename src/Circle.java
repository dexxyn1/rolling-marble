import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

public class Circle extends javafx.scene.shape.Circle {
    Circle(double x, double y, double r, Paint paint) {
        super(x, y, r, paint);
        setOnMouseDragged(e-> {
            if (Controller.editorModeOn) {
                setCenterX(e.getX());
                setCenterY(e.getY());
            }
        });
    }
    void setFill(MaterialType materialType) {
        if (materialType == MaterialType.METAL) {
            setFill(new ImagePattern(new Image("Resources/ball_metal.png")));
        } else if (materialType == MaterialType.PLASTIC) {
            setFill(new ImagePattern(new Image("Resources/ball_plastic.png")));
        }
    }
}
