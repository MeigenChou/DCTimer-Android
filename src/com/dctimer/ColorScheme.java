package com.dctimer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ColorScheme extends Dialog {
	Context context;
	private int cubeType;
	private int[] colors;
	private OnSchemeChangedListener mListener;
	
	public ColorScheme(Context context, int ct, int[] cl, OnSchemeChangedListener listener) {
		super(context);
		this.context = context;
		cubeType = ct;
		colors = cl;
		mListener = listener;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager manager = getWindow().getWindowManager();
		int size=Math.min(manager.getDefaultDisplay().getHeight(), manager.getDefaultDisplay().getWidth());
		int width = (int) (size * 0.8);
		int height = (int) (size * 0.6);
		ColorSchemeView myView = new ColorSchemeView(context, height, width);
		setContentView(myView);
	}
	
	private class ColorSchemeView extends View {
		private Paint mPaint;
		private int mHeight;
    	private int mWidth;
    	private int downInPosition;
    	
    	public ColorSchemeView(Context context, int height, int width) {
    		super(context);
			this.mHeight = height;
			this.mWidth = width;
			setMinimumHeight(height);
			setMinimumWidth(width);
			
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			downInPosition = 0;
    	}
    	
    	@Override
    	protected void onDraw(Canvas canvas) {
    		mPaint.setStyle(Paint.Style.FILL);
    		switch(cubeType){
    		case 1:
    		case 3:
    			if(downInPosition>0){
    				mPaint.setColor(0xff00ff00);
    				switch(downInPosition){
    				case 1:
    					canvas.drawRect(mWidth/4, mHeight*2/3, mWidth/2, mHeight, mPaint);
    					break;
    				case 2:
    					canvas.drawRect(mWidth*3/4, mHeight/3, mWidth, mHeight*2/3, mPaint);
    					break;
    				case 3:
    					canvas.drawRect(mWidth/2, mHeight/3, mWidth*3/4, mHeight*2/3, mPaint);
    					break;
    				case 4:
    					canvas.drawRect(mWidth/4, 0, mWidth/2, mHeight/3, mPaint);
    					break;
    				case 5:
    					canvas.drawRect(mWidth/4, mHeight/3, mWidth/2, mHeight*2/3, mPaint);
    					break;
    				case 6:
    					canvas.drawRect(0, mHeight/3, mWidth/4, mHeight*2/3, mPaint);
    					break;
    				}
    			}
    			mPaint.setColor(colors[3]);
    			canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.02), (float)(mWidth*0.48), (float)(mWidth*0.23), mPaint);
    			mPaint.setColor(colors[5]);
    			canvas.drawRect((float)(mWidth*0.02), (float)(mWidth*0.27), (float)(mWidth*0.23), (float)(mWidth*0.48), mPaint);
    			mPaint.setColor(colors[4]);
    			canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.27), (float)(mWidth*0.48), (float)(mWidth*0.48), mPaint);
    			mPaint.setColor(colors[2]);
    			canvas.drawRect((float)(mWidth*0.52), (float)(mWidth*0.27), (float)(mWidth*0.73), (float)(mWidth*0.48), mPaint);
    			mPaint.setColor(colors[1]);
    			canvas.drawRect((float)(mWidth*0.77), (float)(mWidth*0.27), (float)(mWidth*0.98), (float)(mWidth*0.48), mPaint);
    			mPaint.setColor(colors[0]);
    			canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.52), (float)(mWidth*0.48), (float)(mWidth*0.73), mPaint);
    			mPaint.setStyle(Style.STROKE);
    			mPaint.setColor(0xff000000);
    			canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.02), (float)(mWidth*0.48), (float)(mWidth*0.23), mPaint);
    			canvas.drawRect((float)(mWidth*0.02), (float)(mWidth*0.27), (float)(mWidth*0.23), (float)(mWidth*0.48), mPaint);
    			canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.27), (float)(mWidth*0.48), (float)(mWidth*0.48), mPaint);
    			canvas.drawRect((float)(mWidth*0.52), (float)(mWidth*0.27), (float)(mWidth*0.73), (float)(mWidth*0.48), mPaint);
    			canvas.drawRect((float)(mWidth*0.77), (float)(mWidth*0.27), (float)(mWidth*0.98), (float)(mWidth*0.48), mPaint);
    			canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.52), (float)(mWidth*0.48), (float)(mWidth*0.73), mPaint);
    			if(cubeType==1){
    				canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.09), (float)(mWidth*0.48), (float)(mWidth*0.16), mPaint);
    				canvas.drawRect((float)(mWidth*0.34), (float)(mWidth*0.02), (float)(mWidth*0.41), (float)(mWidth*0.23), mPaint);
    				canvas.drawRect((float)(mWidth*0.02), (float)(mWidth*0.34), (float)(mWidth*0.23), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.09), (float)(mWidth*0.27), (float)(mWidth*0.16), (float)(mWidth*0.48), mPaint);
    				canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.34), (float)(mWidth*0.48), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.34), (float)(mWidth*0.27), (float)(mWidth*0.41), (float)(mWidth*0.48), mPaint);
    				canvas.drawRect((float)(mWidth*0.52), (float)(mWidth*0.34), (float)(mWidth*0.73), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.59), (float)(mWidth*0.27), (float)(mWidth*0.66), (float)(mWidth*0.48), mPaint);
    				canvas.drawRect((float)(mWidth*0.77), (float)(mWidth*0.34), (float)(mWidth*0.98), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.84), (float)(mWidth*0.27), (float)(mWidth*0.91), (float)(mWidth*0.48), mPaint);
    				canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.59), (float)(mWidth*0.48), (float)(mWidth*0.66), mPaint);
    				canvas.drawRect((float)(mWidth*0.34), (float)(mWidth*0.52), (float)(mWidth*0.41), (float)(mWidth*0.73), mPaint);
    			}
    			else if(cubeType==3){
    				canvas.drawLine((float)(mWidth*0.347), (float)(mWidth*0.02), (float)(mWidth*0.403), (float)(mWidth*0.23), mPaint);
    				canvas.drawLine((float)(mWidth*0.403), (float)(mWidth*0.02), (float)(mWidth*0.347), (float)(mWidth*0.23), mPaint);
    				canvas.drawLine((float)(mWidth*0.27), (float)(mWidth*0.097), (float)(mWidth*0.48), (float)(mWidth*0.153), mPaint);
    				canvas.drawLine((float)(mWidth*0.27), (float)(mWidth*0.153), (float)(mWidth*0.48), (float)(mWidth*0.097), mPaint);
    				canvas.drawRect((float)(mWidth*0.02), (float)(mWidth*0.34), (float)(mWidth*0.23), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.153), (float)(mWidth*0.27), (float)(mWidth*0.097), (float)(mWidth*0.34), mPaint);
    				canvas.drawRect((float)(mWidth*0.153), (float)(mWidth*0.41), (float)(mWidth*0.097), (float)(mWidth*0.48), mPaint);
    				canvas.drawRect((float)(mWidth*0.27), (float)(mWidth*0.34), (float)(mWidth*0.48), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.347), (float)(mWidth*0.27), (float)(mWidth*0.403), (float)(mWidth*0.34), mPaint);
    				canvas.drawRect((float)(mWidth*0.347), (float)(mWidth*0.41), (float)(mWidth*0.403), (float)(mWidth*0.48), mPaint);
    				canvas.drawLine((float)(mWidth*0.347), (float)(mWidth*0.34), (float)(mWidth*0.347), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.52), (float)(mWidth*0.34), (float)(mWidth*0.73), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.597), (float)(mWidth*0.27), (float)(mWidth*0.653), (float)(mWidth*0.34), mPaint);
    				canvas.drawRect((float)(mWidth*0.597), (float)(mWidth*0.41), (float)(mWidth*0.653), (float)(mWidth*0.48), mPaint);
    				canvas.drawRect((float)(mWidth*0.77), (float)(mWidth*0.34), (float)(mWidth*0.98), (float)(mWidth*0.41), mPaint);
    				canvas.drawRect((float)(mWidth*0.847), (float)(mWidth*0.27), (float)(mWidth*0.903), (float)(mWidth*0.34), mPaint);
    				canvas.drawRect((float)(mWidth*0.847), (float)(mWidth*0.41), (float)(mWidth*0.903), (float)(mWidth*0.48), mPaint);
    				canvas.drawLine((float)(mWidth*0.847), (float)(mWidth*0.34), (float)(mWidth*0.847), (float)(mWidth*0.41), mPaint);
    				canvas.drawLine((float)(mWidth*0.347), (float)(mWidth*0.52), (float)(mWidth*0.403), (float)(mWidth*0.73), mPaint);
    				canvas.drawLine((float)(mWidth*0.403), (float)(mWidth*0.52), (float)(mWidth*0.347), (float)(mWidth*0.73), mPaint);
    				canvas.drawLine((float)(mWidth*0.27), (float)(mWidth*0.597), (float)(mWidth*0.48), (float)(mWidth*0.653), mPaint);
    				canvas.drawLine((float)(mWidth*0.27), (float)(mWidth*0.653), (float)(mWidth*0.48), (float)(mWidth*0.597), mPaint);
    				
    			}
    			break;
    		case 2:
    			float a = (float) (mHeight / Math.sqrt(3));
    			if(downInPosition>0){
    				switch(downInPosition){
    				case 1:
    					Mi.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2-a, mWidth/2, mWidth/2-a/2}, new float[]{0,0,mHeight/2},true);
    					break;
    				case 2:
    					Mi.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2, mWidth/2-a/2, mWidth/2+a/2}, new float[]{0,mHeight/2,mHeight/2},true);
    					break;
    				case 3:
    					Mi.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2, mWidth/2+a, mWidth/2+a/2}, new float[]{0,0,mHeight/2},true);
    					break;
    				case 4:
    					Mi.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2, mWidth/2-a/2, mWidth/2+a/2}, new float[]{mHeight,mHeight/2,mHeight/2},true);
    					break;
    				}
    			}
    			float b = (float) (a*0.08);
    			float c = (float) (a*0.08/Math.sqrt(3));
    			float d = (float) (a*0.14*Math.sqrt(3));
    			float[] ax = {(float)(mWidth/2-a+b), (float)(mWidth/2-b), (float)(mWidth/2-a/2)};
    			float[] ay = {c, c, (float)(mHeight/2-c*2)};
    			Mi.drawPolygon(mPaint, canvas, colors[0], ax, ay,true);
    			ax = new float[]{(float)(mWidth/2+b), (float)(mWidth/2+a-b), (float)(mWidth/2+a/2)};
    			ay = new float[]{c, c, (float)(mHeight/2-c*2)};
    			Mi.drawPolygon(mPaint, canvas, colors[2], ax, ay,true);
    			ax = new float[]{mWidth/2, (float)(mWidth/2-a/2+b), (float)(mWidth/2+a/2-b)};
    			ay = new float[]{c*2, (float)(mHeight/2-c), (float)(mHeight/2-c)};
    			Mi.drawPolygon(mPaint, canvas, colors[1], ax, ay,true);
    			ax = new float[]{mWidth/2, (float)(mWidth/2-a/2+b), (float)(mWidth/2+a/2-b)};
    			ay = new float[]{(float)(mHeight-c*2), (float)(mHeight/2+c), (float)(mHeight/2+c)};
    			Mi.drawPolygon(mPaint, canvas, colors[3], ax, ay,true);
    			ax = new float[]{(float)(mWidth/2-a*0.64), (float)(mWidth/2-a*0.36), (float)(mWidth/2-a*0.64), (float)(mWidth/2-a*0.36), (float)(mWidth/2-a*0.22), (float)(mWidth/2-a*0.78)};
    			ay = new float[]{c, c+2*d, c+2*d, c, c+d, c+d};
    			drawTriangle(mPaint, canvas, ax, ay);
    			ax = new float[]{(float)(mWidth/2+a*0.64), (float)(mWidth/2+a*0.36), (float)(mWidth/2+a*0.64), (float)(mWidth/2+a*0.36), (float)(mWidth/2+a*0.22), (float)(mWidth/2+a*0.78)};
    			ay = new float[]{c, c+2*d, c+2*d, c, c+d, c+d};
    			drawTriangle(mPaint, canvas, ax, ay);
    			ax = new float[]{(float)(mWidth/2+a*0.14), (float)(mWidth/2-a*0.14), (float)(mWidth/2-a*0.28), (float)(mWidth/2+a*0.28), (float)(mWidth/2+a*0.14), (float)(mWidth/2-a*0.14)};
    			ay = new float[]{mHeight/2-c-2*d, mHeight/2-c, mHeight/2-c-d, mHeight/2-c-d, mHeight/2-c, mHeight/2-c-2*d};
    			drawTriangle(mPaint, canvas, ax, ay);
    			ax = new float[]{(float)(mWidth/2+a*0.14), (float)(mWidth/2-a*0.14), (float)(mWidth/2-a*0.28), (float)(mWidth/2+a*0.28), (float)(mWidth/2+a*0.14), (float)(mWidth/2-a*0.14)};
    			ay = new float[]{mHeight/2+c+2*d, mHeight/2+c, mHeight/2+c+d, mHeight/2+c+d, mHeight/2+c, mHeight/2+c+2*d};
    			drawTriangle(mPaint, canvas, ax, ay);
    			break;
    		}
    		mPaint.setStyle(Style.FILL);
    		mPaint.setColor(0xffc0c0c0);
    		canvas.drawRect(mWidth*2/3, (float)(mHeight*0.81), mWidth, mHeight, mPaint);
    		mPaint.setTextSize(mWidth/16);
    		mPaint.setColor(0xff000000);
    		mPaint.setTextAlign(Align.CENTER);
    		canvas.drawText(context.getResources().getString(R.string.scheme_reset), mWidth*5/6, (float)(mHeight*0.93), mPaint);
    	}
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) {
    		float x = event.getX();
    		float y = event.getY();
    		int inPosition = 0;
    		switch(cubeType){
    		case 1:
    		case 3:inPosition=inCube(x, y);break;
    		case 2:inPosition=inPrym(x, y);break;
    		}
    		switch (event.getAction()) {
    		case MotionEvent.ACTION_DOWN:
    			downInPosition = inPosition;
    		case MotionEvent.ACTION_MOVE:
    			if(downInPosition>0 && inPosition!=downInPosition)downInPosition=0;
    			if(downInPosition<0 && downInPosition>=0)downInPosition=0;
    			invalidate();
    			break;
    		case MotionEvent.ACTION_UP:
    			if(downInPosition>0){
    				final int dip=downInPosition;
    				ColorPicker cp = new ColorPicker(context, colors[dip-1], new ColorPicker.OnColorChangedListener() {
    					@Override
						public void colorChanged(int color) {
    						colors[dip-1]=color;
    						invalidate();
    						if(mListener != null) {
    							mListener.schemeChanged(dip, color);
    						}
						}
    				});
    				cp.setTitle(context.getResources().getString(R.string.select_color));
    				cp.show();
    			}
    			else {
    				int[] color = null;
    				switch(cubeType) {
    				case 1:
    				case 3:
    					color = new int[]{0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff8026};
    					for(int i=1; i<7; i++)
    						if(cubeType==1) DCTimer.edit.remove("csn"+i);
    						else DCTimer.edit.remove("csq"+i);
    					break;
    				case 2:
    					color = new int[]{0xffff0000, 0xff009900, 0xff0000ff, 0xffffff00};
    					for(int i=1; i<5; i++) DCTimer.edit.remove("csp"+i);
    					break;
    				}
    				DCTimer.edit.commit();
    				colors = color;
    				invalidate();
    			}
    			downInPosition=0;
    			invalidate();
    			break;
    		}
    		return true;
    	}
    	
    	@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(mWidth, mHeight);
		}
    	
		private int inCube(float x, float y) {
			if(y>0 && y<mHeight/3){
				if(x>mWidth/4 && x<mWidth/2)return 4;
			}
			if(y>mHeight/3 && y<mHeight*2/3){
				if(x>0 && x<mWidth/4)return 6;
				if(x>mWidth/4 && x<mWidth/2)return 5;
				if(x>mWidth/2 && x<mWidth*3/4)return 3;
				if(x>mWidth*3/4 && x<mWidth)return 2;
			}
			if(y>mHeight*2/3 && y<mHeight){
				if(x>mWidth/4 && x<mWidth/2)return 1;
				if(y>mHeight*4/5 && x>mWidth*2/3 && x<mWidth)return -1;
			}
			return 0;
		}
		
		private int inPrym(float x, float y){
			if(y>0 && y<mHeight/2){
				if(x>0 && x<mWidth/3)return 1;
				if(x>mWidth/3 && x<mWidth*2/3)return 2;
				if(x>mWidth*2/3 && x<mWidth)return 3;
			}
			if(y>mHeight/2 && y<mHeight){
				if(x>mWidth/3 && x<mWidth*2/3)return 4;
				if(y>mHeight*4/5 && x>mWidth*2/3 && x<mWidth)return -1;
			}
			return 0;
		}
		private void drawTriangle(Paint p, Canvas c, float[] arx, float[] ary){
			p.setColor(0xff000000);
			Path path=new Path();
			path.moveTo(arx[0],ary[0]);
			for(int idx=1;idx<arx.length;idx++)path.lineTo(arx[idx], ary[idx]);
			path.close();
			c.drawPath(path, p);
		}
	}
	
	public interface OnSchemeChangedListener {
    	/**
    	 * 回调函数
    	 * @param color 选中的颜色
    	 */
        void schemeChanged(int idx, int color);
    }
	
	public OnSchemeChangedListener getmListener() {
		return mListener;
	}

	public void setmListener(OnSchemeChangedListener mListener) {
		this.mListener = mListener;
	}
}
