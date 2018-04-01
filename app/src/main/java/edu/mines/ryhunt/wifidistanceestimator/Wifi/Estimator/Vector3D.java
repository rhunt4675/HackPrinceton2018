package edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator;

public class Vector3D {
    private float _x, _y, _z;

    public Vector3D(float x, float y, float z) {
        _x = x;
        _y = y;
        _z = z;
    }

    public Vector3D plus(Vector3D vector) {
        return new Vector3D(_x + vector._x, _y + vector._y, _z + vector._z);
    }

    public Vector3D minus(Vector3D vector) {
        return new Vector3D(_x - vector._x, _y - vector._y, _z - vector._z);
    }

    public float dot(Vector3D vector) {
        return _x * vector._x + _y * vector._y + _z * vector._z;
    }

    public Vector3D cross(Vector3D vector) {
        return new Vector3D(_y * vector._z - _z * vector._y,
                _z * vector._x - _x * vector._z,
                _x * vector._y - _y * vector._x);
    }

    public float norm() {
        return (float) Math.sqrt(Math.pow(_x, 2)
                + Math.pow(_y, 2) + Math.pow(_z, 2));
    }

    public Vector3D scale(float scaleFactor) {
        return new Vector3D(_x * scaleFactor, _y * scaleFactor, _z * scaleFactor);
    }

    @Override
    public String toString() {
        return "(" + _x + ", " + _y + ", " + _z + ")";
    }
}
