package com.thaivan.bay.branch.scan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thaivan.bay.branch.CamfaceActivity;
import com.thaivan.bay.branch.Utility;
import com.thaivan.bay.branch.util.BitmapHandler;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FaceCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = this.getClass().getName();

    private Context m_context;
    private boolean isCameraOn = false;
    // bitmap crop status
    private int isDone = 0;
    // preview : pic => 720 : 960 = 1944 : 2592
    private static int preview_width = 720;
    private static int preview_height = 960;

    // before rotate size
    private static int picture_width = 2592;
    private static int picture_height = 1944;

    private double rate = 2.7;

    // person photo = 3.5*4.5 so 560*720
    private int view_left = 80;
    private int view_top = 150;
    private int view_right = 640;
    private int view_bottom = 870;
/*
    // A4 size = 210*297 -> 678 * 960
    private int view_left = 21;
    private int view_top = 0;
    private int view_right = 699;
    private int view_bottom = 960;
*/
    // 좌우측 좌표 반올림 처리
    private int viewCrop_x = (int) Math.round((view_left * rate));
    private int viewCrop_w = (int) Math.round(((view_right - view_left) * rate));
    private int viewCrop_y = (int) Math.round((view_top * rate));
    private int viewCrop_h = (int) Math.round(((view_bottom - view_top) * rate));

    private Camera mCamera;
    public List<Camera.Size> prSupportedPreviewSizes;
    private Camera.Size prPreviewSize;

    public FaceCameraPreview(Context context) {
        super(context);
        m_context = context;
        this.setWillNotDraw(false);

    }

    public FaceCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
        this.setWillNotDraw(false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            setAutoFocusArea(mCamera, (int) x, (int) y, 128, true, new Point(preview_width, preview_height));
        }

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                //return true;
            }
        });
        return true;
    }

    public int is_Done() {
        int done = 0;
        done = isDone;
        isDone = 0;
        return done;
    }

    public boolean takePhoto() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    // data[] 로 넘어온 데이터를 bitmap으로 변환
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if(Utility.IsDebug){
                        Log.d(TAG, "bmp size : with " + bmp.getWidth() + " height : " + bmp.getHeight());
                        Log.d(TAG, "crop size : with " + viewCrop_x + " , " + viewCrop_w + " height : " + viewCrop_y + " ," + viewCrop_h);
                    }
                    // 이미지 자르기
                    Bitmap CapImg;
                    if(CamfaceActivity.flag != 1)
                        CapImg = Bitmap.createBitmap(bmp, viewCrop_x, viewCrop_y, viewCrop_w, viewCrop_h);
                    else {
                        Matrix matrix = new Matrix();
                        matrix.setScale(-1,1);
                        int rotate = 90;
                        matrix.postRotate(rotate);
                        CapImg = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    CapImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    byte[] aaa = android.util.Base64.encode(byteArray, android.util.Base64.NO_WRAP);
                    makeFile(new String (aaa), "pic_photo");

                    String FilePath = "/storage/emulated/0/Pictures/";
                    File file = new File(FilePath + "pic_photo.bmp");
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        CapImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    BitmapHandler.setBitmap(CapImg);

                    isDone = 1;
                    // bitmap 자원해제
                    if (bmp.isRecycled())
                        bmp.recycle();

                    if (CapImg.isRecycled())
                        CapImg.recycle();

                }
            });
            return true;
        } else {
            return false;
        }
    }

    private void makeFile(String data, String fileName) {
        String str = data;

        File saveFile = new File("/sdcard/oversea_ct/bay_branch"); // 저장 경로

        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            File existFile = new File("/sdcard/oversea_ct/bay_branch/" + fileName + ".txt");
            existFile.delete();

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/"+fileName+".txt", true));
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open(CamfaceActivity.flag);
        prSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        // Surface가 생성되었으니 프리뷰를 어디에 띄울지 지정해준다. (holder 로 받은 SurfaceHolder에 뿌려준다.
        try {

            Camera.Parameters parameters = mCamera.getParameters();
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            parameters.setPictureSize(picture_width, picture_height);
//            Log.d(TAG, "setting camera preview ");

            SharedPreferences preferences = m_context.getSharedPreferences("KTB_branch", MODE_PRIVATE);
            int flash_flag = preferences.getInt("flash_mode", 0);

            String stat = "off";
            if(flash_flag == 1 ) stat = "torch";
            parameters.setFlashMode(stat);

            if(CamfaceActivity.flag != 1)
                mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            isCameraOn = true;
            mHandler.sendEmptyMessage(0);
        } catch (IOException e) {
//            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // 프리뷰 제거시 카메라 사용도 끝났다고 간주하여 리소스를 전부 반환한다
        if (mCamera != null) {
            isCameraOn = false;
            mHandler.removeMessages(0);

            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // 프리뷰를 회전시키거나 변경시 처리를 여기서 해준다.
        // 프리뷰 변경시에는 먼저 프리뷰를 멈춘다음 변경해야한다.
        if (holder.getSurface() == null) {
            // 프리뷰가 존재하지 않을때
            return;
        }

        // 우선 멈춘다
        try {
            isCameraOn = false;
            mHandler.removeMessages(0);
            mCamera.stopPreview();

        } catch (Exception e) {
            // 프리뷰가 존재조차 하지 않는 경우다
            return;
        }

        // 프리뷰 변경, 처리 등을 여기서 해준다.
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        prPreviewSize = getOptimalPreviewSize(prSupportedPreviewSizes, preview_width, preview_height);

        parameters.setPreviewSize(prPreviewSize.width, prPreviewSize.height);
        mCamera.setParameters(parameters);
        // 새로 변경된 설정으로 프리뷰를 재생성한다
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            isCameraOn = true;
            mHandler.sendEmptyMessage(0);
        } catch (Exception e) {
//            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(preview_width, preview_height);

        if (prSupportedPreviewSizes != null) {
            prPreviewSize = getOptimalPreviewSize(prSupportedPreviewSizes, preview_width, preview_height);

        }
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - h) < minDiff) {

                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }

        return optimalSize;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas) {
        //  get Screen size
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

        Paint paint = new Paint();
        Paint paint2 = new Paint();

        /*  crop guide line */
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.parseColor("White"));
        paint.setColor(0xFFFFD500);

        canvas.drawRoundRect(view_left, view_top, view_right, view_bottom, 10, 10, paint);


        // set color for shadow
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.BLACK);
        paint2.setAlpha(204);

        // left shadow
        canvas.drawRect(0, view_top, view_left, view_bottom, paint2);
        // right shadow
        canvas.drawRect(view_right, view_top, metrics.widthPixels, view_bottom, paint2);
        // top shadow
        canvas.drawRect(0, 0, metrics.widthPixels, view_top, paint2);
        // bottom shadow
        canvas.drawRect(0, view_bottom, metrics.widthPixels, metrics.heightPixels, paint2);
//        if (IsDebug.LOG)
//            Log.d(TAG, "left : " + view_left + " top : " + view_top + " right : " + view_right + " bottom : " + view_bottom + "total w : " + metrics.widthPixels + " h : " + metrics.heightPixels);
        //super.onDraw(canvas);
    }

    public int get_flash_stat() {
        int is_on = 0;
        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters.getFlashMode().equals("off")) {
            is_on = 0;
        } else if (parameters.getFlashMode().equals("torch")) {
            is_on = 1;
        }
        return is_on;
    }

    public void set_flash_stat(String stat) {

        Camera.Parameters parameters = mCamera.getParameters();

        parameters.setFlashMode(stat);

        mCamera.setParameters(parameters);

    }

    private void setAutoFocusArea(Camera camera, int posX, int posY, int focusRange, boolean flag, Point point) {

        if (posX < 0 || posY < 0) {
            setArea(camera, null);
            return;
        }

        int touchPointX;
        int touchPointY;
        int endFocusY;
        int startFocusY;

        if (!flag) {
            /* Camera.setDisplayOrientation()을 이용해서 영상을 세로로 보고 있는 경우. */
            touchPointX = point.y >> 1;
            touchPointY = point.x >> 1;

            startFocusY = posX;
            endFocusY = posY;
        } else {
            /* Camera.setDisplayOrientation()을 이용해서 영상을 가로로 보고 있는 경우. */
            touchPointX = point.x >> 1;
            touchPointY = point.y >> 1;

            startFocusY = posY;
            endFocusY = point.x - posX;
        }

        float startFocusX = 1000F / (float) touchPointY;
        float endFocusX = 1000F / (float) touchPointX;

        startFocusX = (int) (startFocusX * (float) (startFocusY - touchPointY)) - focusRange;
        startFocusY = (int) (endFocusX * (float) (endFocusY - touchPointX)) - focusRange;
        endFocusX = startFocusX + focusRange;
        endFocusY = startFocusY + focusRange;

        if (startFocusX < -1000)
            startFocusX = -1000;

        if (startFocusY < -1000)
            startFocusY = -1000;

        if (endFocusX > 1000) {
            endFocusX = 1000;
        }

        if (endFocusY > 1000) {
            endFocusY = 1000;
        }

        Rect rect = new Rect((int) startFocusX, (int) startFocusY, (int) endFocusX, (int) endFocusY);
        ArrayList<Camera.Area> arraylist = new ArrayList<Camera.Area>();
        arraylist.add(new Camera.Area(rect, 1000));

        setArea(camera, arraylist);
    }

    private void setArea(Camera camera, List<Camera.Area> list) {
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        if (parameters.getMaxNumFocusAreas() > 0) {
            parameters.setFocusAreas(list);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            parameters.setMeteringAreas(list);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(parameters);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isCameraOn) {
                try {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            //return true;
                        }
                    });
                } catch (Exception e) {
//                    if (IsDebug.LOG)
//                        Log.d(TAG, "auto focus fail " + e.getMessage());
                }
            }
            mHandler.sendEmptyMessageDelayed(0, 5000);
        }
    };
}
