package edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator;

public class Point3D {
    private float _x, _y, _z;

    public Point3D(float x, float y, float z) {
        _x = x;
        _y = y;
        _z = z;
    }

    public float getX() {
        return _x;
    }

    public float getY() {
        return _y;
    }

    public float getZ() {
        return _z;
    }

    public Vector3D minus(Point3D origin) {
        return new Vector3D(_x - origin._x, _y - origin._y, _z - origin._z);
    }

    @Override
    public String toString() {
        return "(" + _x + ", " + _y + ", " + _z + ")";
    }
}
