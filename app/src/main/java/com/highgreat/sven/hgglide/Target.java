package com.highgreat.sven.hgglide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.highgreat.sven.hgglide.request.Request;

import java.lang.ref.WeakReference;

public class Target {

    private static int maxDisplayLength = -1;
    private ImageView view;
    private Request request;
    private LayoutListener layoutListener;
    private SizeReadyCallback cb;

    public void onLoadFailed(Drawable error){
        view.setImageDrawable(error);
    }

    public void cancel(){
        ViewTreeObserver observer = view.getViewTreeObserver();
        if(observer.isAlive()){
            observer.removeOnPreDrawListener(layoutListener);
        }
        layoutListener = null;
        cb = null;
    }

    public void getSize(SizeReadyCallback cb){
        int currentWidth = getTargetWidth();
        int currentHeight = getTargetHeight();
        if(currentHeight > 0 && currentWidth > 0){
            cb.onSizeReady(currentWidth,currentHeight);
            return;
        }

        this.cb = cb;
        if(layoutListener == null){
            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
            layoutListener = new LayoutListener(this);
            viewTreeObserver.addOnPreDrawListener(layoutListener);
        }
    }

    public void onLoadStarted(Drawable placeholderDrawable){
        view.setImageDrawable(placeholderDrawable);
    }

    public void onResourceReady(Bitmap bitmap){
        view.setImageBitmap(bitmap);
    }

    public void setRequest(Request request){
        this.request = request;
    }

    public interface SizeReadyCallback{
        void onSizeReady(int width,int height);
    }

    public Target(ImageView imageView){
        this.view = imageView;
    }

    /**
     * 通过ViewTreeObserver .addOnPreDrawListener来获得宽高，
     * 在执行onDraw之前已经执行了onLayout()和onMeasure()，
     * 可以得到宽高了，当获得正确的宽高后，请移除这个观察者，否则回调会多次执行
     */
    private static final class LayoutListener implements ViewTreeObserver.OnPreDrawListener{

        private final WeakReference<Target> targetRef;

        LayoutListener(Target sizeDeterminer){
            targetRef = new WeakReference<>(sizeDeterminer);
        }

        @Override
        public boolean onPreDraw() {
            Target target = targetRef.get();
            if(target != null){
                target.checkCurrentDimens();
            }
            return true;
        }
    }

    private int getTargetHeight(){
        int verticalPadding = view.getPaddingTop() +view.getPaddingBottom();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int layoutParamSize = layoutParams != null ? layoutParams.height : 0;
        return getTargetDimen(view.getHeight(),layoutParamSize,verticalPadding);
    }

    private int getTargetWidth(){
        int horizontalPaddig = view.getPaddingLeft() + view.getPaddingRight();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int layoutParamSize = layoutParams != null?layoutParams.width : 0;
        return getTargetDimen(view.getWidth(),layoutParamSize,horizontalPaddig);
    }

    private int getTargetDimen(int viewSize ,int paramSize,int paddingSize){
        int adjustedParamSize = paramSize - paddingSize;
        if(adjustedParamSize > 0){
            return adjustedParamSize;
        }

        int adjustedViewSize = viewSize - paddingSize;
        if(adjustedViewSize > 0){
            return adjustedViewSize;
        }
        if(!view.isLayoutRequested() && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT){
            return getMaxDisplayLength(view.getContext());
        }
        return 0;
    }

    private static int getMaxDisplayLength(Context context){
        if(maxDisplayLength == -1){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point displayDimensions = new Point();
            display.getSize(displayDimensions);
            maxDisplayLength = Math.max(displayDimensions.x,displayDimensions.y);
        }
        return maxDisplayLength;
    }


    void checkCurrentDimens(){
        if(null == cb){
            return;
        }

        int currentWidth = getTargetWidth();
        int currentHeight = getTargetHeight();
        if(currentHeight <= 0 && currentWidth <= 0){
            return;
        }
        cb.onSizeReady(currentWidth,currentHeight);
        cancel();
    }


}
