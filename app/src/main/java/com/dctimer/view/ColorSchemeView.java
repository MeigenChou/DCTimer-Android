package com.dctimer.view;

import com.dctimer.APP;
import com.dctimer.activity.MainActivity;
import com.dingmouren.colorpicker.ColorPickerDialog;
import com.dingmouren.colorpicker.OnColorPickerListener;

import scrambler.Scrambler;
import android.annotation.SuppressLint;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class ColorSchemeView extends View {
	MainActivity context;
	private int cubeType;
	private int[] colors;
	private int[] defColors;
	
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int downInPosition;
	//private boolean downInReset;
	
	//private ColorPickerDialog cpd;
	private Path path = new Path();

	public ColorSchemeView(MainActivity context, int width, int[] colors, int cubeType) {
		super(context);
		this.context = context;
		this.mHeight = width * 3 / 4;
		this.mWidth = width;
		this.colors = colors;
		this.cubeType = cubeType;
		setMinimumHeight(mHeight);
		setMinimumWidth(width);
		switch (cubeType) {
			case 1:	//n阶
			case 4:	//斜转
				defColors = new int[] {0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff9900};
				break;
			case 3:	//sq1
				defColors = new int[] {0xffffffff, 0xffff9900, 0xff009900, 0xffffff00, 0xffff0000, 0xff0000ff};
				break;
			case 2:	//金字塔
				defColors = new int[] {0xffff0000, 0xff009900, 0xff0000ff, 0xffffff00};
				break;
		}
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		downInPosition = 0;
	}

	public void setColor(int[] color) {
		this.colors = color;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(mWidth / 20);
		mPaint.setTextAlign(Align.CENTER);
		switch (cubeType) {
		case 1:
		case 3:
		case 4:
			if (downInPosition > 0) {
				mPaint.setColor(0xff00ff00);
				switch (downInPosition) {
				case 1:
					canvas.drawRect(mWidth / 4, mHeight * 2 / 3, mWidth / 2, mHeight, mPaint);
					break;
				case 2:
					canvas.drawRect(mWidth * 3 / 4, mHeight / 3, mWidth, mHeight * 2 / 3, mPaint);
					break;
				case 3:
					canvas.drawRect(mWidth / 2, mHeight / 3, mWidth * 3 / 4, mHeight * 2 / 3, mPaint);
					break;
				case 4:
					canvas.drawRect(mWidth / 4, 0, mWidth / 2, mHeight / 3, mPaint);
					break;
				case 5:
					canvas.drawRect(mWidth / 4, mHeight / 3, mWidth / 2, mHeight * 2 / 3, mPaint);
					break;
				case 6:
					canvas.drawRect(0, mHeight / 3, mWidth / 4, mHeight * 2 / 3, mPaint);
					break;
				}
			}
			mPaint.setColor(colors[3]);
			canvas.drawRect(mWidth * 0.27f, mWidth * 0.02f, mWidth * 0.48f, mWidth * 0.23f, mPaint);
			mPaint.setColor(colors[5]);
			canvas.drawRect(mWidth * 0.02f, mWidth * 0.27f, mWidth * 0.23f, mWidth * 0.48f, mPaint);
			mPaint.setColor(colors[4]);
			canvas.drawRect(mWidth * 0.27f, mWidth * 0.27f, mWidth * 0.48f, mWidth * 0.48f, mPaint);
			mPaint.setColor(colors[2]);
			canvas.drawRect(mWidth * 0.52f, mWidth * 0.27f, mWidth * 0.73f, mWidth * 0.48f, mPaint);
			mPaint.setColor(colors[1]);
			canvas.drawRect(mWidth * 0.77f, mWidth * 0.27f, mWidth * 0.98f, mWidth * 0.48f, mPaint);
			mPaint.setColor(colors[0]);
			canvas.drawRect(mWidth * 0.27f, mWidth * 0.52f, mWidth * 0.48f, mWidth * 0.73f, mPaint);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(APP.dpi);
			mPaint.setColor(0xff000000);
			canvas.drawRect(mWidth * 0.27f, mWidth * 0.02f, mWidth * 0.48f, mWidth * 0.23f, mPaint);
			canvas.drawRect(mWidth * 0.02f, mWidth * 0.27f, mWidth * 0.23f, mWidth * 0.48f, mPaint);
			canvas.drawRect(mWidth * 0.27f, mWidth * 0.27f, mWidth * 0.48f, mWidth * 0.48f, mPaint);
			canvas.drawRect(mWidth * 0.52f, mWidth * 0.27f, mWidth * 0.73f, mWidth * 0.48f, mPaint);
			canvas.drawRect(mWidth * 0.77f, mWidth * 0.27f, mWidth * 0.98f, mWidth * 0.48f, mPaint);
			canvas.drawRect(mWidth * 0.27f, mWidth * 0.52f, mWidth * 0.48f, mWidth * 0.73f, mPaint);
			mPaint.setStrokeWidth(1);
			if (cubeType == 1) {
				canvas.drawRect(mWidth * 0.27f, mWidth * 0.09f, mWidth * 0.48f, mWidth * 0.16f, mPaint);
				canvas.drawRect(mWidth * 0.34f, mWidth * 0.02f, mWidth * 0.41f, mWidth * 0.23f, mPaint);
				canvas.drawRect(mWidth * 0.02f, mWidth * 0.34f, mWidth * 0.23f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.09f, mWidth * 0.27f, mWidth * 0.16f, mWidth * 0.48f, mPaint);
				canvas.drawRect(mWidth * 0.27f, mWidth * 0.34f, mWidth * 0.48f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.34f, mWidth * 0.27f, mWidth * 0.41f, mWidth * 0.48f, mPaint);
				canvas.drawRect(mWidth * 0.52f, mWidth * 0.34f, mWidth * 0.73f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.59f, mWidth * 0.27f, mWidth * 0.66f, mWidth * 0.48f, mPaint);
				canvas.drawRect(mWidth * 0.77f, mWidth * 0.34f, mWidth * 0.98f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.84f, mWidth * 0.27f, mWidth * 0.91f, mWidth * 0.48f, mPaint);
				canvas.drawRect(mWidth * 0.27f, mWidth * 0.59f, mWidth * 0.48f, mWidth * 0.66f, mPaint);
				canvas.drawRect(mWidth * 0.34f, mWidth * 0.52f, mWidth * 0.41f, mWidth * 0.73f, mPaint);
				mPaint.setStyle(Style.FILL);
				canvas.drawText("U", mWidth * 0.375f, mWidth * 0.144f, mPaint);
				canvas.drawText("F", mWidth * 0.375f, mWidth * 0.394f, mPaint);
			}
			else if (cubeType == 3) {
				canvas.drawLine(mWidth * 0.347f, mWidth * 0.02f, mWidth * 0.403f, mWidth * 0.23f, mPaint);
				canvas.drawLine(mWidth * 0.403f, mWidth * 0.02f, mWidth * 0.347f, mWidth * 0.23f, mPaint);
				canvas.drawLine(mWidth * 0.27f, mWidth * 0.097f, mWidth * 0.48f, mWidth * 0.153f, mPaint);
				canvas.drawLine(mWidth * 0.27f, mWidth * 0.153f, mWidth * 0.48f, mWidth * 0.097f, mPaint);
				canvas.drawRect(mWidth * 0.02f, mWidth * 0.34f, mWidth * 0.23f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.097f, mWidth * 0.27f, mWidth * 0.153f, mWidth * 0.34f, mPaint);
				canvas.drawRect(mWidth * 0.097f, mWidth * 0.41f, mWidth * 0.153f, mWidth * 0.48f, mPaint);
				canvas.drawRect(mWidth * 0.27f, mWidth * 0.34f, mWidth * 0.48f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.347f, mWidth * 0.27f, mWidth * 0.403f, mWidth * 0.34f, mPaint);
				canvas.drawRect(mWidth * 0.347f, mWidth * 0.41f, mWidth * 0.403f, mWidth * 0.48f, mPaint);
				canvas.drawLine(mWidth * 0.347f, mWidth * 0.34f, mWidth * 0.347f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.52f, mWidth * 0.34f, mWidth * 0.73f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.597f, mWidth * 0.27f, mWidth * 0.653f, mWidth * 0.34f, mPaint);
				canvas.drawRect(mWidth * 0.597f, mWidth * 0.41f, mWidth * 0.653f, mWidth * 0.48f, mPaint);
				canvas.drawRect(mWidth * 0.77f, mWidth * 0.34f, mWidth * 0.98f, mWidth * 0.41f, mPaint);
				canvas.drawRect(mWidth * 0.847f, mWidth * 0.27f, mWidth * 0.903f, mWidth * 0.34f, mPaint);
				canvas.drawRect(mWidth * 0.847f, mWidth * 0.41f, mWidth * 0.903f, mWidth * 0.48f, mPaint);
				canvas.drawLine(mWidth * 0.847f, mWidth * 0.34f, mWidth * 0.847f, mWidth * 0.41f, mPaint);
				canvas.drawLine(mWidth * 0.347f, mWidth * 0.52f, mWidth * 0.403f, mWidth * 0.73f, mPaint);
				canvas.drawLine(mWidth * 0.403f, mWidth * 0.52f, mWidth * 0.347f, mWidth * 0.73f, mPaint);
				canvas.drawLine(mWidth * 0.27f, mWidth * 0.597f, mWidth * 0.48f, mWidth * 0.653f, mPaint);
				canvas.drawLine(mWidth * 0.27f, mWidth * 0.653f, mWidth * 0.48f, mWidth * 0.597f, mPaint);
				mPaint.setStyle(Style.FILL);
				canvas.drawText("U", mWidth * 0.42f, mWidth * 0.195f, mPaint);
				canvas.drawText("F", mWidth * 0.375f, mWidth * 0.395f, mPaint);
			}
			else if (cubeType == 4) {
				path.reset();
				path.moveTo(mWidth * 0.375f, mWidth * 0.02f);
				path.lineTo(mWidth * 0.48f, mWidth * 0.125f);
				path.lineTo(mWidth * 0.375f, mWidth * 0.23f);
				path.lineTo(mWidth * 0.27f, mWidth * 0.125f);
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo(mWidth * 0.125f, mWidth * 0.27f);
				path.lineTo(mWidth * 0.23f, mWidth * 0.375f);
				path.lineTo(mWidth * 0.125f, mWidth * 0.48f);
				path.lineTo(mWidth * 0.02f, mWidth * 0.375f);
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo(mWidth * 0.375f, mWidth * 0.27f);
				path.lineTo(mWidth * 0.48f, mWidth * 0.375f);
				path.lineTo(mWidth * 0.375f, mWidth * 0.48f);
				path.lineTo(mWidth * 0.27f, mWidth * 0.375f);
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo(mWidth * 0.625f, mWidth * 0.27f);
				path.lineTo(mWidth * 0.73f, mWidth * 0.375f);
				path.lineTo(mWidth * 0.625f, mWidth * 0.48f);
				path.lineTo(mWidth * 0.52f, mWidth * 0.375f);
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo(mWidth * 0.875f, mWidth * 0.27f);
				path.lineTo(mWidth * 0.98f, mWidth * 0.375f);
				path.lineTo(mWidth * 0.875f, mWidth * 0.48f);
				path.lineTo(mWidth * 0.77f, mWidth * 0.375f);
				path.close();
				canvas.drawPath(path, mPaint);
				path.reset();
				path.moveTo(mWidth * 0.375f, mWidth * 0.52f);
				path.lineTo(mWidth * 0.48f, mWidth * 0.625f);
				path.lineTo(mWidth * 0.375f, mWidth * 0.73f);
				path.lineTo(mWidth * 0.27f, mWidth * 0.625f);
				path.close();
				canvas.drawPath(path, mPaint);
				mPaint.setStyle(Style.FILL);
				canvas.drawText("U", mWidth * 0.375f, mWidth * 0.145f, mPaint);
				canvas.drawText("F", mWidth * 0.375f, mWidth * 0.395f, mPaint);
			}
			break;
		case 2:
			float a = (float) (mHeight / Math.sqrt(3));
			if (downInPosition > 0) {
				switch (downInPosition) {
				case 1:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[] {mWidth / 2 - a, mWidth / 2, mWidth / 2 - a / 2}, new float[] {0, 0, mHeight / 2},false);
					break;
				case 2:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[] {mWidth / 2, mWidth / 2 - a / 2, mWidth / 2 + a / 2}, new float[] {0, mHeight / 2, mHeight / 2},false);
					break;
				case 3:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[] {mWidth / 2, mWidth / 2 + a, mWidth / 2 + a / 2}, new float[] {0, 0, mHeight / 2},false);
					break;
				case 4:
					Scrambler.drawPolygon(mPaint, canvas, 0xff00ff00, new float[] {mWidth / 2, mWidth / 2 - a / 2, mWidth / 2 + a / 2}, new float[] {mHeight, mHeight / 2, mHeight / 2},false);
					break;
				}
			}
			mPaint.setStrokeWidth(APP.dpi);
			float b = (float) (a * 0.08);
			float c = (float) (a * 0.08/Math.sqrt(3));
			float d = (float) (a * 0.14*Math.sqrt(3));
			float[] ax = {mWidth / 2 - a+b, mWidth / 2 - b, mWidth / 2 - a / 2};
			float[] ay = {c, c, mHeight / 2 - c * 2};
			Scrambler.drawPolygon(mPaint, canvas, colors[0], ax, ay,true);
			ax = new float[] {mWidth / 2 + b, mWidth / 2 + a - b, mWidth / 2 + a / 2};
			ay = new float[] {c, c, mHeight / 2 - c * 2};
			Scrambler.drawPolygon(mPaint, canvas, colors[2], ax, ay,true);
			ax = new float[] {mWidth / 2, mWidth / 2 - a / 2 + b, mWidth / 2 + a / 2 - b};
			ay = new float[] {c * 2, mHeight / 2 - c, mHeight / 2 - c};
			Scrambler.drawPolygon(mPaint, canvas, colors[1], ax, ay,true);
			ax = new float[] {mWidth / 2, mWidth / 2 - a / 2 + b, mWidth / 2 + a / 2 - b};
			ay = new float[] {mHeight-c * 2, mHeight / 2 + c, mHeight / 2 + c};
			Scrambler.drawPolygon(mPaint, canvas, colors[3], ax, ay,true);
			mPaint.setStrokeWidth(1);
			ax = new float[] {mWidth / 2 - a * 0.64f, mWidth / 2 - a * 0.36f, mWidth / 2 - a * 0.64f, mWidth / 2 - a * 0.36f, mWidth / 2 - a * 0.22f, mWidth / 2 - a * 0.78f};
			ay = new float[] {c, c + 2 * d, c + 2 * d, c, c + d, c + d};
			drawTriangle(mPaint, canvas, ax, ay);
			ax = new float[] {mWidth / 2 + a * 0.64f, mWidth / 2 + a * 0.36f, mWidth / 2 + a * 0.64f, mWidth / 2 + a * 0.36f, mWidth / 2 + a * 0.22f, mWidth / 2 + a * 0.78f};
			ay = new float[] {c, c + 2 * d, c + 2 * d, c, c + d, c + d};
			drawTriangle(mPaint, canvas, ax, ay);
			ax = new float[] {mWidth / 2 + a * 0.14f, mWidth / 2 - a * 0.14f, mWidth / 2 - a * 0.28f, mWidth / 2 + a * 0.28f, mWidth / 2 + a * 0.14f, mWidth / 2 - a * 0.14f};
			ay = new float[] {mHeight / 2 - c - 2 * d, mHeight / 2 - c, mHeight / 2 - c - d, mHeight / 2 - c - d, mHeight / 2 - c, mHeight / 2 - c - 2 * d};
			drawTriangle(mPaint, canvas, ax, ay);
			ax = new float[] {mWidth / 2 + a * 0.14f, mWidth / 2 - a * 0.14f, mWidth / 2 - a * 0.28f, mWidth / 2 + a * 0.28f, mWidth / 2 + a * 0.14f, mWidth / 2 - a * 0.14f};
			ay = new float[] {mHeight / 2 + c + 2 * d, mHeight / 2 + c, mHeight / 2 + c + d, mHeight / 2 + c + d, mHeight / 2 + c, mHeight / 2 + c + 2 * d};
			drawTriangle(mPaint, canvas, ax, ay);
			break;
		}
		//mPaint.setStyle(Style.STROKE);
		//mPaint.setStrokeWidth(APP.dpi);
		//mPaint.setColor(getResources().getColor(R.color.colorAccent));
		//canvas.drawRoundRect(new RectF(mWidth * 0.66f, mHeight*0.81f, mWidth * 0.98f, mHeight*0.98f), mWidth * 0.03f, mWidth * 0.03f, mPaint);
		//mPaint.setColor(0xff000000);
		//mPaint.setStyle(Style.FILL);
		//canvas.drawText(context.getResources().getString(R.string.scheme_reset), mWidth * 0.82f, mHeight*0.92f, mPaint);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int inPosition = 0;
		switch (cubeType) {
		case 1:
		case 3:
		case 4: inPosition=inCube(x, y); break;
		case 2: inPosition=inPrym(x, y); break;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downInPosition = inPosition;
			//downInReset = inReset(x, y);
		case MotionEvent.ACTION_MOVE:
			if (downInPosition > 0 && inPosition != downInPosition) downInPosition = 0;
			//if (downInPosition < 0 && downInPosition >= 0) downInPosition = 0;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (downInPosition > 0) {
				final int dip = downInPosition;
				new ColorPickerDialog(context, new int[] {colors[dip - 1]}, defColors, true, new OnColorPickerListener() {
					@Override
					public void onColorCancel(ColorPickerDialog dialog) { }

					@Override
					public void onColorChange(ColorPickerDialog dialog, int[] color) { }

					@Override
					public void onColorConfirm(ColorPickerDialog dialog, int[] color) {
						colors[dip - 1] = color[0];
						invalidate();
						switch (cubeType) {
							case 1:
								context.setPref("csn" + dip, color[0]);
								break;
							case 2:
								context.setPref("csp" + dip, color[0]);
								break;
							case 3:
								context.setPref("csq" + dip, color[0]);
								break;
							case 4:
								context.setPref("csw" + dip, color[0]);
								break;
						}
					}

					@Override
					public void onColorReset(ColorPickerDialog dialog, int[] color) {
						colors[dip - 1] = color[dip - 1];
						invalidate();
						switch (cubeType) {
							case 1:
								context.delPref("csn" + dip);
								break;
							case 2:
								context.delPref("csp" + dip);
								break;
							case 3:
								context.delPref("csq" + dip);
								break;
							case 4:
								context.delPref("csw" + dip);
								break;
						}
					}
				}).show();
			}
//			else if (downInReset && inReset(x, y)) {
//				int[] color;
//				switch (cubeType) {
//				case 1:
//				case 4:
//					color = new int[] {0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff9900};
//					for (int i=1; i<7; i++) {
//						if (cubeType == 1) context.delPref("csn" + i);
//						else context.delPref("csw" + i);
//					}
//					break;
//				case 3:
//					color = new int[] {0xffffffff, 0xffff9900, 0xff009900, 0xffffff00, 0xffff0000, 0xff0000ff};
//					for (int i=1; i<7; i++) {
//						context.delPref("csq" + i);
//					}
//					break;
//				case 2:
//					color = new int[] {0xffff0000, 0xff009900, 0xff0000ff, 0xffffff00};
//					for (int i=1; i<5; i++) context.delPref("csp"+i);
//					break;
//				default:
//					color = new int[6];
//					break;
//				}
//				colors = color;
//				invalidate();
//			}
			downInPosition = 0;
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
		if (y > 0 && y < mHeight / 3) {
			if (x > mWidth / 4 && x < mWidth / 2) return 4;
		}
		if (y > mHeight / 3 && y < mHeight * 2 / 3) {
			if (x > 0 && x<mWidth/4) return 6;
			if (x > mWidth / 4 && x < mWidth / 2) return 5;
			if (x > mWidth / 2 && x < mWidth * 3 / 4) return 3;
			if (x > mWidth * 3 / 4 && x < mWidth) return 2;
		}
		if (y > mHeight * 2 / 3 && y < mHeight) {
			if (x > mWidth / 4 && x < mWidth / 2) return 1;
			if (y > mHeight * 4 / 5 && x > mWidth * 2 / 3 && x < mWidth) return -1;
		}
		return 0;
	}
	
	private int inPrym(float x, float y) {
		if (y > 0 && y < mHeight / 2) {
			if (x > 0 && x < mWidth / 3) return 1;
			if (x > mWidth / 3 && x < mWidth * 2 / 3) return 2;
			if (x > mWidth * 2 / 3 && x < mWidth) return 3;
		}
		if (y > mHeight / 2 && y<mHeight) {
			if (x > mWidth / 3 && x < mWidth * 2 / 3) return 4;
			if (y > mHeight * 4 / 5 && x > mWidth * 2 / 3 && x < mWidth) return -1;
		}
		return 0;
	}
	
//	private boolean inReset(float x, float y) {
//		return x > mWidth * 0.64 && y > mHeight * 0.81;
//	}
	
	private void drawTriangle(Paint p, Canvas c, float[] arx, float[] ary) {
		p.setColor(0xff000000);
		Path path = new Path();
		path.moveTo(arx[0], ary[0]);
		for (int idx=1; idx<arx.length; idx++)
			path.lineTo(arx[idx], ary[idx]);
		path.close();
		c.drawPath(path, p);
	}

//	public void reset() {
//		switch (cubeType) {
//			case 1:
//			case 4:
//				colors = new int[] {0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff9900};
//				break;
//			case 3:
//				colors = new int[] {0xffffffff, 0xffff9900, 0xff009900, 0xffffff00, 0xffff0000, 0xff0000ff};
//				break;
//			case 2:
//				colors = new int[] {0xffff0000, 0xff009900, 0xff0000ff, 0xffffff00};
//				break;
//		}
//	}
}
