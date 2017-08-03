package com.yxr.guidelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
/**
 * Created by 63062 on 2017/8/2.
 */

public class GuideBackground extends View {
    private static final String TAG = "TAG";
    private Guide.GuideParams params;
    private Paint paint;
    private boolean isNeedInit = true;
    private int mCurrIndex = 0;
    private GuideListener guideListener;

    public GuideBackground(Context context) {
        super(context);
        init();
    }

    /**初始化*/
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(15 * getContext().getResources().getDisplayMetrics().density);
    }

    /**
     * 设置Guide参数
     * @param params
     */
    public void setGuideParams(Guide.GuideParams params){
        this.params = params;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(params.width == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : params.width,
                params.height == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : params.height);
        setLayoutParams(lp);
        mCurrIndex = 0;
    }

    /**初始化所有targetView的位置信息，在onDraw初始的原因是控件都已经测量完毕*/
    private void initRect() {
        if (!isNeedInit)
            return;
        isNeedInit = false;
        if (params.views != null && params.views.size() > 0){
            for (Guide.ViewParams viewParams : params.views){
                if (viewParams == null || viewParams.targetView == null)
                    continue;
                int[] locations = new int[2];
                viewParams.targetView.getLocationOnScreen(locations);
                int width = viewParams.targetView.getMeasuredWidth();
                int height = viewParams.targetView.getMeasuredHeight();
                viewParams.rect = new Rect(locations[0],locations[1],locations[0] + width,locations[1] + height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (params == null)
            return;
        initRect();

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        //绘制背景
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        paint.setColor(params.backgroundColor);
        canvas.drawRect(0, 0, canvasWidth, canvasHeight, paint);

        if (params.views == null || params.views.size() <= 0){
            canvas.restoreToCount(layerId);
            return;
        }

        if (mCurrIndex >= params.views.size()){
            canvas.restoreToCount(layerId);
            return;
        }

        Guide.ViewParams p = this.params.views.get(mCurrIndex);
        if (params.oneByOne){
            //只绘制当前引导
            drawGuide(p,canvas);
        }else{
            //绘制所有引导
            for (Guide.ViewParams pm : params.views)
                drawGuide(pm,canvas);
        }

        canvas.restoreToCount(layerId);

        drawText(p,canvas,canvasWidth,canvasHeight);
    }

    /**
     * 位置会根据targetView位置自动调整
     * 没有设置guideRes是默认绘制提示文字
     * @param p ： 当前引导图参数
     */
    private void drawText(Guide.ViewParams p, Canvas canvas, int canvasWidth, int canvasHeight) {
        if (p == null || p.rect == null || canvas == null || p.guideRes != 0)
            return;
        String des = p.des == null ? "this is des" : p.des;
        int x = p.rect.left > canvasWidth - p.rect.right ? (int) (p.rect.left - des.length() * paint.getTextSize()) : (int) (p.rect.right - des.length() * paint.getTextSize() / 2);
        int y = p.rect.top > canvasHeight - p.rect.bottom ? (int) (p.rect.top - paint.getTextSize() * 2)  : (int) (p.rect.bottom + paint.getTextSize() * 2);
        paint.setColor(Color.WHITE);
        canvas.drawText(des,x,y,paint);
    }

    /**
     * 绘制引导图层和挖洞
     * @param p ： 当前引导参数
     */
    private void drawGuide(Guide.ViewParams p, Canvas canvas) {
        if (p == null)
            return;
        if (p.state == Guide.State.CIRCLE)
            drawCircle(canvas,p.rect);
        else if (p.state == Guide.State.OVAL)
            drawOval(canvas,p.rect);
        else
            drawRect(canvas,p.rect);

        if (p.guideRes == 0)
            return;
        //绘制引导图片，虽然会根据targetView位置自动调整
        //如果targetView左边空间较大引导图绘制在targetView左边，否则相反
        //如果targetView上边空间较大引导图绘制在targetView上边，否则反之
        //提供了offX和offY供用户调整引导图位置
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), p.guideRes);
        if (bitmap == null)
            return;
        int x = p.rect.left > canvas.getWidth() - p.rect.right ?
                 p.rect.left - bitmap.getWidth() + (p.rect.right - p.rect.left) / 2 + p.offX:
                 p.rect.right - (p.rect.right - p.rect.left) / 2 + p.offX;

        int y = p.rect.top > canvas.getHeight() - p.rect.bottom ?
                p.rect.top - bitmap.getHeight() + p.offY :
                p.rect.bottom + p.offY;

        canvas.drawBitmap(bitmap,x,y,paint);
        bitmap.recycle();
        bitmap = null;
    }

    /**
     * 挖椭圆洞
     * @param rect ： targetView位置信息
     */
    private void drawOval(Canvas canvas, Rect rect) {
        if (rect == null || canvas == null)
            return;
        //挖洞
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //算出targetView外接椭圆的短轴和长轴
        int len1 = (int) (((rect.right - rect.left) * Math.sqrt(2) - (rect.right - rect.left)) / 2);
        int len2 = (int) (((rect.bottom - rect.top) * Math.sqrt(2) - (rect.bottom - rect.top)) / 2);

        canvas.drawOval(new RectF(rect.left - len1
                ,rect.top - len2
                ,rect.right + len1
                ,rect.bottom + len2),paint);
        paint.setXfermode(null);
    }

    /**
     * 挖圆形洞
     * @param rect ： targetView位置信息
     */
    private void drawCircle(Canvas canvas, Rect rect) {
        if (rect == null || canvas == null)
            return;
        //挖洞
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //绘制圆形，算出外接圆的半径
        canvas.drawCircle((rect.left + rect.right) / 2
                , (rect.top + rect.bottom) / 2
                , (int) Math.sqrt((rect.right - rect.left) * (rect.right - rect.left) + (rect.bottom - rect.top) * (rect.bottom - rect.top)) / 2
                , paint);
        paint.setXfermode(null);
    }

    /**
     * 挖矩形洞
     * @param rect ： targetView位置信息
     */
    private void drawRect(Canvas canvas, Rect rect) {
        if (rect == null || canvas == null)
            return;
        //挖洞
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(rect,paint);
        paint.setXfermode(null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        isNeedInit = true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e(TAG, "dispatchTouchEvent: " + (MotionEvent.ACTION_DOWN == ev.getAction()) );
        if (params == null){
            if (MotionEvent.ACTION_DOWN == ev.getAction())
                onNext();
            return super.dispatchTouchEvent(ev);
        }
        //如果除了targetView也可以触发事件，点击任意区域都将进入下一个引导
        if (params.outsideTouchable){
            if (MotionEvent.ACTION_DOWN == ev.getAction())
                onNext();
            return super.dispatchTouchEvent(ev);
        }
        boolean touchable = false;
        //如果是oneByOne只有点击当前targetView才可以触发事件，进入下一个引导
        if (params.oneByOne){
            if (params.views != null && params.views.size() > 0 && mCurrIndex < params.views.size()){
                Guide.ViewParams p = this.params.views.get(mCurrIndex);
                if (p != null && p.rect != null)
                    if (ev.getX() > p.rect.left && ev.getX() < p.rect.right
                            && ev.getY() > p.rect.top && ev.getY() < p.rect.bottom){
                        touchable = true;
                    }
            }
        }else{
            //如果不是oneByOne点击所有targetView都可以触发事件，进入下一个引导
            if (params.views != null && params.views.size() > 0){
                for (Guide.ViewParams p : params.views){
                    if (p == null || p.rect == null)
                        continue;
                    if (ev.getX() > p.rect.left && ev.getX() < p.rect.right
                            && ev.getY() > p.rect.top && ev.getY() < p.rect.bottom){
                        touchable = true;
                        break;
                    }
                }
            }
        }

        if (MotionEvent.ACTION_DOWN == ev.getAction() && touchable)
            onNext();

        return touchable ? touchable : super.dispatchTouchEvent(ev);
    }

    /**触发事件，下一步引导或者结束引导*/
    private void onNext() {
        if (params == null || !params.oneByOne || params.views == null){
            if (guideListener != null)
                guideListener.onFinish();
        }else if (mCurrIndex < params.views.size() - 1){
            mCurrIndex++;
            invalidate();
            if (guideListener != null)
                guideListener.onNext(mCurrIndex);
        }else {
            if (guideListener != null)
                guideListener.onFinish();
        }
    }

    /**设置监听*/
    public void setGuideListener(GuideListener guideListener){
        this.guideListener = guideListener;
    }

    public interface GuideListener{
        void onNext(int index);
        void onFinish();
    }
}
