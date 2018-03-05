package com.hosigus.coc_helper.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.hosigus.coc_helper.MyApplication;
import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.items.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 网上抄的View
 */
public class PolygonView extends View {
    private Paint eagePaint;
    private Paint areaPaint;
    private Paint textPaint;

    private int deadColor;
    private int poorColor;
    private int normalColor;
    private int godColor;

    private int width;
    private int height;
    private float maxRadius;
    private int eageCount;
    private int loopCount;
    private float angle;
    private List<Float> pointValue;
    private List<String> pointName;
    private List<Float> maxPointXList;
    private List<Float> maxPointYList;

    private static final String TAG = "PolygonView";

    public PolygonView(Context context) {
        super(context);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initPaint() {
        eagePaint = new Paint();
        areaPaint = new Paint();
        textPaint = new Paint();

        eagePaint.setStyle(Paint.Style.STROKE);
        eagePaint.setAntiAlias(true);

        areaPaint.setStyle(Paint.Style.FILL);
        areaPaint.setAntiAlias(true);

        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(35);
        textPaint.setAntiAlias(true);

        //固定文本
        pointName = new ArrayList<>();
        pointName.add("力量");
        pointName.add("敏捷");
//        pointName.add("体质");
//        pointName.add("外貌");
//        pointName.add("意志");
//        pointName.add("智力");
//        pointName.add("体型");
//        pointName.add("教育");
//        pointName.add("幸运");
        pointName.add("意志");
        pointName.add("体质");
        pointName.add("外貌");
        pointName.add("教育");
        pointName.add("体型");
        pointName.add("智力");
        pointName.add("幸运");

        //设置颜色
        deadColor= Color.parseColor("#191970");
        poorColor= Color.parseColor("#6495ED");
        normalColor= Color.parseColor("#FF7F50");
        godColor= Color.parseColor("#FF0000");
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PolygonView);
        initPaint();
        setTextColor(typedArray.getColor(R.styleable.PolygonView_textColor, Color.BLACK));
        setLoopCount(typedArray.getInteger(R.styleable.PolygonView_loopCount, 0));
        setEageCount(typedArray.getInteger(R.styleable.PolygonView_edgeCount, 0));
        setAreaColor(typedArray.getColor(R.styleable.PolygonView_areaColor, Color.rgb(63,175,245)));
        setEageColor(typedArray.getColor(R.styleable.PolygonView_edgeColor, Color.GRAY));
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        maxRadius = (float) ((width / 2) * 0.8);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!canDraw()) {
            return;
        }
        canvas.translate(width / 2, height / 2);
        computeMaxPoint();
        drawPolygon(canvas);
        drawLine(canvas);
        drawArea(canvas);
        drawText(canvas);
    }

    /*
        绘制文字
     */
    private int spTopx(int sp) {
        DisplayMetrics displayMetrics = MyApplication.getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP/*单位*/,
                sp/*值*/, displayMetrics);
    }
    private void drawText(Canvas canvas) {
        for (int i = 0; i < pointName.size(); i++) {
            float currentAngle = i * angle;
            int value= (int) (pointValue.get(i)*100);
            textPaint.setTextSize(spTopx(value/5+9));
            if (value>75){
                textPaint.setColor(godColor);
            }else if(value>50){
                textPaint.setColor(normalColor);
            }else if(value>25){
                textPaint.setColor(poorColor);
            }else {
                textPaint.setColor(deadColor);
            }
            float currentX = maxPointXList.get(i) * 1.1f;
            float currentY = maxPointYList.get(i) * 1.1f;
            if (currentAngle > 90 && currentAngle < 270) {
                canvas.drawText(pointName.get(i),
                        currentX - (textPaint.getTextSize() / 2) * (pointName.get(i).length()),
                        currentY + (textPaint.getTextSize() / 2) ,
                        textPaint);
            } else {
                canvas.drawText(pointName.get(i), currentX - (textPaint.getTextSize() / 2)
                        * (pointName.get(i).length()), currentY, textPaint);
            }
        }
    }

    /*
        绘制个方向值覆盖的区域
     */
    private void drawArea(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < eageCount; i++) {
            float rate = pointValue.get(i);
            float currentX = maxPointXList.get(i) * rate;
            float currentY = maxPointYList.get(i) * rate;
            if (i == 0) {
                path.moveTo(currentX, currentY);
            } else {
                path.lineTo(currentX, currentY);
            }
        }
        path.close();
        canvas.drawPath(path, areaPaint);
    }
    /*
        画出从中心向各顶点的连线
     */
    private void drawLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < eageCount; i++) {
            path.reset();
            path.lineTo(maxPointXList.get(i), maxPointYList.get(i));
            canvas.drawPath(path, eagePaint);
        }
    }

    /*
        绘制多边形和每一层
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < loopCount; i++) {
            path.reset();
            float rate = computeRate(i + 1, loopCount);
            for (int j = 0; j < eageCount; j++) {
                float currentX = maxPointXList.get(j) * rate;
                float currentY = maxPointYList.get(j) * rate;
                if (j == 0) {
                    path.moveTo(currentX, currentY);
                } else {
                    path.lineTo(currentX, currentY);
                }
            }
            path.close();
            canvas.drawPath(path, eagePaint);
        }
    }

    private float computeRate(float value, float max) {
        return value / max;
    }

    /*
        计算最大半径，之后的位置都是基于最大半径的比例
     */
    public void computeMaxPoint() {
        maxPointXList = new ArrayList<>();
        maxPointYList = new ArrayList<>();
        for (int i = 0; i < eageCount; i++) {

            float currentAngle = i * angle - 90;

            float currentX = (float) (maxRadius * Math.cos((currentAngle / 180) * Math.PI));
            float currentY = (float) (maxRadius * Math.sin((currentAngle / 180) * Math.PI));
            maxPointXList.add(currentX);
            maxPointYList.add(currentY);
        }
    }

    /*
        用属性动画绘制组件
     */
    public void draw() {
        if (canDraw()) {
            final Float[] trueValues = pointValue.toArray(new Float[pointValue.size()]);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000);
            valueAnimator.addUpdateListener(animation -> {
                float rate = animation.getAnimatedFraction();
                for (int i = 0; i < pointValue.size(); i++) {
                    pointValue.set(i, trueValues[i] * rate);
                }
                invalidate();
            });
            valueAnimator.start();
        }
    }

    /*
        判断是否可以绘制
        条件为
        loopCount(绘制层数)必须大于0
        eageCount(边数)必须大于3
        pointValue(各方向值)不能为null，且size不能小于边数
     */
    private boolean canDraw() {
        return !(loopCount <= 0 || eageCount <= 2 || pointValue == null
                || pointValue.size() < eageCount);
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
    }


    public void setAreaColor(int color) {
        areaPaint.setColor(color);
        areaPaint.setAlpha(150);
    }

    public void setEageColor(int color) {
        eagePaint.setColor(color);
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void setEageCount(int eageCount) {
        this.eageCount = eageCount;
        angle = 360 / eageCount;
    }

    public void setPointValue(int position,Float value){this.pointValue.set(position,value);}

    public void setPointValue(List<Float> pointValue) {
        this.pointValue = pointValue;
    }

    public void setPointName(List<String> pointName) {
        this.pointName = pointName;
    }

    public int getEageCount() {
        return eageCount;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public List<Float> getPointValue() {
        return pointValue;
    }

    public List<String> getPointName() {
        return pointName;
    }
}
