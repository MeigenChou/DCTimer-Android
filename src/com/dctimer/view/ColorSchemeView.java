package com.dctimer.view;

import com.dctimer.DCTimer;
import com.dctimer.R;
import scrambler.Scrambler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class ColorSchemeView extends View {
	Context context;
	private int cubeType;
	private int[] colors;
	
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int downInPosition;
	private boolean downInReset;
	
	private ColorPickerView cpv;
	private Path path = new Path();

	public ColorSchemeView(Context context, int width, int[] colors, int cubeType) {
		super(context);
		this.context = context;
		this.mHeight = (int)(width * 0.75);
		this.mWidth = width;
		this.colors = colors;
		this.cubeType = cubeType;
		setMinimumHeight(mHeight);
		setMinimumWidth(width);
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		downInPosition = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextSize(mWidth/16);
		mPaint.setTextAlign(Align.CENTER);
		switch(cubeType) {
		case 1:
		case 3:
		case 4:
			if(downInPosition>0) {
				mPaint.setColor(0xff00ff00);
				switch(downInPosition) {
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
			if(cubeType==1) {
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
				mPaint.setStyle(Style.FILL);
				canvas.drawText("U", (float)(mWidth*0.375), (float)(mWidth*0.15), mPaint);
				canvas.drawText("F", (float)(mWidth*0.375), (float)(mWidth*0.40), mPaint);
			}
			else if(cubeType==3) {
				canvas.drawLine((float)(mWidth*0.347), (float)(mWidth*0.02), (float)(mWidth*0.403), (float)(mWidth*0.23), mPaint);
				canvas.drawLine((float)(mWidth*0.403), (float)(mWidth*0.02), (float)(mWidth*0.347), (float)(mWidth*0.23), mPaint);
				canvas.drawLine((float)(mWidth*0.27), (float)(mWidth*0.097), (float)(mWidth*0.48), (float)(mWidth*0.153), mPaint);
				canvas.drawLine((float)(mWidth*0.27), (float)(mWidth*0.153), (float)(mWidth*0.48), (float)(mWidth*0.097), mPaint);
				canvas.drawRect((float)(mWidth*0.02), (float)(mWidth*0.34), (float)(mWidth*0.23), (float)(mWidth*0.41), mPaint);
				canvas.drawRect((float)(mWidth*0.097), (float)(mWidth*0.27), (float)(mWidth*0.153), (float)(mWidth*0.34), mPaint);
				canvas.drawRect((float)(mWidth*0.097), (float)(mWidth*0.41), (float)(mWidth*0.153), (float)(mWidth*0.48), mPaint);
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
				mPaint.setStyle(Style.FILL);
				canvas.drawText("U", (float)(mWidth*0.42), (float)(mWidth*0.20), mPaint);
				canvas.drawText("F", (float)(mWidth*0.375), (float)(mWidth*0.40), mPaint);
			}
			else if(cubeType==4) {
				path.reset();
				path.moveTo((float)(mWidth*0.375), (float)(mWidth*0.02));
				path.lineTo((float)(mWidth*0.48), (float)(mWidth*0.125));
				path.lineTo((float)(mWidth*0.375), (float)(mWidth*0.23));
				path.lineTo((float)(mWidth*0.27), (float)(mWidth*0.125));
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo((float)(mWidth*0.125), (float)(mWidth*0.27));
				path.lineTo((float)(mWidth*0.23), (float)(mWidth*0.375));
				path.lineTo((float)(mWidth*0.125), (float)(mWidth*0.48));
				path.lineTo((float)(mWidth*0.02), (float)(mWidth*0.375));
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo((float)(mWidth*0.375), (float)(mWidth*0.27));
				path.lineTo((float)(mWidth*0.48), (float)(mWidth*0.375));
				path.lineTo((float)(mWidth*0.375), (float)(mWidth*0.48));
				path.lineTo((float)(mWidth*0.27), (float)(mWidth*0.375));
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo((float)(mWidth*0.625), (float)(mWidth*0.27));
				path.lineTo((float)(mWidth*0.73), (float)(mWidth*0.375));
				path.lineTo((float)(mWidth*0.625), (float)(mWidth*0.48));
				path.lineTo((float)(mWidth*0.52), (float)(mWidth*0.375));
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo((float)(mWidth*0.875), (float)(mWidth*0.27));
				path.lineTo((float)(mWidth*0.98), (float)(mWidth*0.375));
				path.lineTo((float)(mWidth*0.875), (float)(mWidth*0.48));
				path.lineTo((float)(mWidth*0.77), (float)(mWidth*0.375));
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo((float)(mWidth*0.375), (float)(mWidth*0.52));
				path.lineTo((float)(mWidth*0.48), (float)(mWidth*0.625));
				path.lineTo((float)(mWidth*0.375), (float)(mWidth*0.73));
				path.lineTo((float)(mWidth*0.27), (float)(mWidth*0.625));
				path.close();
				canvas.drawPath(path, mPaint);
				mPaint.setStyle(Style.FILL);
				canvas.drawText("U", (float)(mWidth*0.375), (float)(mWidth*0.15), mPaint);
				canvas.drawText("FL", (float)(mWidth*0.375), (float)(mWidth*0.40), mPaint);
			}
			break;
		case 2:
			float a = (float) (mHeight / Math.sqrt(3));
			if(downInPosition > 0) {
				switch(downInPosition) {
				case 1:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2-a, mWidth/2, mWidth/2-a/2}, new float[]{0,0,mHeight/2},true);
					break;
				case 2:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2, mWidth/2-a/2, mWidth/2+a/2}, new float[]{0,mHeight/2,mHeight/2},true);
					break;
				case 3:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2, mWidth/2+a, mWidth/2+a/2}, new float[]{0,0,mHeight/2},true);
					break;
				case 4:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[]{mWidth/2, mWidth/2-a/2, mWidth/2+a/2}, new float[]{mHeight,mHeight/2,mHeight/2},true);
					break;
				}
			}
			float b = (float) (a*0.08);
			float c = (float) (a*0.08/Math.sqrt(3));
			float d = (float) (a*0.14*Math.sqrt(3));
			float[] ax = {(float)(mWidth/2-a+b), (float)(mWidth/2-b), (float)(mWidth/2-a/2)};
			float[] ay = {c, c, (float)(mHeight/2-c*2)};
			Scrambler.drawPolygon(mPaint, canvas, colors[0], ax, ay,true);
			ax = new float[]{(float)(mWidth/2+b), (float)(mWidth/2+a-b), (float)(mWidth/2+a/2)};
			ay = new float[]{c, c, (float)(mHeight/2-c*2)};
			Scrambler.drawPolygon(mPaint, canvas, colors[2], ax, ay,true);
			ax = new float[]{mWidth/2, (float)(mWidth/2-a/2+b), (float)(mWidth/2+a/2-b)};
			ay = new float[]{c*2, (float)(mHeight/2-c), (float)(mHeight/2-c)};
			Scrambler.drawPolygon(mPaint, canvas, colors[1], ax, ay,true);
			ax = new float[]{mWidth/2, (float)(mWidth/2-a/2+b), (float)(mWidth/2+a/2-b)};
			ay = new float[]{(float)(mHeight-c*2), (float)(mHeight/2+c), (float)(mHeight/2+c)};
			Scrambler.drawPolygon(mPaint, canvas, colors[3], ax, ay,true);
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
		canvas.drawRect((float)(mWidth*0.64), (float)(mHeight*0.81), (float)(mWidth*0.98), mHeight, mPaint);
		
		mPaint.setColor(0xff000000);
		canvas.drawText(context.getResources().getString(R.string.scheme_reset), (float)(mWidth*0.81), (float)(mHeight*0.93), mPaint);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int inPosition = 0;
		switch(cubeType) {
		case 1:
		case 3:
		case 4: inPosition=inCube(x, y); break;
		case 2: inPosition=inPrym(x, y); break;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downInPosition = inPosition;
			downInReset = inReset(x, y);
		case MotionEvent.ACTION_MOVE:
			if(downInPosition>0 && inPosition!=downInPosition)downInPosition=0;
			if(downInPosition<0 && downInPosition>=0)downInPosition=0;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if(downInPosition>0) {
				final int dip=downInPosition;
				cpv = new ColorPickerView(context, mWidth, colors[dip-1]);
				new com.dctimer.ui.CustomDialog.Builder(context).setTitle(R.string.select_color).setView(cpv).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int color = cpv.getColor();
						colors[dip-1]=color;
						invalidate();
						switch (cubeType) {
						case 1:
							DCTimer.edit.putInt("csn"+dip, color);
							break;
						case 2:
							DCTimer.edit.putInt("csp"+dip, color);
							break;
						case 3:
							DCTimer.edit.putInt("csq"+dip, color);
							break;
						case 4:
							DCTimer.edit.putInt("csw"+dip, color);
							break;
						}
						DCTimer.edit.commit();
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
			}
			else if(downInReset && inReset(x, y)) {
				int[] color = null;
				switch(cubeType) {
				case 1:
				case 3:
				case 4:
					color = new int[]{0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff8026};
					for(int i=1; i<7; i++)
						if(cubeType==1) DCTimer.edit.remove("csn"+i);
						else if(cubeType==3) DCTimer.edit.remove("csq"+i);
						else DCTimer.edit.remove("csw"+i);
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
			if(x>mWidth/4 && x<mWidth/2) return 4;
		}
		if(y>mHeight/3 && y<mHeight*2/3) {
			if(x>0 && x<mWidth/4) return 6;
			if(x>mWidth/4 && x<mWidth/2) return 5;
			if(x>mWidth/2 && x<mWidth*3/4) return 3;
			if(x>mWidth*3/4 && x<mWidth) return 2;
		}
		if(y>mHeight*2/3 && y<mHeight) {
			if(x>mWidth/4 && x<mWidth/2) return 1;
			if(y>mHeight*4/5 && x>mWidth*2/3 && x<mWidth) return -1;
		}
		return 0;
	}
	
	private int inPrym(float x, float y) {
		if(y>0 && y<mHeight/2) {
			if(x>0 && x<mWidth/3) return 1;
			if(x>mWidth/3 && x<mWidth*2/3) return 2;
			if(x>mWidth*2/3 && x<mWidth) return 3;
		}
		if(y>mHeight/2 && y<mHeight){
			if(x>mWidth/3 && x<mWidth*2/3) return 4;
			if(y>mHeight*4/5 && x>mWidth*2/3 && x<mWidth) return -1;
		}
		return 0;
	}
	
	private boolean inReset(float x, float y) {
		return x>mWidth*0.64 && y>mHeight*0.81;
	}
	
	private void drawTriangle(Paint p, Canvas c, float[] arx, float[] ary) {
		p.setColor(0xff000000);
		Path path=new Path();
		path.moveTo(arx[0],ary[0]);
		for(int idx=1;idx<arx.length;idx++)path.lineTo(arx[idx], ary[idx]);
		path.close();
		c.drawPath(path, p);
	}
}
