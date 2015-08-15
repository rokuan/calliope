package apps.rokuan.com.calliope_helper.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by LEBEAU Christophe on 10/08/15.
 */
public class SoundLevelView extends View {
    //private static final int MIN_VALUE = -5;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 35;

    //private int borderWidth = -1;
    private int levelBorderWidth = -1;
    private int levelColor = Color.rgb(0, 0x97, 0xA7);
    //private int borderColor = Color.argb(127, 0, 0, 0);
    private int value = MIN_VALUE;

    private Paint paint = new Paint();
    //private RectF meterBounds = new RectF();

    public SoundLevelView(Context context) {
        super(context);
    }

    public SoundLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoundLevelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*private void initView(){

    }*/

    public void setLevel(int l){
        value = l;
        this.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int minDimension = Math.min(this.getWidth(), this.getHeight());
        int centerX = this.getWidth()/2, centerY = this.getHeight()/2;
        int radius = Math.min(centerX, centerY);
        int distanceToBounds = radius;
        int startX, startY, stopX, stopY;
        double percentageLevel = (Math.min(MAX_VALUE, Math.max(MIN_VALUE, value)) - MIN_VALUE) * 1.0 / (MAX_VALUE - MIN_VALUE);

        if(levelBorderWidth == -1){
            levelBorderWidth = minDimension/4;
        }

        Path squarePath = new Path();

        squarePath.moveTo(centerX, centerY + radius);
        squarePath.lineTo(centerX - radius, centerY);
        squarePath.lineTo(centerX, centerY - radius);
        squarePath.lineTo(centerX + radius, centerY);

        canvas.clipPath(squarePath);

        paint.setStrokeWidth(levelBorderWidth);
        paint.setStyle(Paint.Style.STROKE);

        for(int i=0; i<4; i++){
            int xDistanceFactor = (i % 2 == 1) ? (i - 2) : 0;
            int yDistanceFactor = (i % 2 == 0) ? (1 - i) : 0;
            int xDirection = (i == 1 || i == 2) ? 1 : -1;
            int yDirection = (i == 2 || i == 3) ? 1 : -1;
            double levelFactor = (percentageLevel - i * 0.25) * 4;

            paint.setColor(Color.rgb(0xCC, 0xCC, 0xCC));

            startX = centerX + distanceToBounds * xDistanceFactor;
            startY = centerY + distanceToBounds * yDistanceFactor;
            stopX = startX + distanceToBounds * xDirection;
            stopY = startY + distanceToBounds * yDirection;

            canvas.drawLine(startX, startY, stopX, stopY, paint);

            if(percentageLevel > i * 0.25) {
                paint.setColor(levelColor);

                stopX = (int) (startX + distanceToBounds * xDirection * levelFactor);
                stopY = (int) (startY + distanceToBounds * yDirection * levelFactor);

                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
    }
}
