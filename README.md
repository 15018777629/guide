# guide
    Android 最简单最好用的遮罩引导库；
    该库使用方便，定制灵活，可以设置根据需求定制引导层； 
    引导层需要引导的控件挖洞类型，目前支持三种类型，分别为：矩形（默认），圆形，椭圆（控件宽高一致时显示效果为圆形）；
    支持挖洞的方式支持单控件和多控件； 
    支持引导的方式也有两种，一种是所有控件同时挖洞显示引导，一种是按照加入的控件顺序一步一步引导；  
    支持引导监听，可监听下一步引导，和引导结束事件；
    支持自定义引导图，会根据引导控件位置自动计算引导图大致位置，计算规则如下：
    如果targetView左边空间较大引导图绘制在targetView左边，否则相反；
    如果targetView上边空间较大引导图绘制在targetView上边，否则反之，当然基于灵活也提供了offX和offY供调整引导图位置；     
# 实现最简单的一个引导
```java
Guide.ViewParams viewParams = new Guide.ViewParams(findViewById(R.id.ivBack));
new Guide.Builder(this)
         .guideSingelView(viewParams)            //设置单个引导
         .build()
         .show();
```
