class Vector extends java.util.Vector {
    double x;
    double y;

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector (Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    void add(Vector vector) {
        x = x + vector.x;
        y = y + vector.y;
    }
    void sub(Vector vector) {
        x = x - vector.x;
        y = y - vector.y;
    }
    void mul(double n) {
        x = x * n;
        y = y * n;
    }
    void mul(Vector vector) {
        x = x * vector.x;
        y = y * vector.y;
    }
    void div(double n) {
        x = x / n;
        y = y / n;
    }
    double mag() {
        return Math.sqrt(x*x + y*y);
    }
    void normalize() {
        double m = mag();
        if (m != 0) {
            div(m);
        }
    }
    void limit(double max) {
        if (mag() > max) {
            normalize();
            mul(max);
        }

    }
    static Vector add(Vector v1, Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }
    static Vector mul(Vector v1, double n) {
        return new Vector(v1.x * n, v1.y * n);
    }

    static Vector mul(Vector v1, Vector v2) {
        return new Vector(v1.x * v2.x, v1.y * v2.y);
    }

    static Vector div(Vector v1, double n) {
        return new Vector(v1.x / n, v1.y / n);
    }

    static Vector sub(Vector v1,Vector v2) {
        return new Vector(v1.x - v2.x, v1.y - v2.y);
    }

    public Vector get() {
        return new Vector(x, y);
    }

    @Override
    public synchronized String toString() {
        return String.format("(%f, %f)", x, y);
    }
}