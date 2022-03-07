import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class MovingObject {
    Vector location;
    Vector velocity;
    Vector acceleration;
    Shape shape;
    double mass;
    double topSpeed;
    private static int idCounter = 0;
    private double id;
    MaterialType materialType;

    MovingObject(Vector location, Vector velocity, Vector acceleration, Shape shape, double mass, double topSpeed, MaterialType materialType) {
        this.location = location;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.shape = shape;
        this.mass = mass;
        this.topSpeed = topSpeed;
        this.materialType = materialType;
        id = idCounter;
        idCounter++;
    }
    MovingObject(MovingObject movingObject) {
        this.materialType = movingObject.materialType;
        this.id = movingObject.id;
        this.topSpeed = movingObject.topSpeed;
        this.mass = movingObject.mass;
        this.shape = movingObject.shape;
        this.location = new Vector(movingObject.location);
        this.velocity = new Vector(movingObject.velocity);
        this.acceleration = new Vector(movingObject.acceleration);
    }



    double getShapeSize() {
        if (shape instanceof Circle)
            return ((Circle) shape).getRadius()*2;
        else if (shape instanceof Rectangle)
            return ((Rectangle) shape).getWidth();
        else
            return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MovingObject) {
            return ((MovingObject) obj).id == id;
        }
        return false;
    }

    @Override
    public String toString() {
        return id + "";
    }
}