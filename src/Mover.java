import com.sun.prism.Material;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;



public class Mover {
    private ArrayList<Rectangle> rectangles;
    private ArrayList<MovingObject> movingObjects;
    private double width;
    private double height;
    private Pane canvas;
    private boolean isStarted;
    Mover(double width, double height, Pane canvas) {
        rectangles = new ArrayList<>();
        movingObjects = new ArrayList<>();
        this.width = width;
        this.height= height;
        this.canvas = canvas;
    }

    private Mover(ArrayList<Rectangle> rectangles, ArrayList<MovingObject> movingObjects, double width, double height, Pane canvas) {
        this.rectangles = new ArrayList<>();
        for (Rectangle rectangle : rectangles) {
            this.rectangles.add(rectangle.getCopy());
        }
        this.movingObjects = new ArrayList<>( );
        for (MovingObject movingObject : movingObjects) {
            this.movingObjects.add(new MovingObject(movingObject));
        }
        this.width = width;
        this.height = height;
        this.canvas = canvas;

    }

    public void removeAll() {
        movingObjects = new ArrayList<>();
        rectangles = new ArrayList<>();
        canvas.getChildren().clear();
    }

    public ArrayList<MovingObject> getMovingObjects() {
        return movingObjects;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setNotStarted(boolean notStarted) {
        this.isStarted = notStarted;
    }

    public synchronized Mover getCopy() {
        return new Mover(rectangles, movingObjects, width, height, canvas);
    }

    void switchEditorModeToOff() {
        for (Rectangle rectangle: rectangles) {
            if (rectangle.isSelected) {
                rectangle.showUnshowCircles();
            }
        }

    }



    void addMovingObject(MovingObject movingObject) {
        movingObjects.add(movingObject);
        canvas.getChildren().add(movingObject.shape);
    }

    static MovingObject createMovingObject(javafx.scene.shape.Shape shape, double mass, double topSpeed, MaterialType materialType) {
        Vector location = new Vector(shape.getLayoutX(), shape.getLayoutY());
        Vector velocity = new Vector(0,0);
        Vector acceleration = new Vector(0,0);
        return new MovingObject(location, velocity, acceleration, shape, mass, topSpeed, materialType);
    }

    /**
     * updates the location
     */
    void update() {
        for (MovingObject movingObject : movingObjects) {
            movingObject.velocity = Vector.add(movingObject.velocity, movingObject.acceleration);
            if (movingObject.topSpeed != -1)
                movingObject.velocity.limit(movingObject.topSpeed);
            movingObject.location = Vector.add(movingObject.location, movingObject.velocity);
            if (movingObject instanceof Magnet)
                if (((Magnet) movingObject).shape instanceof Rectangle) {
                    ((Rectangle) movingObject.shape).setAllCircleCoordinates(
                            movingObject.location.x + movingObject.shape.getBoundsInLocal().getCenterX() - (((Rectangle) movingObject.shape).widthProperty().getValue() / 2),
                            movingObject.location.y + movingObject.shape.getBoundsInLocal().getCenterY() - (((Rectangle) movingObject.shape).heightProperty().getValue() / 2)
                    );
                }
            movingObject.acceleration.mul(0);
        }
    }

    /**
     * Adds the acceleration to the given force
     */
    void applyForce(Vector force, MovingObject movingObject) {
        Vector f = Vector.div(force, movingObject.mass);
        movingObject.acceleration = Vector.add(movingObject.acceleration, f);
    }

    void applyForceToAll(Vector force) {
        for (MovingObject movingObject : movingObjects) {
            Vector f = Vector.div(force, movingObject.mass);
            movingObject.acceleration = Vector.add(movingObject.acceleration, f);
        }
    }

    /**
     * updates the location of the shape in the GUI
     */
    void display() {
        for (MovingObject movingObject : movingObjects) {
            movingObject.shape.setLayoutX(movingObject.location.x);
            movingObject.shape.setLayoutY(movingObject.location.y);

            if (movingObject instanceof Magnet) {
                ((Magnet) movingObject).circle.setLayoutX(movingObject.location.x);
                ((Magnet) movingObject).circle.setLayoutY(movingObject.location.y);

            }
        }
    }

    void checkMagnets() {
        for (int i =0; i < movingObjects.size(); i++) {
            for (int j = i+1; j < movingObjects.size(); j++) {
                MovingObject movingObject1 = movingObjects.get(i);
                MovingObject movingObject2 = movingObjects.get(j);
                if (
                        movingObject1.materialType == MaterialType.METAL && movingObject2.materialType == MaterialType.MAGNET ||
                                movingObject1.materialType == MaterialType.MAGNET && movingObject2.materialType == MaterialType.MAGNET ||
                                movingObject1.materialType == MaterialType.MAGNET && movingObject2.materialType == MaterialType.METAL
                ) {

                    if (movingObject1 instanceof Magnet) {
                        attract(movingObject2, movingObject1);
                    } else if (movingObject2 instanceof  Magnet) {
                        attract(movingObject1, movingObject2);
                    }


                }
            }
        }
    }

    private void attract(MovingObject movingObject1, MovingObject movingObject2) {
        Shape intersect = Shape.intersect(((Magnet) movingObject2).circle, movingObject1.shape);
        if (intersect.getBoundsInLocal().getWidth() != -1) {
            Vector dir = Vector.sub(getLocationOfMovingObject(movingObject2), getLocationOfMovingObject(movingObject1));
            //System.out.printf("%s - %s = %s%n", location2, location1, dir);
            //System.out.println(dir.mag());
            //dir.mag() <= movingObject1.getShapeSize()/2 + movingObject2.getShapeSize()/2
            Shape intersect2 = Shape.intersect(((Magnet) movingObject2).shape, movingObject1.shape);
            if (intersect2.getBoundsInLocal().getWidth() >= 0) {
                movingObject1.velocity.x = 0;
                movingObject1.velocity.y = 0;
                movingObject2.velocity.x = 0;
                movingObject2.velocity.y = 0;
            } else {
                dir.normalize();
                dir.mul(0.1);
                //System.out.println(dir);
                applyForce(dir, movingObject1);
            }
        }
    }

    private Vector getLocationOfMovingObject(MovingObject movingObject) {
        return new Vector(movingObject.shape.getBoundsInLocal().getCenterX()+movingObject.location.x,
                movingObject.shape.getBoundsInLocal().getCenterY()+movingObject.location.y);
    }


    void checkBallCollision() {
        for (int i =0; i < movingObjects.size(); i++) {
            for (int j = i+1; j < movingObjects.size(); j++) {
                MovingObject movingObject1 = movingObjects.get(i);
                MovingObject movingObject2 = movingObjects.get(j);
                javafx.scene.shape.Shape intersect = Shape.intersect(movingObjects.get(i).shape, movingObjects.get(j).shape);
                if (movingObject1.materialType == MaterialType.MAGNET && movingObject2.materialType == MaterialType.METAL ||
                        movingObject2.materialType == MaterialType.MAGNET && movingObject1.materialType == MaterialType.METAL
                ) {
                    continue;
                }
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    Vector shape1Dir = Vector.sub(getLocationOfMovingObject(movingObject2),getLocationOfMovingObject(movingObject1));
                    //Vector shape1Dir = Vector.sub(movingObject1.location,movingObject2.location);
                    shape1Dir.normalize();
                    shape1Dir.mul(movingObject1.mass);
                    applyForce(shape1Dir, movingObject2);
                    //Vector shape2Dir = Vector.sub(movingObject2.location,movingObject1.location);
                    Vector shape2Dir = Vector.sub(getLocationOfMovingObject(movingObject1),getLocationOfMovingObject(movingObject2));
                    shape2Dir.normalize();
                    shape2Dir.mul(movingObject1.mass);
                    applyForce(shape2Dir, movingObject1);
                }
            }
        }
    }

    /**
     * Checks for collisions with rectangles
     */
    void checkCollisionAll() {
        for (MovingObject movingObject : movingObjects) {
            checkCollisionWithRectangles(movingObject);
        }
    }
    void checkCollisionWithRectangles(MovingObject movingObject) {
            for (Rectangle rectangle : rectangles) {
                javafx.scene.shape.Shape intersect = Shape.intersect(movingObject.shape, rectangle);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    Vector opposingForce = new Vector(Math.cos(Math.toRadians(270 + rectangle.getRotate())), Math.sin(Math.toRadians(270 + rectangle.getRotate())));
                    Vector velocity = movingObject.velocity.get();
                    if (intersect.getBoundsInLocal().getWidth() > 0) {
                        if (movingObject instanceof Magnet) {
                            movingObject.location.y -= intersect.getBoundsInLocal().getHeight();
                        } else {
                            if (intersect.getBoundsInLocal().getHeight() < intersect.getBoundsInLocal().getWidth())
                                movingObject.location.y -= intersect.getBoundsInLocal().getHeight();
                        }
                    }
                    if (rectangle.getRotate()!=0) {
                        opposingForce.normalize();
                        opposingForce.mul(velocity.mag());
                        //movingObject.velocity.add(opposingForce);
                        opposingForce.y *= .7;
                        applyForce(opposingForce, movingObject);
                        movingObject.velocity.y = 0;
                        //movingObject.topSpeed = 4;
                    }
                    else {
                        velocity.mul(-1);
                        Vector yVelocity = velocity.get();
                        yVelocity.y = yVelocity.y *.5;
                        yVelocity.x *= -1;
                        opposingForce.mul(Math.abs(rectangle.getRotate()));

                        applyForce(yVelocity, movingObject);
                        applyForce(velocity, movingObject);
                        applyForce(opposingForce, movingObject);

                    }
                }
            }
    }


    /**
     * Adds a rectangle to the lists of rectangles to check for collisions
     */
    void addRectangle(Rectangle rectangle) {
        rectangles.add(rectangle);
        canvas.getChildren().add(rectangle);
    }


    /**
     * Adds a rectangle
     */
    void addRectangle() {
        addRectangle(new Rectangle(width/2, height/2, 20, 20, canvas));
    }

    /**
     * Checks if the ball is going over an edge. Reverse the velocity if that happens
     */
    void checkEdges() {
        for (MovingObject movingObject : movingObjects) {
            if (movingObject.shape.getBoundsInLocal().getCenterX()+movingObject.location.x > width) {
                movingObject.location.x -= 5;
                movingObject.velocity.x *= -1;
            }

            /*if (movingObject.location.x > width) {
                movingObject.location.x = width;
                movingObject.velocity.x *= -.8;
                System.out.println("Collision2a");
            } else if (movingObject.shape.getLayoutX() < 0) {
                movingObject.velocity.x *= -.8;
                movingObject.location.x = 0;
                System.out.println("Collision2b");
            }
            if (movingObject.location.y > height) {
                movingObject.velocity.y *= -.8;
                movingObject.location.y = height;
                System.out.println("Collision2c");
            }*/
        }

    }

    void addMagnet(Magnet magnet) {
        addMovingObject(magnet);
        canvas.getChildren().addAll(magnet.circle);
        magnet.circle.toBack();
        magnet.shape.toFront();
    }

    Magnet createMagnet(double x, double y, int r) {
        Rectangle rectangle = new Rectangle(x, y, 14, 7, canvas);
        return new Magnet(rectangle, 0.5, r);
    }

    /**
     * Apply gravity force to the mover
     */
    void gravity(double c, MovingObject movingObject) {
        double m = c * movingObject.mass;
        Vector gravity = new Vector(0, m);
        applyForce(gravity, movingObject);
    }

    /**
     * Apply gravity force to the mover
     */
    void gravityToAll(double c) {
        for (MovingObject movingObject : movingObjects) {
            gravity(c, movingObject);
        }
    }
    /**
     * Apply friction force to the mover
     */
    void friction(double c) {
        for (MovingObject movingObject : movingObjects) {
            friction(c, movingObject);
        }
    }
    /**
     * Apply friction force a the mover
     */
    void friction(double c, MovingObject movingObject) {
        Vector frictionForce = movingObject.velocity.get();
        frictionForce.mul(-1);
        frictionForce.normalize();
        frictionForce.mul(c);
        applyForce(frictionForce, movingObject);
    }

    void horizontalFriction(double c, MovingObject movingObject) {
        Vector frictionForce = movingObject.velocity.get();
        frictionForce.mul(-1);
        frictionForce.normalize();
        frictionForce.mul(c);
        frictionForce.y = 0;
        applyForce(frictionForce, movingObject);
    }


    static class Magnet extends MovingObject{
        Circle circle;
        double r;
        double magnetSize;
        Rectangle rectangle;

        private Magnet(Rectangle rectangle, Circle circle, double r, double magnetSize,double mass, Vector location, Vector velocity, Vector acceleration) {
            super(location, velocity,acceleration, rectangle, mass, -1, MaterialType.MAGNET);
            this.rectangle = rectangle;
            this.circle = circle;
            this.r = r;
            this.magnetSize = magnetSize;
        }

        public Magnet(Rectangle rectangle, double mass, double r) {
            this(new Vector(0, 0), new Vector(0,0),new Vector(0,0), rectangle, mass);
            magnetSize = rectangle.getWidth();
            this.r = r;
            circle = new Circle(rectangle.getX()+rectangle.getWidth()/2, rectangle.getY()+rectangle.getHeight()/2, r);
            circle.setOpacity(0.2);

            shape.setOnMouseDragged(e-> {
                if (Controller.editorModeOn) {
                circle.setCenterX(shape.getBoundsInLocal().getCenterX());
                circle.setCenterY(shape.getBoundsInLocal().getCenterY());
                if (shape instanceof Rectangle) {
                    if (((Rectangle) shape).isSelected) {
                        ((Rectangle) shape).setX(e.getX() - ((Rectangle) shape).getWidth() / 2);
                        ((Rectangle) shape).setY(e.getY() - ((Rectangle) shape).getHeight() / 2);
                        ((Rectangle) shape).setAllCircleCoordinates();
                        ((Rectangle) shape).wasDragged = true;
                    }
                }

                }
            });
            this.rectangle = rectangle;

        }
        public Magnet getCopy() {
            return new Magnet(rectangle.getCopy(),
                    new Circle(rectangle.getX()+rectangle.getWidth()/2, rectangle.getY()+rectangle.getHeight()/2, r),
                    r, magnetSize, mass, super.location, super.velocity, super.acceleration
                    );
        }

        private Magnet(Vector location, Vector velocity, Vector acceleration, javafx.scene.shape.Shape shape, double mass) {
            super(location, velocity, acceleration, shape, mass*10, -1, MaterialType.MAGNET);
        }

    }
}