package com.psnsw.scratchdemo;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MyScratchCard extends ImageView implements View.OnTouchListener{
	private int mWidth = 0;
	private int mHeight = 0;
	private Paint   mPaint;
//  private Paint nPaint;
	Bitmap bitmap;
	Canvas pcanvas ;
	int x = 0;
	int y =0;
	int r =0;

	private int imageX;
	private int imageY;
	private int[] pixels;
	private Random rnd = new Random();
	private int lastStatus;
	private int status;
	private float circleX;
	private float circleY;
	private float circleR = 30;
	private float lastX;
	private float lastY;

	public MyScratchCard(Context context) {
		super(context);
	}


	public MyScratchCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public MyScratchCard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	private void init() {
		Log.v("Panel", "STROKE");

		setFocusable(true);
		setBackgroundColor(Color.TRANSPARENT);
		mPaint = new Paint();
		mPaint.setAlpha(0);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeCap(Paint.Cap.BUTT);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//.Mode.DST_IN));
		mPaint.setAntiAlias(false);
		// getting image from resources
		Bitmap bm1 = BitmapFactory.decodeResource(getResources(),R.drawable.scratch);
		bm1 = Bitmap.createScaledBitmap(bm1, mWidth, mHeight, true);


		// converting image bitmap into mutable bitmap
		bitmap =  Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), Config.ARGB_8888);
		pcanvas = new Canvas();
		pcanvas.setBitmap(bitmap);                   // drawXY will result on that Bitmap
		pcanvas.drawBitmap(bm1,0,0,null);

		pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		this.setOnTouchListener(this);
	}
		@Override
			protected void onDraw(Canvas canvas) {

			//pcanvas.drawRect(cBK, nPaint);
			// draw a circle that is  erasing bitmap
//			pcanvas.drawCircle(x, y, r, mPaint);
//			canvas.drawBitmap(bitmap, 0, 0,null);

//			drawCircle(bitmap, imageX, imageY, 20);
			switch(status) {
			case MotionEvent.ACTION_UP:
				switch(lastStatus) {
					case MotionEvent.ACTION_MOVE:
						if (Math.pow(circleX - lastX, 2) + Math.pow(circleY - lastY, 2) <= Math.pow(circleR, 2) / 2) {
							drawCircle(bitmap, (int)circleX - imageX, (int)circleY - imageY, (int)circleR);
						}
						else {
							drawLine(bitmap, circleX - imageX, circleY - imageY, lastX - imageX,lastY - imageY, (int)circleR);
						}
						break;
					case MotionEvent.ACTION_DOWN:
						drawCircle(bitmap, (int)circleX - imageX, (int)circleY - imageY, (int)circleR);
						break;
				}
				boolean result = isScratched(bitmap, 10);
				if (result) Log.v(VIEW_LOG_TAG, ">> SCRATCHED");
				break;
			case MotionEvent.ACTION_DOWN:
				drawCircle(bitmap, (int)circleX - imageX, (int)circleY - imageY, (int)circleR);
				break;
			case MotionEvent.ACTION_MOVE:
				if (Math.pow(circleX - lastX, 2) + Math.pow(circleY - lastY, 2) <= Math.pow(circleR, 2) / 2) {
					drawCircle(bitmap, (int)circleX - imageX, (int)circleY - imageY, (int)circleR);
				}
				else {
					drawLine(bitmap, (int)circleX - imageX, (int)circleY - imageY, (int)lastX - imageX, (int)lastY - imageY, (int)circleR);
				}
				break;
			}
			canvas.drawBitmap(bitmap, 0, 0, null);
			super.onDraw(canvas);
		}

		private void drawCircle(Bitmap bitmap, int centerX, int centerY,int r) {
			int h = bitmap.getHeight();
			int w = bitmap.getWidth();
			bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
			drawCircle(pixels, centerX, centerY, r, h, w, 0);
			try {
				bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void drawCircle(int[]pixels, int centerX, int centerY, int r, int h, int w, int color) {
			int x;
			int y;
			for (x = Math.max(centerX - r, 0); x <= centerX + r && x < w; x++) {
				for (y = Math.max(centerY - r, 0); y <= centerY + r && y < h; y++) {
					if (Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2) <= Math.pow(r * 3 / 4 + (rnd.nextInt() % ((float)r / 4)), 2)) {
						pixels[x + y * w] = color;
					}
				}
			}
		}

		private void drawLine(Bitmap bitmap, float x1, float y1, float x2, float y2, int r) {
			int h = bitmap.getHeight();
			int w = bitmap.getWidth();
			int x;
			int y;
			int xLimit;
			int yLimit;
			int xInit;
			int yInit;


			xLimit = (int) Math.min(Math.max(x1, x2) + r, w - 1);
			yLimit = (int)Math.min(Math.max(y1, y2) + r, h - 1);
			xInit = (int)Math.max(Math.min(x1, x2) - r, 0);
			yInit = (int)Math.max(Math.min(y1, y2) - r, 0);
			bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

			for (x = xInit; x <= xLimit; x++) {
				for (y = yInit; y <= yLimit; y++) {
					double a1;
					double a2;
					double b1;
					double b2;
					double x3;
					double y3;

					if (x2 - x1 == 0) {
						x3 = x1;
						y3 = y;
					}
					else if (y2 - y1 == 0) {
						x3 = x;
						y3 = y1;
					}
					else {
						a1 = (y2 - y1)/(x2 - x1);
						a2 = - 1 / a1;
						b1 = y1 - a1 * x1;
						b2 = y - a2 * x;
						x3 = (b2 - b1)/(a1 - a2);
						y3 = a1 * x3 + b1;
					}
					if (Math.pow(x3 - x, 2) + Math.pow(y3 - y, 2) <= Math.pow(r * 3 / 4 + (rnd.nextInt() % (r / 4)), 2)) {
						int tmpPixel = pixels[x + y * w];
						pixels[x + y * w] = 0;
						if (tmpPixel != 0) {
							// 消した箇所の色を適当に散らす
							int x4;
							int y4;

							if (rnd.nextInt() / 2 == 1) {
								x4 = Math.min(Math.max((int) (x - (Math.abs(y1 - y2) + r) * 4), xInit), xLimit);
								y4 = Math.min(Math.max((int) (y - (Math.abs(x1 - x2) + r) * 4), yInit), yLimit);
							}else {
								x4 = Math.min(Math.max((int) (x + (Math.abs(y1 - y2) + r) * 4), xInit), xLimit);
								y4 = Math.min(Math.max((int) (y + (Math.abs(x1 - x2) + r) * 4), yInit), yLimit);
							}
							drawCircle(pixels, x4, y4, 1, h, w, tmpPixel);
						}
					}
				}
			}

			try {
				bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			lastStatus = status;
			status = event.getAction();
			switch(status) {
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					lastX = circleX;
					lastY = circleY;
					circleX = event.getX();
					circleY = event.getY();
					break;
			}
			invalidate();

			return true;
		}

		@Override
	public boolean onTouchEvent(MotionEvent event) {

		// set paramete to draw circle on touch event
		imageX = (int) event.getX();
		imageY = (int) event.getY();

		// Atlast invalidate canvas
		invalidate();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO 自動生成されたメソッド・スタブ
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		Log.v("", String.format("w %d h %d", mWidth, mHeight));
		this.setMeasuredDimension(mWidth, mHeight);
		init();
	}

	private boolean isScratched(Bitmap bitmap, int scratchedPercent) {
		
		boolean result = false;
		int count = 0;//Counter to count pixels
		int c;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		int size = height * width;
		//Looping over all pixels
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
			c = bitmap.getPixel(i, j);//Get the current pixel.

			if (c == Color.TRANSPARENT) {//Checking pixel
				count++;//increase counter
				//Do what ever you want, you have got the pixel that have
				// R = 153 AND G = 184 AND B = 226
			}
			}
		}
		double percent = (count *100/ size);
		Log.v(VIEW_LOG_TAG, String.format(">> isScratched was called count="+ count));
		Log.v(VIEW_LOG_TAG, String.format(">> isScratched was called size="+ size));
		Log.v(VIEW_LOG_TAG, String.format(">> isScratched was called percent="+ percent));
		if ( percent >= scratchedPercent) {
			result = true;
		}
		return result;
	}
}
