package com.yxr.guidelibrary;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 63062 on 2017/8/2.
 */

public class Guide implements GuideBackground.GuideListener {
    private static final String TAG = "TAG";

    private FrameLayout guide;
    private GuideBackground guideBackground;
    public List<View> guideChildren = new ArrayList<>();
    private GuideBackground.GuideListener guideListener;

    private Guide() {
    }

    /**
     * oneByOne才会调用此方法
     * 不要手动调用，如果需要监听此方法可以setGuideListener
     * @param index ： 第几个引导
     */
    @Override
    public void onNext(int index) {
        Log.e(TAG, "onNext:::::::: " + index);
        if(guideListener != null)
            guideListener.onNext(index);
    }

    /**引导结束会自动调用，不要手动调用，如果需要监听此方法可以setGuideListener*/
    @Override
    public void onFinish() {
        Log.e(TAG, "onFinish::::::::: ");
        dismiss();
        if(guideListener != null)
            guideListener.onFinish();
    }

    /**设置引导层监听*/
    public void setGuideListener(GuideBackground.GuideListener guideListener){
        this.guideListener = guideListener;
    }

    /**隐藏引导层*/
    public void dismiss(){
        if (guide == null || guideChildren == null || guideChildren.size() <= 0)
            return;
        try {
            for (View view : guideChildren)
                guide.removeView(view);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**展示引导层*/
    public void show(){
        if (guide == null || guideBackground == null)
            return;
        guide.addView(guideBackground);
    }


    /**引导层构造器*/
    public static class Builder {
        private GuideParams params = null;

        public Builder(Activity context){
            params = new GuideParams();
            params.context = context;
        }

        /**设置引导层背景颜色*/
        public Builder backgroundColor(int color){
            params.backgroundColor = color;
            return this;
        }

        /**设置引导层除targetView（需要挖洞的控件）外是否可以点击*/
        public Builder outsideTouchable(boolean outsideTouchable){
            params.outsideTouchable = outsideTouchable;
            return this;
        }

        /**设置引导步骤是否一个接一个*/
        public Builder oneByOne(boolean oneByOne){
            params.oneByOne = oneByOne;
            return this;
        }

        /**设置引导层控件，单个targetView和多个互斥，那个后设置用哪个*/
        public Builder guideSingelView(ViewParams view){
            params.views = new ArrayList<>();
            params.views.add(view);
            return this;
        }

        /**设置引导层控件，多个targetView的参数集合和单个互斥，那个后设置用哪个*/
        public Builder guideViews(List<ViewParams> views){
            params.views = views;
            return this;
        }

        /**引导层宽度*/
        public Builder width(int width){
            params.width = width;
            return this;
        }

        /**引导层高度*/
        public Builder height(int height){
            params.height = height;
            return this;
        }

        /**
         * 建造构造器
         * @return ： 返回引导层对象
         */
        public Guide build(){
            FrameLayout decorView = (FrameLayout) params.context.getWindow().getDecorView();
            Guide guide = new Guide();

            final GuideBackground background = new GuideBackground(params.context);
            background.setGuideParams(params);
            background.setGuideListener(guide);

            guide.guideBackground = background;
            guide.guide = decorView;
            guide.guideChildren.add(background);
            return guide;
        }
    }

    /**引导层总参数*/
    public static class GuideParams {
        /**引导层宽度，不设置默认为match_parent*/
        public int width;
        /**引导层高度，不设置默认为match_parent*/
        public int height;
        /**上下文，必须为activity*/
        public Activity context;
        /**是否按顺序一个引导接一个引导*/
        public boolean oneByOne;
        /**是否允许targetView外的事件*/
        public boolean outsideTouchable = true;
        /**引导层背景颜色*/
        public int backgroundColor = Color.parseColor("#AA000000");
        /**引导集合，oneByOne顺序按这个*/
        public List<ViewParams> views;
    }

    /**
     * 引导层细节参数
     *绘制引导图片，虽然会根据targetView位置自动调整
     *如果targetView左边空间较大引导图绘制在targetView左边，否则相反
     *如果targetView上边空间较大引导图绘制在targetView上边，否则反之
     *提供了offX和offY供调整引导图位置
     **/
    public static class ViewParams{
        /**蒙层以这个控件挖洞*/
        public View targetView;
        /**要显示的引导图资源文件*/
        public int guideRes;
        /**挖洞的模式RECT为矩形，CIRCLE为圆形，OVAL为椭圆，targetView长宽一致时为圆形，默认为RECT*/
        public State state;
        /**如果不设置guideRes则默认显示文字引导，des为文字引导的文字*/
        public String des;
        /**引导图在X轴上的偏移，虽然会自动根据targetView位置添加引导图，不能满足用户需求情况下提供这个属性进行引导图位置矫正*/
        public int offX;
        /**引导图在Y轴上的偏移，虽然会自动根据targetView位置添加引导图，不能满足用户需求情况下提供这个属性进行引导图位置矫正*/
        public int offY;
        /**targetView位置信息，手动改变可能引起数据不准确*/
        public Rect rect;
        public ViewParams(View targetView){
            this.targetView = targetView;
            this.state = State.RECT;
            this.rect = new Rect(0,0,0,0);
        }
    }

    public enum State {
        /**矩形*/
        RECT,
        /**圆形*/
        CIRCLE,
        /**椭圆*/
        OVAL,
    }
}
