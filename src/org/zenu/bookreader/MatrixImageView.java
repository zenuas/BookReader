package org.zenu.bookreader;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.ImageView;


public class MatrixImageView
	extends ImageView
	implements OnScaleGestureListener
{
	public MatrixImageView(Context context)
	{
		this(context, null);
	}
	
	public MatrixImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setScaleType(ScaleType.MATRIX);
		dector_ = new ScaleGestureDetector(context, this);
	}
	
	public void setAutoRotateAndFitScaleAndCenter()
	{
		Rect rc = getDrawable().getBounds();
		
		float view_height = getHeight();
		float view_width = getWidth();
		float image_height = rc.height();
		float image_width = rc.width();
		
		Matrix x = new Matrix();
		
		// ビューが縦表示で、画像の横幅が縦幅の1.5倍を超えていたら90度回転
		float scale = 1.0f;
		x.postTranslate(-image_width / 2, -image_height / 2);
		if(view_height > view_width && image_width > image_height * 1.5)
		{
			x.postRotate(90);
			scale = view_height / image_width;
		}
		else
		{
			float height_scale = view_height / image_height;
			float width_scale = view_width / image_width;
			
			scale = Math.min(height_scale, width_scale);
		}
		
		x.postScale(scale, scale);
		x.postTranslate(view_width / 2, view_height / 2);
		setImageMatrix(x);
		invalidate();
	}
	
	private ScaleGestureDetector dector_;
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		dector_.onTouchEvent(event);
		return(true);
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector arg0)
	{
		float scale = arg0.getScaleFactor();
		Matrix x = new Matrix(getMatrix());
		x.postScale(scale, scale);
		setImageMatrix(x);
		invalidate();
		return(false);
	}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector arg0)
	{
		return(true);
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector arg0)
	{
	}
}
