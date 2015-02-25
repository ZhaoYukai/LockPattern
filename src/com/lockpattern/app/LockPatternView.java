package com.lockpattern.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LockPatternView extends View{
	
	/*-------------------------------变量声明部分---------------------------------*/
	//先把这9个点声明出来，但是没有初始化
	private Point[][] points = new Point[3][3];
	
	//一个布尔型变量判断点是否已经经过初始化了
	private boolean isInit;
	
	//两个变量分别用来存储布局的宽和高
	private float width;
	private float height;
	
	//定义两个变量用于记录X和Y方向上的偏移量
	private float offsetX;
	private float offsetY;
	
	//定义一些Bitmap对象来存储图片资源
	private Bitmap pointNormal;
	private Bitmap pointPressed;
	private Bitmap pointError;
	private Bitmap linePressed;
	private Bitmap lineError;
	
	//在屏幕上画图还需要画笔
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	//为了准确设置画笔的落点，先定义出一个图像半径
	private float bitmapR;
	
	//为了画出点和点之间的连线，先声明一个点的集合变量。一旦一些点被按下，就把这些点放进一个集合中，然后在里面进行连线。所以这个pointList就是按下的点的集合
	private List<Point> pointList = new ArrayList<Point>();
	
	//为了记录鼠标移动的坐标，声明两个变量
	private float movingX;
	private float movingY;
	
	//用于判断点是否已经被选择过
	private boolean isSelect;
	
	//用于判断点的选择是否结束
	private boolean isFinish;
	
	private static final int POINT_SINZE = 4;
	
	//鼠标在移动，但是不是九宫格里面的点
	private boolean movingNoPoint;
	
	
	private Matrix matrix = new Matrix();
	
	//监听器
	private OnPatterChangeListener onPatterChangeListener;
	
	/*-------------------------------------------------------------------------*/
	
	
	/*-------------------------------构造函数部分---------------------------------*/
	//声明3个构造函数
	public LockPatternView(Context context) {
		super(context);
	}
	
	public LockPatternView(Context context , AttributeSet attrs) {
		super(context , attrs);
	}
	
	public LockPatternView(Context context , AttributeSet attrs , int defStyleAttr) {
		super(context , attrs , defStyleAttr);
	}
	/*-------------------------------------------------------------------------*/
	
	
	
	
	//绘制那9个点
	@Override
	protected void onDraw(Canvas canvas) {
		//在这里先把那9个点初始化一下
		if( ! isInit){ //如果没有经过初始化
			initPoints(); //那就调用这个函数初始化一下
		}
		
		//画点
		points2Canvas(canvas);
		
		//画线
		if(pointList.size() > 0){
			Point a = pointList.get(0);
			//绘制九宫格里面的点
			for(int i=0; i<pointList.size() ; i++){
				Point b = pointList.get(i);
				line2Canvas(canvas, a, b);
				a = b; //一次绘制完之后，原来的b点成了新的a点，而新的b点则在下一次循环中获得
			}
			//绘制鼠标的坐标点
			if(movingNoPoint){
				line2Canvas( canvas , a , new Point(movingX , movingY) );
			}
		}
	}
	
	
	/*-------------------------------------------------------------------------*/

	//一个自定义函数，用于点的初始化
	private void initPoints(){
		//(1)我们得获取布局的宽和高，之所以要这样做是因为横屏和竖屏点的位置是不一样的，所以我们应该先确认当前状态下是横屏还是竖屏的
		width = getWidth();
		height = getHeight();
		
		//(2)算偏移量
		//然后判断是横屏还是竖屏
		if(width > height){ //如果是横屏，则……
			offsetX = (width - height)/2;
			width = height; //由于九宫格是正方型，此时height简短，那就以它为基准，让width跟height的长度一样
		}
		else{ //如果是竖屏，则……
			offsetY = (height - width)/2;
			height = width; //由于九宫格是正方型，此时width简短，那就以它为基准，让height跟width的长度一样
		}
		
		
		//(3)导入图片资源
		pointNormal  = BitmapFactory.decodeResource(getResources() , R.drawable.oval_normal);
		pointPressed  = BitmapFactory.decodeResource(getResources() , R.drawable.oval_pressed);
		pointError  = BitmapFactory.decodeResource(getResources() , R.drawable.oval_error);
		linePressed  = BitmapFactory.decodeResource(getResources() , R.drawable.line_pressed);
		lineError  = BitmapFactory.decodeResource(getResources() , R.drawable.line_error);
		
		//(4)设置点的坐标
		//第一行
		points[0][0] = new Point(offsetX + width/4 , offsetY + width/4);
		points[0][1] = new Point(offsetX + width/2 , offsetY + width/4);
		points[0][2] = new Point(offsetX + width - width/4, offsetY + width/4);
		//第二行
		points[1][0] = new Point(offsetX + width/4 , offsetY + width/2);
		points[1][1] = new Point(offsetX + width/2 , offsetY + width/2);
		points[1][2] = new Point(offsetX + width - width/4 , offsetY + width/2);
		//第三行
		points[2][0] = new Point(offsetX + width/4 , offsetY + width - width/4);
		points[2][1] = new Point(offsetX + width/2 , offsetY + width - width/4);
		points[2][2] = new Point(offsetX + width - width/4 , offsetY + width - width/4);
		
		//(5)设置图片资源的半径，让画笔落点更准确
		bitmapR = pointNormal.getWidth() / 2; //这样就获取了图像的一半，也就是圆的半径
		
		//(6)设置密码
		int index = 1;
		for(Point[] p : points){
			for(Point q : p){
				q.index = index;
				index ++ ;
			}
		}
		
		//(7)初始化完成，把isInit标志设置为true
		isInit = true;
		
	}//自定义函数initPoints()结束
	
	/*-------------------------------------------------------------------------*/
	
	//一个自定义函数，用于将点在画布上画出来
	private void points2Canvas(Canvas canvas) {
		
		for(int i = 0; i< points.length ; i++){
			for(int j = 0; j< points[i].length ; j++){
				Point point = points[i][j];
				if(point.state == Point.STATE_PRESSED){
					//这里之所以要减去bitmapR是因为我们期望的圆的落点是以圆心为中心，而画笔在画的时候是从最左边开始画，这样就有了一个半径那样长的偏差
					canvas.drawBitmap(pointPressed , point.x - bitmapR , point.y - bitmapR , paint);
				}
				else if(point.state == Point.STATE_NORMAL){
					canvas.drawBitmap(pointNormal , point.x - bitmapR , point.y - bitmapR , paint);
				}
				else if(point.state == Point.STATE_ERROR){
					canvas.drawBitmap(pointError , point.x - bitmapR , point.y - bitmapR , paint);
				}
			}
		}
		
	}//自定义函数points2Canvas()结束
	
	
	/*-------------------------------------------------------------------------*/
	
	private void line2Canvas(Canvas canvas , Point a , Point b){
		
		//计算两点之间线的长度
		float lineLength = (float) Math.sqrt( Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y) );
		float degrees = getDegrees( a , b );
		
		canvas.rotate(degrees , a.x , a.y);
		
		if(a.state == Point.STATE_PRESSED){
			matrix.setScale(lineLength / linePressed.getWidth() , 1); //两个点的连线，图片资源是一个小方块，由于是动态拉长，所以x方向上会产生巨大缩放，而y方向上不变
			matrix.postTranslate(a.x - linePressed.getWidth() , a.y - linePressed.getHeight());
			canvas.drawBitmap(linePressed, matrix, paint);
		}
		else{
			matrix.setScale(lineLength / lineError.getWidth() , 1);
			matrix.postTranslate(a.x - lineError.getWidth() , a.y - lineError.getHeight());
			canvas.drawBitmap(lineError, matrix, paint);
		}
		
		//再转回来
		canvas.rotate(-degrees , a.x , a.y);
	}
	
	/*-------------------------------------------------------------------------*/
	
	private float getDegrees(Point a , Point b){
		float ax = a.x;
		float ay = a.y;
		float bx = b.x;
		float by = b.y;
		float degrees = 0;
		
		if(ax == bx){ //y轴相等 90度或270度
			if(by > ay){ //在y轴的下边 90
				degrees = 90;
			}
			else if(by < ay){ //在y轴的上边 270
				degrees = 270;
			}
		}
		else if(ay == by){ //y轴相等 0或180
			if(bx > ax){ //在y轴的下边 90
				degrees = 0;
			}
			else if(bx < ax){ //在y轴的上边 270
				degrees = 180;
			}
		}
		else{
			if(bx > ax){ //在y轴的右边 270~90
				if(by > ay){ //在y轴的下边 0~90
					degrees = 0;
					degrees = degrees + switchDegrees(Math.abs(by - ay) , Math.abs(bx - ax));
				}
				else if(by < ay){ //在y轴的上边 270~0
					degrees = 360;
					degrees = degrees - switchDegrees(Math.abs(by - ay) , Math.abs(bx - ax));
				}
			}
			else if(bx < ax){ //在y轴的左边 90~270
				if(by > ay){ //在y轴的下边 180~270
					degrees = 90;
					degrees = degrees + switchDegrees(Math.abs(bx - ax) , Math.abs(by - ay));
				}
				else if(by < ay){ //在y轴的上边 90~180
					degrees = 270;
					degrees = degrees - switchDegrees(Math.abs(bx - ax) , Math.abs(by - ay));
				}
			}
		}
		return degrees;
	}
	
	
	/*-------------------------------------------------------------------------*/
	
	
	private float switchDegrees(float x , float y){
		//弧度转化为角度
		return (float) Math.toDegrees(Math.atan2(x , y));
	}
	
	
	/*-------------------------------------------------------------------------*/
	
	//当触碰点的时候，会发生什么样的逻辑
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		movingNoPoint = false;
		isFinish = false;
		//当鼠标的坐标和圆的坐标相接近，控制在某一个范围内的时候，就开始进行一系列判断了
		movingX = event.getX();
		movingY = event.getY();
		
		Point point = null;
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			//如果鼠标当前“按下”的时候不在任何一个点的范围之内，checkSelectedPoint()会返回null，就什么操作也不执行
			//如果鼠标当前“按下”的时候在某一个点的范围内，就把这个点返回回来，由于先来后到，返回的是遇到的第一个点
			
			//重新绘制
			if(onPatterChangeListener != null){
				onPatterChangeListener.onPatterStart(true);
			}
			
			for(int i=0; i<pointList.size() ; i++){
				Point p = pointList.get(i);
				p.state = Point.STATE_NORMAL;
			}
			pointList.clear(); //在绘制之前先清空
			point = checkSelectedPoint();
			if(point != null){ //来判断你是否选中了某个点，一旦你选中了某个点，后面的绘制就正式开始了
				isSelect = true; //既然开始了，就把isSelect设置为true，表示“开始了”这样的标示
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(isSelect){ //当鼠标移动的时候，发现isSelect是true，表示当前可以开始绘制了，那就用checkSelectedPoint()检查一下鼠标碰到了那些其他的点
				point = checkSelectedPoint();
				if(point == null){
					movingNoPoint = true;
				}
			}
			break;
		case MotionEvent.ACTION_UP: //一旦鼠标抬起的时候，表示选择点的过程就结束了，那就让isSelect为false，并让isFinish为true。
			isFinish = true;
			isSelect = false;
			break;
		default:
			break;
		}
		
		//对已被选中的，再选就重复的点进行检查
		if( ! isFinish && isSelect && point != null){
			if(pointList.contains(point)){ //如果当前选中的点已经被包含了
				movingNoPoint = true;
			}
			else{ //如果这是一个新点
				point.state = Point.STATE_PRESSED;
				pointList.add(point);
			}
		}
		
		//绘制结束
		if(isFinish){
			if(pointList.size() == 1){ //绘制不成立
				resetPoint();
			}
			else if(pointList.size() > 0 && pointList.size() < POINT_SINZE){ //如果绘制错误
				for(Point p : pointList){
					p.state = Point.STATE_ERROR;
				}
				if(onPatterChangeListener != null){
					onPatterChangeListener.onPatterChange(null);
				}
			}
			else{ //如果绘制成功
				if(onPatterChangeListener != null){
					String passwordStr = "";
					for(int i=0; i<pointList.size() ; i++){
						passwordStr = passwordStr + pointList.get(i).index; //把角标一个一个拼接到字符串里
					}
					if( ! TextUtils.isEmpty(passwordStr)){
						onPatterChangeListener.onPatterChange(passwordStr);
					}
				}
			}
		}
		
		postInvalidate(); //每次执行onTouchEvent都要调用这个函数，让View刷新一下
		
		return true;
	}
	
	/*-------------------------------------------------------------------------*/
	
	public void resetPoint(){
		for(int i=0; i<pointList.size() ; i++){
			Point p = pointList.get(i);
			p.state = Point.STATE_NORMAL;
		}
		pointList.clear();
	}
	
	/*-------------------------------------------------------------------------*/
	
	//该函数是这样，如果鼠标的落点正好在圆内，就把当前这个圆点给毫不犹豫立即返回出去，表示这个点被选中了。因此要用两层for循环遍历所有点进行判断
	private Point checkSelectedPoint(){
		
		for(int i=0;i<points.length;i++){
			for(int j=0;j<points[i].length;j++){
				Point point = points[i][j];
				if( (point.x - movingX)*(point.x - movingX) + (point.y - movingY)*(point.y - movingY) < bitmapR*bitmapR ){
					return point;
				}
			}
		}
		
		return null;
	}
	
	/*-------------------------------------------------------------------------*/
	
	/*
	 * 设置图案监听器（这个监听器会在onTouch的时候被触发）
	 */
	public static interface OnPatterChangeListener{
		/*
		 * 图案改变
		 * @param passwordStr 图案密码
		 */
		void onPatterChange(String passwordStr);
		/*
		 * 图案重新绘制
		 * @param isStart 是否重新绘制
		 */
		void onPatterStart(boolean isStart);
	}
	
	
	/*
	 * 设置图案监听器
	 * @param onPatterChangeListener
	 */
	public void setPatterChangeListener(OnPatterChangeListener onPatterChangeListener){
		if(onPatterChangeListener != null){
			this.onPatterChangeListener = onPatterChangeListener;
		}
	}
	

	/*-------------------------------内部类部分---------------------------------*/
	public static class Point{
		
		//正常时候的标示
		public static int STATE_NORMAL = 0;
		
		//选中时候的标示
		public static int STATE_PRESSED = 1;
		
		//错误时候的标示
		public static int STATE_ERROR = 2;
		
		public float x,y; //点的x,y坐标
		public int index = 0;
		public int state = 0;
		
		public Point(){
			//什么也不写
		}
		
		public Point (float x , float y){
			this.x = x;
			this.y = y;
		}
		
	}//Point类结束
	/*----------------------------------------------------------------------*/
}
