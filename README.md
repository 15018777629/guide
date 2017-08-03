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
## 实现最简单的一个引导
```java
Guide.ViewParams viewParams = new Guide.ViewParams(findViewById(R.id.ivBack));
new Guide.Builder(this)
         .guideSingelView(viewParams)            //设置单个引导
         .build()
         .show();
```
#### 效果如下（忽略红色文字）
![Screenshot](https://github.com/15018777629/guide/blob/master/screens/screenshot1.jpg)
## 现在来实现一个圆形洞，带有引导图的引导层
```java
float density = getResources().getDisplayMetrics().density;
Guide.ViewParams viewParams = new Guide.ViewParams(findViewById(R.id.ivBack));
// 设置挖洞类型为圆形
viewParams.state = Guide.State.CIRCLE;
// 设置引导图资源
viewParams.guideRes = R.drawable.fangqibianji_layey;
// 因为这张引导图左边有一些透明边界，显示位置不太好，所以设置引导图在X轴上的偏移
viewParams.offX = (int) (-17 * density);
new Guide.Builder(this)
         .outsideTouchable(false)
         .guideSingelView(viewParams)            //设置单个引导
         .build()
         .show();
```
#### 效果如下（绿色的为引导图）
![Screenshot](https://github.com/15018777629/guide/blob/master/screens/screenshot1.jpg)
