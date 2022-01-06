package com.thaivan.bay.branch.cv;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;
import com.thaivan.bay.branch.CardManager;
import com.thaivan.bay.branch.MainActivity;
import com.thaivan.bay.branch.MainApplication;
import com.thaivan.bay.branch.Preference;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class Dopa_SuccessActivity extends AppCompatActivity {
    private String data;
    private CardManager cardManager = null;
    private AidlPrinter printDev = null;
    private Bitmap bitmapOld = null;
    private LinearLayout slipLinearLayout;
    private Dialog dialogOutOfPaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dopa_sucess);
        Tool.setTitle("Dopa_SuccessActivity onCreate");

        setCustomToolbar();
        customDialogOutOfPaper();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            data = bundle.getString("data");
        }

        setPrint();
        doPrinting(getBitmapFromView(slipLinearLayout));
    }

    void setCustomToolbar() {
        Tool.setTitle("Dopa_SuccessActivity setCustomToolbar");

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.tool_bar));

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(mCustomView);
        }

        Toolbar toolbar = mCustomView.findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.dopa_success_title);
        TextView btn_back = toolbar.findViewById(R.id.toolbar_btn);
        btn_back.setVisibility(View.INVISIBLE);
    }

    private void setPrint() {
        Tool.setTitle("Dopa_SuccessActivity setPrint");

        cardManager = MainApplication.getCardManager();
        printDev = cardManager.getInstancesPrint();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View printFirst = inflater.inflate(R.layout.dopa_only_slip, null);
        slipLinearLayout = printFirst.findViewById(R.id.slipLinearLayout);
        TextView txt_firstName = printFirst.findViewById(R.id.txt_firstName);
        TextView txt_lastName = printFirst.findViewById(R.id.txt_lastName);
        TextView txt_diis = printFirst.findViewById(R.id.txt_diis);
        TextView txt_dateTime = printFirst.findViewById(R.id.txt_dateTime);
        TextView txt_tid = printFirst.findViewById(R.id.txt_tid);
        TextView txt_merchant = printFirst.findViewById(R.id.txt_merchant);


        try {
            Tool.setTitle("Dopa_SuccessActivity setPrint try");

            JSONObject jsonObject = new JSONObject(data);
            txt_dateTime.setText(jsonObject.getString("date"));
            txt_firstName.setText(jsonObject.getString("firstName"));
            txt_lastName.setText(jsonObject.getString("lastName"));
            txt_diis.setText(jsonObject.getString("diis"));
            String tid = Preference.getInstance(Dopa_SuccessActivity.this).getValueString(Preference.KEY_TERMINAL_ID);
            String mer_name = Preference.getInstance(Dopa_SuccessActivity.this).getValueString(Preference.KEY_MERCHANTNAME_ID);
            txt_tid.setText(tid);
            txt_merchant.setText(mer_name);
        } catch (JSONException e) {
            Tool.setTitle("Dopa_SuccessActivity setPrint catch");

            e.printStackTrace();
        }

        printFirst.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        printFirst.layout(0, 0, printFirst.getMeasuredWidth(), printFirst.getMeasuredHeight());
    }

    public Bitmap getBitmapFromView(View view) {
        Tool.setTitle("Dopa_SuccessActivity getBitmapFromView");

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888); // Cannot print slip here
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null){
            Tool.setTitle("Dopa_SuccessActivity getBitmapFromView bgDrawable 1");
            bgDrawable.draw(canvas);
        } else{
            Tool.setTitle("Dopa_SuccessActivity getBitmapFromView bgDrawable 2");
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public void doPrinting(Bitmap slip) {
        Tool.setTitle("Dopa_SuccessActivity doPrinting");

        bitmapOld = slip;
        new Thread() {
            @Override
            public void run() {
                try {
                    Tool.setTitle("Dopa_SuccessActivity doPrinting try");

                    printDev.initPrinter();
                    printDev.setPrinterGray(2);
                    //This interface is used for set the gray level of the printing
                    //MIN is 0,Max is 4
                    //The level bigger, the speed of print is smaller
                    printDev.printBmpFast(bitmapOld, Constant.ALIGN.CENTER, new AidlPrinterStateChangeListener.Stub() {
                        @Override
                        public void onPrintFinish() {
                            Tool.setTitle("Dopa_SuccessActivity doPrinting onPrintFinish");
                        }

                        @Override
                        public void onPrintError(int i) {
                            Tool.setTitle("Dopa_SuccessActivity doPrinting onPrintError");

                        }

                        @Override
                        public void onPrintOutOfPaper() {
                            Tool.setTitle("Dopa_SuccessActivity doPrinting onPrintOutOfPaper");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogOutOfPaper.show();
                                }
                            });

                        }
                    });

                    int ret = printDev.printBarCodeSync("asdasd");
                    Log.d("utility", "after call printData ret = " + ret);

                } catch (RemoteException e) {
                    Tool.setTitle("Dopa_SuccessActivity doPrinting catch 1");
                    e.printStackTrace();

                }catch (Exception e) {
                    Tool.setTitle("Dopa_SuccessActivity doPrinting catch 2");
                }
            }
        }.start();
    }

    private void customDialogOutOfPaper() {
        Tool.setTitle("Dopa_SuccessActivity customDialogOutOfPaper");

        dialogOutOfPaper = new Dialog(this);
        dialogOutOfPaper.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOutOfPaper.setContentView(R.layout.dialog_custom_printer);
        dialogOutOfPaper.setCancelable(false);
        dialogOutOfPaper.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOutOfPaper.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button okBtn = dialogOutOfPaper.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPrinting(bitmapOld);
                dialogOutOfPaper.dismiss();
            }
        });

    }

    public void Finish(View view) {
        Tool.setTitle("Dopa_SuccessActivity Finish");

        deleteFile();
        Intent intent = new Intent(Dopa_SuccessActivity.this, MainActivity.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
//        overridePendingTransition(0, 0);
//        finish();
    }

    private void deleteFile() {
        Tool.setTitle("Dopa_SuccessActivity deleteFile");

        File existFile = new File("/sdcard/oversea_ct/bay_branch/pic_photo.txt");
        existFile.delete();
    }
}
