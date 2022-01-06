package com.thaivan.bay.branch.scan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.thaivan.bay.branch.Utility;

public class CameraShow extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback
{
    private final String TAG = this.getClass().getName();
    private int isDone =0;
    private boolean isCameraOn = false;
    //Thaiid size = 85+54 so 680*432
//    private int view_left = 20;
//    private int view_top = 240;
//    private int view_right = 700;
//    private int view_bottom = 672;

//    private int view_left = 70;
//    private int view_top = 268;
//    private int view_right = 650;
//    private int view_bottom = 1028;

//    private int view_left = 20;
//    private int view_top = 360;
//    private int view_right = 700;
//    private int view_bottom = 790;

    private int view_left = 145;
    private int view_top = 235;
    private int view_right = 575;
    private int view_bottom = 905;

    private int viewCrop_x;
    private int viewCrop_y;
    private int viewCrop_w;
    private int viewCrop_h;

    private int img_flag;
    private int flash_mode;
    private Camera camera;
    @SuppressWarnings("unused")
    private Context context;
    private Paint paint       = new Paint();
    private Paint paint2       = new Paint();

    @SuppressWarnings("deprecation")
    public CameraShow(Context context, int flag, int flash)
    {
        super(context);
        img_flag = flag;
        flash_mode = flash;
        this.context = context;
        this.setWillNotDraw(false);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        viewCrop_x = view_left/2*3;
        viewCrop_y = view_top/2*3;
        viewCrop_w = (view_right-view_left)/2*3;
        viewCrop_h = (view_bottom-view_top)/2*3;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        camera = Camera.open(0);

        try {
            // set preview size and make any resize, rotate or
            // reformatting changes here
            Camera.Parameters parameters = camera.getParameters();
            camera.setDisplayOrientation(90); // Rotates Camera's preview 90 degrees
            parameters.setRotation(90);
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {

                if (size.width <= 1920) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
            String stat = "off";
            if(flash_mode == 1 ) stat = "torch";
            parameters.setFlashMode(stat);
            // Set parameters for camera
            camera.setParameters(parameters);

            camera.setPreviewDisplay(holder);

        } catch (Exception e) {
            if(Utility.IsDebug)
                Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        camera.startPreview();
        isCameraOn = true;
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        isCameraOn = false;
        mHandler.removeCallbacksAndMessages(null);

        camera.setPreviewCallback(null);
        camera.stopPreview();

        camera.release();
        camera = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
//        camera.autoFocus(new Camera.AutoFocusCallback() {
//            @Override
//            public void onAutoFocus(boolean success, Camera camera) {
//                //return true;
//            }
//        });
        return true;
    }

    public int get_flash_stat(){
        int is_on = 0;
        Camera.Parameters parameters = camera.getParameters();
        if(Utility.IsDebug)
            Log.d(TAG, "onClick: " + parameters.getFlashMode());
        if( parameters.getFlashMode().equals("off")){
            is_on = 0;
        }else if( parameters.getFlashMode().equals("torch")){
            is_on = 1;
        }
        return is_on;
    }

    public void  set_flash_stat(String stat){

        Camera.Parameters parameters = camera.getParameters();

        parameters.setFlashMode(stat);

        camera.setParameters(parameters);

    }
    public void CusTakePhoto (){
        camera.takePicture(null, null, this);
    }


    public int is_Done(){
        int done = isDone;
        isDone = 0;
        return done;
    }
    public void onPictureTaken(byte[] data, Camera camera)
    {
        // data[] 로 넘어온 데이터를 bitmap으로 변환
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        // 이미지 자르기
        Bitmap capImg = Bitmap.createBitmap(bmp, viewCrop_x, viewCrop_y, viewCrop_w, viewCrop_h);
        BitmapHandler.setBitmap(capImg);

        isDone = 1;

        bmp.recycle();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas)
    {
        Bitmap image1;
        //  Find Screen size first
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int screenWidth = metrics.widthPixels / 2;

        //  crop 영역 설정
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.parseColor("white"));
        paint.setColor(0xFFFFD500);
        canvas.drawRoundRect(view_left , view_top, view_right, view_bottom,10,10, paint);

//        if (img_flag == 1 ) {
//            // draw thai id png file
//            Resources r = context.getResources();
//            image1 = BitmapFactory.decodeResource(r, R.drawable.frame_cid);
//            Rect dst = new Rect(screenWidth - 70, (view_bottom - view_top)-50, screenWidth + 70, (view_bottom - view_top)+50);
//            canvas.drawBitmap(image1, null, dst, null);
//        }

        // 테두리 반투명
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.BLACK);
        paint2.setAlpha(204);

        // top
        canvas.drawRect(0 , 0, metrics.widthPixels, view_top, paint2);
        // left
        canvas.drawRect(0 , view_top, view_left, view_bottom, paint2);
        // right
        canvas.drawRect(view_right , view_top, metrics.widthPixels, view_bottom, paint2);
        // bottom
        canvas.drawRect(0 , view_bottom, metrics.widthPixels, metrics.heightPixels, paint2);
        if(Utility.IsDebug)
            Log.d(TAG, "left : " + view_left + " top : " + view_top  + " right : "  + view_right + " bottom : " + view_bottom + "total w : " + metrics.widthPixels + " h : " + metrics.heightPixels );
        //super.onDraw(canvas);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if( isCameraOn ) {
                try {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            //return true;
                        }
                    });
                } catch (Exception e) {
                    if(Utility.IsDebug)
                        Log.d(TAG,"auto focus fail " + e.getMessage());
                }
            }
            mHandler.sendEmptyMessageDelayed(0, 5000);

        }
    };
}
