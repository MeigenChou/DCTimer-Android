package com.dctimer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ColorPicker extends Dialog {
	Context context;
	private int mInitialColor;	//初始颜色
	private OnColorChangedListener mListener;
	
	/**
     * @param context
     * @param initialColor 初始颜色
     * @param listener 回调
     */
    public ColorPicker(Context context, int initialColor, OnColorChangedListener listener) {
        super(context);
        this.context = context;
        mListener = listener;
        mInitialColor = initialColor;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager manager = getWindow().getWindowManager();
        int size = Math.min(manager.getDefaultDisplay().getHeight(), manager.getDefaultDisplay().getWidth());
        size = size * 8 / 10;
//		int height = (int) (size * 0.8);
//		int width = (int) (size * 0.8);
		ColorPickerView myView = new ColorPickerView(context, size, size);
        setContentView(myView);
    }
    
    private class ColorPickerView extends View {
    	private Paint mPaint;//七彩图画笔
    	private Paint mShaderPaint;//饱和度画笔
    	private Paint mRectPaint;//渐变方块画笔
    	private Paint mLeftPaint;//选择块画笔
    	private Paint mRightPaint;
    	
    	private int[] mCircleColors;//渐变图颜色
    	private int[] mRectColors;//渐变方块颜色
    	
    	private int mHeight;//View高
    	private int mWidth;//View宽
    	private int hue; //色调
    	private double saturation; //饱和度
    	private double lum; //亮度
    	
    	private boolean downInCRect = true;//按在七彩图上
    	private boolean downInRect;//按在渐变方块上
    	private int downInBottom; //按在选择块上
    	private int downInDef;	//按在预设块上
    	private int[] defColor = {Color.RED, Color.YELLOW, 0xff009900, Color.WHITE, Color.BLUE, 0xffff8026};
    	
    	public ColorPickerView(Context context, int height, int width) {
    		super(context);
			this.mHeight = height;
			this.mWidth = width;
			setMinimumHeight(height);
			setMinimumWidth(width);
			
			//七彩图参数
			mCircleColors = new int[] {0xffff0000, 0xffffff00, 0xff00ff00, 0xff00ffff,
		            0xff0000ff, 0xffff00ff, 0xffff0000};
			float[] op = {0, 0.16667f, 0.33333f, 0.5f, 0.66667f, 0.83333f, 1};
			LinearGradient lg = new LinearGradient(0, 0, (int)(width*0.82), 0, mCircleColors, op,
		            TileMode.MIRROR);
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(lg);
			lg = new LinearGradient(0, 0, 0, (int)(height*0.67), 0x00808080,
		            0xff808080, TileMode.MIRROR);
			mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mShaderPaint.setShader(lg);
			
			//选择块参数
			mLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mLeftPaint.setColor(mInitialColor);
			mRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mRightPaint.setColor(mInitialColor);

			//渐变参数
			mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			
			double[] hsl = rgbToHSL(mInitialColor);
			hue = (int)hsl[0];
			saturation = hsl[1];
    		lum = hsl[2];
    	}
    	
    	@Override
    	protected void onDraw(Canvas canvas) {
    		//画七彩图
    		canvas.drawRect(0, 0, (float)(mWidth*0.82), (float)(mHeight*0.67), mPaint);
    		canvas.drawRect(0, 0, (float)(mWidth*0.82), (float)(mHeight*0.67), mShaderPaint);
    		float dx = (float) (hue*mWidth*0.82/360);
    		float dy = (float) ((1-saturation)*mHeight*0.67);
    		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    		paint.setColor(Color.BLACK);
    		paint.setStyle(Paint.Style.FILL);
    		canvas.drawRect((float)(dx-mWidth*0.04), dy-1, (float)(dx-mWidth*0.01), dy+1, paint);
    		canvas.drawRect((float)(dx+mWidth*0.01), dy-1, (float)(dx+mWidth*0.04), dy+1, paint);
    		canvas.drawRect(dx-1, (float)(dy-mWidth*0.04), dx+1, (float)(dy-mWidth*0.01), paint);
    		canvas.drawRect(dx-1, (float)(dy+mWidth*0.01), dx+1, (float)(dy+mWidth*0.04), paint);
    		//画亮度条
    		int x = hslToRgb(hue, saturation, 0.5);
    		mRectColors = new int[] {0xffffffff, x, 0xff000000};
    		float[] op = new float[]{0, 0.5f, 1};
    		LinearGradient lg = new LinearGradient(0, 0, 0, (float)(mHeight*0.67), mRectColors, op, TileMode.MIRROR);
    		mRectPaint.setShader(lg);
    		canvas.drawRect((float)(mWidth*0.84), 0, mWidth, (float)(mHeight*0.67), mRectPaint);
    		dy = (float) ((1-lum)*mHeight*0.67);
    		paint.setColor(Color.WHITE);
    		Path path = new Path();
    		path.moveTo((float)(mWidth*0.82), (float)(dy-mWidth*0.02));
    		path.lineTo((float)(mWidth*0.82), (float)(dy+mWidth*0.02));
    		path.lineTo((float)(mWidth*0.84), dy);
    		path.close();
    		canvas.drawPath(path, paint);
    		//画选择块
    		float a = (float) (mWidth / 52.);
    		canvas.drawRoundRect(new RectF(0, (float)(mHeight*0.69), (float)(mWidth*0.49), (float)(mHeight*0.84)), a, a, mLeftPaint);
    		canvas.drawRoundRect(new RectF((float)(mWidth*0.51), (float)(mHeight*0.69), mWidth, (float)(mHeight*0.84)), a, a, mRightPaint);
    		paint.setColor((0xffffff - mLeftPaint.getColor()&0xffffff)|0xff000000);
    		paint.setStyle(Style.STROKE);
    		canvas.drawRoundRect(new RectF(0, (float)(mHeight*0.69), (float)(mWidth*0.49), (float)(mHeight*0.84)), a, a, paint);
    		paint.setColor((0xffffff - mRightPaint.getColor()&0xffffff)|0xff000000);
    		canvas.drawRoundRect(new RectF((float)(mWidth*0.51), (float)(mHeight*0.69), mWidth, (float)(mHeight*0.84)), a, a, paint);
    		
    		super.onDraw(canvas);
    		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    		textPaint.setTextSize(mHeight/16);
    		textPaint.setTextAlign(Align.CENTER);
    		textPaint.setColor((0xffffff - mLeftPaint.getColor()&0xffffff)|0xff000000);
    		canvas.drawText(context.getResources().getString(R.string.btn_ok), mWidth/4, (float)(mHeight*0.785), textPaint);
    		textPaint.setColor((0xffffff - mRightPaint.getColor()&0xffffff)|0xff000000);
    		canvas.drawText(context.getResources().getString(R.string.btn_cancel), mWidth*3/4, (float)(mHeight*0.785), textPaint);
    		
    		//画预设块
    		paint.setStyle(Style.FILL);
    		for(int i=0; i<6; i++) {
    			paint.setColor(defColor[i]);
    			canvas.drawRoundRect(new RectF((float)(mWidth*(i*0.172)), (float)(mHeight*0.86), (float)(mHeight*(i*0.172+0.14)), mHeight), a, a, paint);
        	}
    	}
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) {
    		float x = event.getX();
    		float y = event.getY();
    		boolean inCRect = inColorRect(x, y);
    		int inBottom = inBottom(x, y);
    		boolean inRect = inRect(x, y);
    		int inDef = inDefault(x, y);
    		
    		switch (event.getAction()) {
    		case MotionEvent.ACTION_DOWN:
    			downInCRect = inCRect;
    			downInRect = inRect;
    			downInBottom = inBottom;
    			downInDef = inDef;
    		case MotionEvent.ACTION_MOVE:
    			if(downInCRect && inCRect) { //down按在渐变色环内, 且move也在渐变色环内
    				hue = getHue(x);
    				saturation = getSl(y);
    				mLeftPaint.setColor(hslToRgb(hue, saturation, lum));
    			} else if(downInRect && inRect) { //down在渐变方块内, 且move也在渐变方块内
    				lum = getSl(y);
    				mLeftPaint.setColor(hslToRgb(hue, saturation, lum));
    				//Log.v("text", hue+","+saturation+","+lum);
    			} else if(downInDef >= 0 && inDef == downInDef) {
    				double[] hsl = rgbToHSL(defColor[inDef]);
    				hue = (int)hsl[0];
    				saturation = hsl[1];
    	    		lum = hsl[2];
    	    		mLeftPaint.setColor(defColor[inDef]);
    			}
    			if(downInBottom == 1 && inBottom !=1) {
    				downInBottom = 0;
    			}
    			else if(downInBottom == 2 && inBottom !=2) {
    				downInBottom = 0;
    			}
    			else if(downInDef >=0 && inDef < 0) {
    				downInDef = -1;
    			}
    			invalidate();
            	break;
    		case MotionEvent.ACTION_UP:
    			if(downInBottom == 1){
    				if(mListener != null) {
    					mListener.colorChanged(mLeftPaint.getColor());
    					ColorPicker.this.dismiss();
    				}
    			}
    			else if(downInBottom == 2) {
    				ColorPicker.this.dismiss();
    			}
    			break;
    		}
    		return true;
    	}

    	@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(mWidth, mHeight);
		}

    	/**
		 * 坐标是否在七彩图上
		 * @param x 坐标
		 * @param y 坐标
		 * @return
		 */
		private boolean inColorRect(float x, float y) {
			return x > 0 && x < mWidth * 0.82 && y>0 && y < mHeight * 0.67;
		}

		/**
		 * 坐标是否在选择块上
		 * @param x 坐标
		 * @param y 坐标
		 * @return 0:不在选择块
		 * 	1:左边选择块
		 *  2:右边选择块
		 */
		private int inBottom(float x, float y) {
			if(y < mHeight * 0.69) return 0;
			if(y < mHeight * 0.84) {
				if(x > 0 && x < mWidth / 2) return 1;
				else if(x > mWidth / 2 && x < mWidth) return 2;
			}
			return 0;
		}

		/**
		 * 坐标是否在渐变色中
		 * @param x
		 * @param y
		 * @return
		 */
		private boolean inRect(float x, float y) {
			return x > mWidth * 0.84 && x < mWidth && y > 0 && y < mHeight * 0.67;
		}

		/**
		 * 坐标是否在预设块中
		 * @param x
		 * @param y
		 * @return color idx
		 */
		private int inDefault(float x, float y) {
			if(y < mHeight * 0.86) return -1;
			if(x < mWidth * 0.167) return 0;
			if(x < mWidth * 0.333) return 1;
			if(x < mWidth / 2) return 2;
			if(x < mWidth * 0.667) return 3;
			if(x < mWidth * 0.833) return 4;
			return 5;
		}

		/**
		 * 获取七彩图色调
		 * @param x
		 * @return
		 */
		private int getHue(float x) {
            return (int) (x * 439.02439 / mWidth);
        }
		
		/**
		 * 获取七彩图饱和度、亮度
		 * @param y
		 * @return
		 */
		private double getSl(float y) {
            return 1 - y / mWidth / 0.67;
        }
    }
    
    /**
     * 回调接口
     * @author <a href="clarkamx@gmail.com">LynK</a>
     * 
     * Create on 2012-1-6 上午8:21:05
     *
     */
    public interface OnColorChangedListener {
    	/**
    	 * 回调函数
    	 * @param color 选中的颜色
    	 */
        void colorChanged(int color);
    }
    
    public int getmInitialColor() {
		return mInitialColor;
	}

	public void setmInitialColor(int mInitialColor) {
		this.mInitialColor = mInitialColor;
	}

	public OnColorChangedListener getmListener() {
		return mListener;
	}

	public void setmListener(OnColorChangedListener mListener) {
		this.mListener = mListener;
	}
	
	public static int hslToRgb(int h, double s, double l) {
		double r, g, b;
		if(s == 0) r = g = b = l;
		else {
			double q, p, tr, tg, tb;
			if(l<0.5) q = l * (1 + s);
			else q = l + s - l * s;
			p = 2 * l - q;
			double H = h/360D;
			tr = H + 1/3D;
			tg = H;
			tb = H - 1/3D;
			r = toRGB(tr, q, p, H);
			g = toRGB(tg, q, p, H);
			b = toRGB(tb, q, p, H);
		}
		r = r * 255 + 0.5;
		g = g * 255 + 0.5;
		b = b * 255 + 0.5;
		return Color.rgb((int)r, (int)g, (int)b);
	}
	
	public static double[] rgbToHSL(int rgb) {
		double R = ((rgb>>16) & 0xff) / 255D;
		double G = ((rgb>>8) & 0xff) / 255D;
		double B = (rgb & 0xff) / 255D;
		double h = 0, s = 0, l;
		double max = Math.max(Math.max(R, G), B);
		double min = Math.min(Math.min(R, G), B);
		if(max == min) h = 0;
		else if(max == R && G >= B) h = 60 * ((G - B) / (max - min));
		else if(max == R && G < B) h = 60 * ((G - B) / (max - min)) + 360;
		else if(max == G) h = 60 * ((B - R) / (max - min)) + 120;
		else if(max == B) h = 60 * ((R - G) / (max - min)) + 240;
		l = (max + min) / 2;
		if(l == 0 || max == min) s = 0;
		else if(l > 0 && l <= 0.5)s = (max - min) / (max + min);
		else if(l > 0.5) s = (max - min) / (2 - (max + min));
		return new double[]{h, s, l};
	}
	
	private static double toRGB(double tc, double q, double p, double H) {
		if(tc < 0)tc += 1;
		if(tc > 1)tc -= 1;
		if(tc < 1/6D)
			return p + (q - p) * 6 * tc;
		else if(tc < 0.5)
			return q;
		else if(tc < 2/3D)
			return p + (q - p) * 6 * (2/3D - tc);
		else return p;
	}
}
