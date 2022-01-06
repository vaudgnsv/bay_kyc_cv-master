package com.thaivan.bay.branch.blink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.thaivan.bay.branch.CardManager;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MLKitFacesAnalyzer implements ImageAnalysis.Analyzer {
    private static final String TAG = "MLKitFacesAnalyzer";
    private FirebaseVisionFaceDetector faceDetector;
    private TextureView tv;
    private ImageView iv;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint dotPaint, linePaint, guidePaint;
    private float widthScaleFactor = 1.0f;
    private float heightScaleFactor = 1.0f;
    private FirebaseVisionImage fbImage;
    private CameraX.LensFacing lens;

    private int view_left = 80;
    private int view_top = 150;
    private int view_right = 640;
    private int view_bottom = 870;
    private double rate = 2.7;

    private CameraListener cameraListener = null;
    private boolean detect = false;
    MLKitFacesAnalyzer(TextureView tv, ImageView iv, CameraX.LensFacing lens, CameraListener cameraListener) {
        this.tv = tv;
        this.iv = iv;
        this.lens = lens;
        this.cameraListener = cameraListener;
        //Init guide line
        bitmap = Bitmap.createBitmap(tv.getWidth(), tv.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        dotPaint = new Paint();
        dotPaint.setColor(Color.RED);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStrokeWidth(2f);
        dotPaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);
        guidePaint = new Paint();
        guidePaint.setColor(Color.WHITE);
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(10);
        canvas.drawRect(120, 200, 600, 840, guidePaint);
        iv.setImageBitmap(bitmap);
    }

    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        if (image == null || image.getImage() == null) {
            return;
        }
        int rotation = degreesToFirebaseRotation(rotationDegrees);
        fbImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);
        initDrawingUtils();

        initDetector();
        detectFaces();
    }

    private void initDrawingUtils() {
        widthScaleFactor = canvas.getWidth() / (fbImage.getBitmap().getWidth() * 1.0f);
        heightScaleFactor = canvas.getHeight() / (fbImage.getBitmap().getHeight() * 1.0f);
    }

    private void initDetector() {
        FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build();
        faceDetector = FirebaseVision
                .getInstance()
                .getVisionFaceDetector(detectorOptions);
    }

    private void detectFaces() {
        faceDetector
                .detectInImage(fbImage)
                .addOnSuccessListener(firebaseVisionFaces -> {
                    if (!firebaseVisionFaces.isEmpty()) {
                        processFaces(firebaseVisionFaces);
                    } else {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
                        guidePaint.setColor(Color.WHITE);
                        canvas.drawRect(120, 200, 600, 840, guidePaint);
                        iv.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(e -> Log.i(TAG, e.toString()));
    }

    private void savePicture() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        fbImage.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        byte[] aaa = android.util.Base64.encode(byteArray, android.util.Base64.NO_WRAP);
        makeFile(new String(aaa), "pic_photo");

        String FilePath = "/storage/emulated/0/Pictures/";
        File file = new File(FilePath + "pic_photo.bmp");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fbImage.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processFaces(List<FirebaseVisionFace> faces) {
//        for (FirebaseVisionFace face : faces) {
//            drawContours(face.getContour(FirebaseVisionFaceContour.FACE).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).getPoints());
//            drawContours(face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).getPoints());
//        }
        if(faces.get(0).getBoundingBox().left >= 120 && faces.get(0).getBoundingBox().top >= 20
        && faces.get(0).getBoundingBox().right <= 480 && faces.get(0).getBoundingBox().bottom <= 640){
//            savePicture();
            if(detect){
                cameraListener.onTakePicture();
            }else{
                detect = true;
                guidePaint.setColor(Color.YELLOW);
                canvas.drawRect(120, 200, 600, 840, guidePaint);
                iv.setImageBitmap(bitmap);
            }
        }else{
            detect = false;
            guidePaint.setColor(Color.WHITE);
            canvas.drawRect(120, 200, 600, 840, guidePaint);
            iv.setImageBitmap(bitmap);
        }
    }

    private void drawContours(List<FirebaseVisionPoint> points) {
        int counter = 0;
        for (FirebaseVisionPoint point : points) {
            if (counter != points.size() - 1) {
                canvas.drawLine(translateX(point.getX()),
                        translateY(point.getY()),
                        translateX(points.get(counter + 1).getX()),
                        translateY(points.get(counter + 1).getY()),
                        linePaint);
            } else {
                canvas.drawLine(translateX(point.getX()),
                        translateY(point.getY()),
                        translateX(points.get(0).getX()),
                        translateY(points.get(0).getY()),
                        linePaint);
            }
            counter++;
            canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), 6, dotPaint);
        }
    }

    private float translateY(float y) {
        return y * heightScaleFactor;
    }

    private float translateX(float x) {
        float scaledX = x * widthScaleFactor;
        if (lens == CameraX.LensFacing.FRONT) {
            return canvas.getWidth() - scaledX;
        } else {
            return scaledX;
        }
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270.");
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

    public interface CameraListener {
        void onTakePicture();
    }

}
