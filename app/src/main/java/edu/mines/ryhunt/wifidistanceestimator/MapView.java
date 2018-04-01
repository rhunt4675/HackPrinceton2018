package edu.mines.ryhunt.wifidistanceestimator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.List;

import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.Point3D;

public class MapView extends AppCompatImageView {
    private Paint _paint = new Paint();
    private List<Point3D> _aps = null;
    private PointF _location = null;
    private Point _offset = null;
    private float _scale;

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setupMap(Point offset, float scale, List<Point3D> aps) {
        _offset = offset;
        _scale = scale;
        _aps = aps;
    }

    public void setUserLocation(PointF location) {
        _location = location == null ? _location : location;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point3D ap : _aps) {
            _paint.setColor(Color.BLUE);
            canvas.drawCircle(_offset.x + ap.getX() * _scale,
                    _offset.y - ap.getY() * _scale, 10, _paint);
        }

        if (_location != null) {
            _paint.setColor(Color.RED);
            canvas.drawCircle(_offset.x + _location.x * _scale,
                    _offset.y - _location.y * _scale, 25, _paint);
        }
    }
}
