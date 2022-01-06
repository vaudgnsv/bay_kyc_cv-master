package com.thaivan.bay.branch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thaivan.bay.branch.FaceComp.ModelFaceResponse;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.util.BitmapHandler;
import com.thaivan.bay.branch.util.DemoHMAC;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceCompActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_comp);

        ImageView iv = findViewById(R.id.imageView);
        iv.setImageBitmap(BitmapHandler.getBitmap());

        TextView bt_send = findViewById(R.id.button_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String uuid = UUID.randomUUID().toString();

                    Date date = new Date();
                    String str_date =  toISO8601UTC(date);


                    String requestBody = "{\n" +
                            "  \"channel\" : \"0000\",\n" +
                            "  \"sourceImage\": \"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\n" +
                            "\",\n" +
                            "  \"destinationImage\": \"/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAB4AKADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1f4Ra2NS8cXt5eyZGpaHd28E/y/6Ne5b0OR9DnvyQHr2/wN4LuNKgMdlcxaX52LjXNVggP2u5vct/xL9N4/2e/oe5OfhD4ffFTwxpOoR6Vp0kV/dz4+3z5Js7b72O5HbPXnnn5d1fUek/Fe1ubZmuLyIRQ47n+8w9cdBz7Y7hif36lU9s358qTV7ayn3k9HdNNO1rq11d/wA7TwqalpZdFazT9/bWzva/8q91O9m39i+HdK0a1BjPm3UvH7+e4N33cHgEn+H9Rzhcn2jQrvw7pwj+0SWtrLkevq3vjoue/fIzjPwn4Q8aaz4y1CaPT9R/s/QbMH7drtx/otpbHJB/p79Vznmvsn4c3/gO0MUlpJdahdDPn67fwEY+Zhx/aZzjgHr3B4zmtsN7q5ujTXnpKX58t/mt3c5K1J25npK/L+L9f5W010e7Wq/Ov/gop/wRp8Aftl6Nrfxo+Alva/Dz4/QQf2gLDVc2vgb4pcy/6BqenbSfC/ijnP8AbJ6nH/CUn5T4nP8AFt8TPhf45+FvivxZ8NfiV4O1Twd428H315p/iTw1rlg1nq+nXmnbu/sN3vkkYyBn/Ui8F3Vu5jkg1KK6h45gn+1934xn/Z+vuRtz+bv/AAU0/wCCb/wl/wCCg9h/b+l6N/wg/wAV/BHg7xNp+h/FexgsLQ+ONZ/s9z4K8AeJNM2sfE+l/wBqEf8AE6z/AMSD+0AScljXzmZZDQzFPEO7tbo7LWS3s7dO6bad3Zte9kue18sX1bEP/hP6fJ1Euu9ktN029G1M/lY/Yl06+i/Z78FXFzZyx2l5qviW3sbj7OP9KH/CQsOuD6fhgdctX2xY6fH837v+7+m/377c/wDAjySDnp/gF8K9Q8D/APBN/wCCCa7oV1pnifSPiN8QLbVLG+tzZ6vpl7/wmPj3TNR0/UgecnU9OPP65NMt4xLE5ePygdvOD2Zh6/5BUE8ZPzGZ5Ksm+pYZXSlkPD8U+i96fT5XSu9Go3vAqjmCzKvmOJW39u8Qau9vt+r7O63u9G1rm29g3zDyv7vcesnv+fP0qRGjtXYy9yM/gzDrn3zweOmSTurXmz5BkjHp3yOCw9D6+/15zWDLFJGhklh6Y798t7H/ADgZIII82NZ2fNy+VlLz31fa/wA1q2mdtChJKUXotG3u/tW2fn5K27ute28FSRPrVtKPmCji35GM2DL1Jyck59jnHBJr95v+Ce/7SPhf9nL4S/HjxBemK+8T6wfAGn+CfDeB9s13V8fEFftud4caZor4fxE6nIwwyCQw/nx0vWLrQc6mmmy6pNB/pAsbe4/0y4yT/wAg3J/E89x1IzX118KNb8TCG2l1k2FpLLPYz3FlAONOyWAsP7R5/tM84/toZ9+ua78PjMNPDTwmK2bi99Gozm2mrN2k7K2+rimmuZ8SpV8DjpZjhu6tJpuzu4p6Ws9vnZJv3pG/+0n438R/Ej4sf8JH4xvHvdV1W31i+u0vJQqi7X+zVUKFxtVcAAD5QAqgYDGvmPU7P+0LHUkQeVb2NwLa4uOQeS4HTI6fzXock+tePpH1v4jaLEHlEf8AYfjLz7jJPGnHwyOhJ6jUfXoRgEgmsTxLZ2WneG2jgj8sz/Yfw/4mWnegwc47Zwc8E8tz8RZ6sZh3hopbWS7WlPS3M1ouXfT3l7qkmpdOVZN7Lm1bu79Vo3J3evu7NPdtye/KfH37RdnBb+GbFLdBF9m1VQPIGeun6wCefwPqMgH3+I206P7Sx8vIGMn6hx65/h+uGPoSfun9oX95oFl141Venf8A0DVh36DAz9OpyPm+NY4N91IR/sDGevLD+hP5+vP5TiU1jZLdXt8uepHv3lf5rV2bPu8vov6jJqzaTeml/eqau77S27t6u2vleuaTE6nCemDyO7ds85wQPQ55Ynnza/0eOIGMJnpgY6/M/ct2weDyQcdBXu2sWZUze2PpjL845ByDkjt06kV5jqFsQ5zz0445wX7gZ98ZycjPPWKFScbq6tppq1a720vZ3u103Tcm2epKhFK6S8tZW3e7v1VrfPqcRpenB7SMyR/LztIzzy2eCD3/AF65KiszWNAjkgYBPTbwT3kJ4x7HHPBI5ORXpGl6djT0Aj7L+e4j39/TrjPOa0LzTcwk/QY79SDk8HnC/juHOST9xltOVfDP8X6OaWmrd+uvR63bb+YxD9lXlZXS7vbWp5db+iSSs7s/SPwN8I9P0C08m08TeIyD0uJ7/Trr+J+40c9/03EHOa+o/AvhSOKA2Y1+TnA/f2/2s8Fh746j3+pLGvn3RYNVMB+1Xnl498YyST656dT6jg5zXpnhywuLtyo1XVCo/wCeE5Geh9SRzx9M8nBr9ZwnVf2e3sru6t70ltJvW3S+1rtKzPk63S6b+LXbrBdnvb70+7Z91+EbK5Oj6ZoI8RWEdjZzi4t4YNJ+yZvQT/yEv+Jz34x1PBA6Ma+r/Anhzw7OY5Ln+xrq5/57z2BtLwjnGNRGjn19fx5NfFvwi+FGnavPbSajqXiO5HGfs+u6ja55b698fXI7Amv0u8E/s7fDePw219e6t4t0qXIuIL7/AISzUeeWAx/aY/qeT33cexRq8ilF7NaP/wAC0eu3Z9LtN8rueLVtdxfWOjXe9RfjeP3+rPW/h7Z2Wjsbi3Ol2sXkD/UXw65fPb/Z6emOcA59ftdL17xTq1rbWWs6Npdr9usvIuPPN3eW5BY/8S3n6+vsfvV4z4Q+Gfh3y57fTvEWva1F8v8Ar5/tf2bl/bOePXOT9SfUbj4Z2XhW1PirSvH/APwjl/psAn+w65f50i5OWHJP/ILHr745IIxHtPL8f+AZck/5Zf8AgL/yPkX9vHwR4A+Fmn/FzWNds9LsfCd54c8TeP7iD/iX2lodZ1DT9X1LxFqGncZGq634n1D+2ffxFqAyTnLfwWad+2V+0VZa6NR1Hxv/AG3YxcXGh32haALS7sstj/kGaQcc/ru5JzX9pH/BbCztPjn8Fvg5qmla1dS2EOrX3g/xLfWE4tNI1L/hIdPfUR3P9qf2IfDufDnOM7uTjJ/i58T/ALIPxpt/GmpeF9C8I6prOl/blt9I8S/8S+00i5stzZ1DUh/bH/Er4Ax+POSAfj+OeI8RjFkeXPApf2XkLTS2z73qiS2SaS5XdKyXO2o3dvqOAeFsDltDPswxGYaZpn0f+EG//Ihte6aWmulkt03Z2jK/9CPwb8aeEdZ/Y28Y+IroRad4m+LF78GfEHgyxn/0y8PhnTz4/HiPpn+yv7E1T+ztI5ycbefvGvMbeC5MZNzJL2/1+cfef8ugJHbjJ5GTwVpE+leB/h54MnitRa+A/B2i+F4fIz/pQ05jg8n/AKCmo6hn13LwCCa6SePO8dQCvTj+Nunt6+g7jO4/H4nEuth8JrdPy7e18+rk2utkk21q/R5HbfqunS7Xfyvu9GtbpoXwdB/xUWnf8sgv27d172DAevBxz3HOfu17z4dup5ZWsbeWWGKHH26/wTn5mx+eePqeoxXinh61lGrRCOXyt0F7kcHoWI5yc5wMc+uQcEn23w0sUIjjjP7rj1I6t6nP8PQ+5yMc+Zia0Y4hq9763S00c2v1tvfq7am/sZe9o9LO946XlJ9+689Etb3bXxPNHZeOfBdvaxZgHg74l9SRj/iY+BR35OSSeDxwM85rl/Fkpk0OLIzu+x98btuo6Z19McdOowOMcz+LLor8QvCAJIJ8AfEvHGempfDMevP4+p5BJNYfiO5L6LET2n0bA45/4mOmg9ORjAJyT1965Kv1n3rJNX6t6xtNarte+72Se92+qpRdsEnZy6XfZxV7p32lK/zabau/mv4//vNEs/8AsKqPr/oGrep7gL16ZPUg18lwW/8ApsIz7fq3bn06ZP14+b61+O3/ACBLP/sLD/0318vWUf8AxMIj+vbqe2e4xnrjJ5ONx+bx/wDv1T/FD/05UPYwH/IvXy/9xnH61b/vLjrnj1/vPnt9PTnuSCB5vqNvxP8Au+5/nJ7/AOemc817DrsfL856Z7D7w/ngd+MHkk8+b3H3Lj8P/alefT+L+v8Ap4ejT+GXqZWkWWNNgHpsxx1+aQnvx24HqBk9Tems/wDRxj355P8AEfTp3znryc5ya1dFt8afa/h/7OPU/wCc8kgmtB4f3bBh6fzfk+hxz9SoJyBX6llMEsLbe1o26bJ+vT121urv5LGu1aTfaP8A7dr/AF3eumv6L+Gdas5I0ivrKKeLjv7sex/qOnXIzXtfhqPwsQXtpbqxkX/nvMbrqz9s9+x7Ad8nPimhabZxxoXOOnPXuwB4PHt1z6jOT7N4Ms11e+tdL0bSjfX9x/o8EHPYuPQnHT36jknJ/ScD7f37/Dpe/fmn+O1+vLa2p8fU3qWvfndrb/FP9L38reZ+h37NPg6DxdbTXmm+OtBtZNNvvs5sZ7+xtLzqw6EknoPf8mY/oboHw00otHH4m8Ry6l5P/LCAaheWh5Y+mhaUOV9TkE8EKSfzK8H/AAU0vwzZR674xuYrQQ4uPs9iGtc8tjn6jp04HIGSZPjr+0N8SNF8PeEPCPw5uLDw7qfjXxH4Y+H/AIb1bxJBqGqfZf8AhIdSfTP7Q/s3+2tDOq9Bj+29c45BPC59qhy3/f3t9m97b1b25tN7b63v1PNnt/s1+bz5vhvK++nr/wBu9T9xPDFj4RtrWOLTdGv7q1+X/l+0/SrP7zcg6YDyPb+8ckkCvVtCsvDR1COCyt9LurXUoL3SNW0v/iX3V39ivy/Ueh7/AIjLEGvw38M/ssftJ6foRsPGf7Y3xatYtSnGoX9h4V134f8AgmztuX/5lzS/g/rnoD/yPPXPUhs9r4b/AGNtM0i/i1XVP2uvjTp2qY+0QX3/AAmfiG8u7b5nH/IN1TWP7K/hGf8AiSfXAznqrZfQamvrq1VuZp2teaT95R1vy+Wyu2mRHZtq/eWj6tbN6dO+t73Wpuf8FPPD+pXXwp+Lfhm/Qn/hG/G/wz+KGhLgD7LoxbWPhvqP9nHI/wCJWdT8Q5GeRkgknmvx4+F+leFL/Qp7XWNOimu7KcEz+py+DgnoQeMehPJzX7I/tK+Avi5L8C/ENjqHj6H9oPw7p3g3WdH/AOElnsdP0n4t6ZZf2l4a8S6dqPiPTNLOhaT4p0vRdU8Naaf+JLoZ8RaBnWcnWiDu/E+zh1Dw1J5V5L9gtZ7c3B8/PYtjvxznHqc9SK/OeMcK6VbBO6uo6tJpSk+ZN2dmk5Lr72ibbaV/rOGqnNfVu/lbdTSfezSvbe+j0SPStd8MfDrTPhRY67pmpSx+Orv4jeJdHn0KCc/ZLXwZp2geGtR03UPvHrqmpaj7/wDEuXOScnyFLN5PKw2f3/P3R3Pt3GM9h6kk1prrOgXumxXFnqv267+3XlvPBBY6gfs9l84/tD+0s/2SANoPPqRkmu6S2+Gl5oqnw9c+PNQ1hYLH/kK6D4f8P6Rb3hLf2j/xMf7Y13VtT0vjg/2H68HbXwlTMMBRvrppda7qUk9Uo7aXd76q+ilf7KlkOOxn/MCtWtLX/mV9Ve916u7XxJnDaJb+XqbyjkGC7xz6F89/p16A8EkmvRNPfykwPvDqf++x3yOmPXqe43HB8GfAvxTqfiC88Tj453XgjFjeW8FhqvgM+NdIuuXH/IOPjHQ9Jx838+cEk/B/7Sn7O/7XHh06jqOhftEeN/Hmh3c4uINI/wCEK8QeCru4vdPLj/kXfDOs671HP9s633OCc4NeUs5wGNUlieu123fWcW7prpu02tUnaW/s4ngyvg8O1h8wWaqyfnb3o6adttdW07tJX+otV8ayar8VtL02fw1r3hv+zfhz4/uPtGufYP8ASRqHiH4Zf8g7+zdY1zOf7OA9stwcfNs6tqkD6dbRkgLLPY/L1z/xMdM7gk9Vz05JIIwpz+L2u/Cf9rHwrGmu3g8UeI0sceRr2h6tqOq2hy7dQTjoP+QNzyzckjcOy/Zz/aY8Z6r4mm+HXxDub/8Atmyvv9CN/b6h/pFlkj7BqQ3f8Sz/AJBx9cZPPG5unL6NDMr4XLswWlrq/W78lfS+lmrWvd8p4uZZfXweHlicRgG1p2et5Xd+Za9m7S1T+Hmv97fG2RRolic7c3w7ZxiwlHXPO4E9cYJHOea+adNJN/GepGM9v4mA7en/AKF6rk+meNviLovjLRobOGO/sNU03Vf9O0q+t9RtLv8A48H/AOJhpuP+Qnpf17Y5ORnzPSiP7SGOc9/pu/8Aif174r57NKNejjcxw+IvvFbvbnl56J3Vvn1bZpgf+Rc9H072+KS363S9d93dmPrn3n/D/wBCrzNv4/x/9qV6drrklsY5wOO/zDjJzjOPwx1JYV5TcAqpz6f/ABz3rgpRTfxW307259d/P8HruetClKMZaSvdWXK03rbu/X9ep2GjR40uHHH+r/H5pMZ+v444681Ynj/0aT93/d9OPmkH6+/vycE1S0GTzNLg7/d/Hlh65/8A1jk4DG9dSfurnp/DjH+849T16d+SeTtJP6vlP+74L0pnxuL/AIsv+3P/AE5UP0U0NY7yGOJJev8ArzgcfeOenf0PTJ5ORX2x+zh/wjng66n1nVJYobnj7PPPk9z7+oz+YyQCx/FdfGX7XvgqDzJPg14c8ZxQf6//AIRvx3p9pd9W/wCYbqekf2t2/mTkGvJfiF/wUS+PXg6zbw9d/AWLwHfjHkXHjk+ILrjL/wDINBbQ8c5zyfc81+qU7qLs38v8c1rrp0768q13Pm4ZfXq8zw17yWiS296ST1drJpvV+rbu5f01+LPHun67qJbUdR8+KHAt7KDjjLHp+A5PfuOrfI37UHxIN34Vtoov7LsHtJ7G4sdW8/8A0zRP7OMn9m/8THJP9qZHOcd8HJLH+ZfxZ+3v+1J4otZrOX4o3/h3T5v+XDwbYaf4eOcv11LTCdX5xn/kOZ6jPAJ+bbvxhrfiqS71TxfrWs+I9Vu/+X/XNV1LVrwZZ88amT1GPXGDySKupiKFndWdkuuj/earl7pLTSN7J8zWvVgshxVv9ou9FrbpeSerS1TW1+/vNXP9AD4b/tCf8Lb+Efhb4p3HirS9L0bUvDguNe1bz82em6zp7HTfEX8RI/4mmn6jnB6EEjrn5m8e/tiWUZvLf4aWv2qGHGPHPiuD7XgfOBqPhvw5n+I5/wCQ2ePXnI/BP9kP4ueIrf8AZi8K/CP+1r86MPGXiXxBew4xZ3H9o6hph07T8ZH/ABKzqmnalrOSeBqPBJyT6z4x8d3dvpcsUAlH/PsM+jHuD6evYkDBHP4txl4hZtQrPJsm/wCEp6Jb3avNLdp91fdJtbJX/UOEPDjKVzZxnT7bp7Jy0VrpabvVfHpI+lPG37Ynxf1DWTb2PxR8b+Zb4uJ7/wD4SzUbT7Ly+f7O03TG/srTM498ZHJAOPOZPG/iDx7fprPjLX9V1qT5f9I1a+N1eXHzMPU9c5456nJya+PNO1C5uL+wt5DLNeapOLiYY4ucM+fUZxg8+uOpNfU9zpl7pWgWGp3BMMQnsu2cjLd84H59z17/AA9TMce8vksRj/7TWiS2a1qO+ib37aapWdnf6hYChWx8vq2BST6r1l1au9WrW06N6XPo/wAMDQruK1B82U8YPP2O3+Z89O4/qDnrn67+Hvg6ynFvIZfKtJsYwDx8zDscnucE+2fvGvzSufEl/oeu+BtRjkltdKl+2WF/DATafaf9AOojjJ/6Buo9vXrgk/UPjj436j8PvBTazbSb9ItLGx1CfVbiD/j30Xc39o/8gw+gJ+mATxXyWK9t7yw9l2T2veXZtL7L6q97JK5+l4TJvqTazF6J2b8rzWt/Rd07Ntt7/aR8JadbBvEOm3kq3Wgz3n2ix+0bvtN6NRYDqf8AmCDTOg6/2j0J5r4C/a6+NniofFX4eRWdza2Ojz31l4f1eEz/AGS7+2agdNOmahpo3Y6ah9ee5GT7LpfiPxbrPw/8I/Ej4eXt14u0PxJY/wBsarZeSftdt/aFhpup/wBn4yf8Pmz/AMxfJ+JvjfD4B+MXiXw34m0/VdasNd+HviPRdY13Qv8AhGvEF3d239nM2NP/ALO0zSOehP4kZyGp4T2Fas8Q7XdrPXV80mt7u14rR63lFXupJ8uc0VSw7w2HTet7u990mrNybbst7JaPW137N43Pi74P/FfwNba6LqXRfi1PZeHr6x1Y8fefTf7Q07Jz27Z6kk4JFfDP7XP7H3iHSrzxB4h0UXWmSef/AKRNBf6hafavmfH/ABMsZHK8cnqBjC5P2R9i8U/tE/ET4W69c3t1daN4V1Wx1Cwa+nziy07cvT/mFg89j35yK+7f2uvAUUvg6wjlvorm+Gh/6fBbg55Lgd8/w+mfmOSSDn0MTif9zzHD/wDIwSSV7pOze1rq+tnZ6uytdHg0clv/AGjh8xb/ALPv2bSfvJ7SiujV2rLu43nL+PzVPjp4z05YPB1yl/H4r02+sdO0nVbCf7Hiy3OP7P1LTs/8TXpnnkfMMjOT9yfBv4gReO9JtbmeP7Br9nAP7Vsefm++P7Q5JxgZ9upJzgV8G/tN2kfg74tXEukSfYZZdtvcXEPGfmf1zj1HfgjIyc/bHw+0u0tr3w34v0eWKK21nSry3voPI9C4/tDAHPHT3I5yDn7uOEr59lDznRZjldmrtO7u2r7X1TVt723Ubn5VjadDJ8c8vVun82tnNJaydtpeT20acn6frsn3uc8jH13HBOTnB59/ucCvKbuTg9e3/s+PwOORnjnrzu9D1u45b2x/6Efr6e/OBn+95Rf3Ef772xx/33nufpj13AkkZr5ah19Wbr+HP1X/AKUej+HZCNIVuD93Hbu49c8YU+4xzkklb5wEO/OPx55YE8dMELgfXkAsTheH7nfptqCOPqTn75HcdMex5wSTkm1eT4t2YjpjGTj+JgR1OB7f7uDkGv0TB1fYYey6KN+71lomnpZxu9G2m1f3W38jiKL9s9UrrW+/xTlpZ27vvbd9T9UNJj0/ULVI7iCKaLjt1+Zx3b8ee5X3zl6B8CjjxF/avi668e+GdYnHkeD/AIgWOna/pFt8zD+z9N/4k/XGe+epzndXOeGfFt0Y0N3aDjH7+AZ6kj8ec+voeRx9K+BfEmjXFgbI+U0ox/r/APROP3nTn9fqOoJr9gpU/bJ2V9tNL7yWur212bd2tdm/j0sTQvqrRe+vXmWnwyV767bq7s03+Vn7R3/BP/4XeLtP1fWfhbosvgHxZZ4uDodjcE+HdayX22B03H/Eszgf8gTpjnwycmvkLw5+zv8ABO88M6ZqEWveI7zWtMvrzR/GehatP9k1bRdZ046bjT9S045AwBqJI+mTk8/0AeIPB/nvNeW9z/ZMwxzwc58zHGcDjqOvIJJIOfgb4ofs8eDfGfjzRpLi7v8AwX4s8Y65onh63+JPg4k/8hBn03TtP8SeHNUzpWq6X/ampadjn/hIh/Zx55xXkZvl2YVsvzB4bHrLMwtHe7TV5LyeqjfRSdpOO7bPockzP2NbA/WbZplvRdXrOTvd3Ssr731vvdPgPhD4L8N+HtIsNK8Ppmxs4Ps9uvni6uzgt33cevudoJB5r0fxp4LFxpsd4Y/N8jE4PPrIDkenp6nOAcE1gav8Bfj9+y1qUE/iLSf+FheE4c/b9c8K2Oo/6NySNQ/s7A9Dj13Nkjbk/QfhHxb4M+I+gmTStQtbr9wPtEGR9stTmUDT/U5445wSeSBk/wAx8RZVnGTY5vMcv633SdlKS0sk/es76J7K7sr/ANIcN5plOc5fL+zrbpLWKbTcmk3zbWiml5pLRu3xL4VsH/4TTSMRfuoZ1/1+TnDEfXnt9exANfWnxQ0h9U8KaPZWdz5TGdRbiH/l1xYnvz1BJ9s4OTXD6/4IuPDuuR6pZR+ZbRTm4Bx6E/0H0znk459r0RJPEVppgkAzFj9wM/NhnP0GMZxnk46kc5UcVfDyeH7Rja91bmnb3k9Unrv1ak0opvGhSdDN5PEu/vRV7WaV5t316pbO+vKrNWcvMPG+nPeL8IdGgl+1XVlrl7PPDOP+Pqy07QW/4mGMn/mJ6kp/r1z7v8Ybc6f8M72+vIZdV0zTtKFxfeDYLL7ULiy07T3H2Dk57H36HJB57HQ/hvbXHjXRvE17bzRXOm6UNPsIM/a/4mHv/j0zkkEfX2kfBvSfEOn239rtdS3Qn+0TzQgj/Tfm9OefrkZPuT5mJq6YKK68u929HV17q9k9dEnbWzZ+o1MwoZynh/qWyjbR33qfyva8VZ76rXQ1P2Ufi5p/ww+E3g/xbqnhHT/DY8SaHouoWXhzxHpOn3f9mWWoWD40/UtN1P8A4lPr39cnOK888ZftUfAnxpquoyad8H5bmSynvdQv/Emk/DzxBa6RqfLD/iWjTGP9qYPJGcDJyc9cnx1oKXnjbT/D15LdX+madY3v7qecf6TZacdNH2Dbg/8AEr/4mQGT/wBA48nJJ+Gv20vHnjLw1Y6LB4U0K+ltDrllbGGxH/HtZguF/tEZzyeOP9rJJyaMFha+LrSw2JUd0nfS9pS30t5a+XuuVkeNnOLw+Cw8sSnbu27/AGpJWSb00aV3vdSlpG/0p8DvGehRa48kEdrYx6l4q8Saxp+lTwfZBptlqHjHVtS0/T9SGcDVP7L1EZyed3GSMn6n/at1/wAMt4bS8DxeVL4cXzzbnOf9AcHIJJGcj9TyWr83f2k9NPw+8EeBPGfhGa6OpgWVvrnkW32T7Te7tNBxz3x7/dPJIJr2342+LNP1L4VaXf6rceX5ulWWoTf6QD009v8AQOuTn39OpxRmdL2GAaw+v9qbu12tZcrtZXulZra1rpNSv5WDxX12vLDYhu+VKNtHspSbW71TW/K9Lq7vY/mU/bWWC88Yvqlna+VNNfD/AEfjr8xznHoOOR35Jya+1vg5aGz+FPhZrjUftOqWOhi41W3n5+zXuokjse+M888nptJb89f2pfF9v4n+JL2WipHKbK+W4MFvybfLPjqTjjjjPUdhz93fAvUbhPhxpc11JMLnUp7zUL0+QbX/AJcG0zTjgc9cE+mRzgNn9WyDFYfK+Ecc8QrtZBZWvvzNb3fWL13eis02fiPGVL63n2C+ra3z6K0ttzNNSTb7RW+7d202dDrN1iT97Lg+ntl/f6H16AeteTajeHzZwR973Pbf6fQc9eCMnqfdl8N6drllNe3F7f8AmC4+WCGfgjLA9Ccfj6kZAyT8j/tHZ8HaHo0mjXl/F/aWuDT7mae4x0L7eepzz+HYA149HJq9DDvE9O+vRz1UX3S5pa3VlZXZz0swoVq7wzS6KNm073aW9u3wq0krJ76+veH9btYtLtyJcj5cjBGRlgOck+/T1GeMC3f65b+Q/wC9zjGevzHJA4zwQMdOuTwCMn4D0rxZrUKFI9VlwcceeMnBkH1444xjkcnqdefxp4t8s5upZOgH+jjnl89wP4R17k8Zr6OjiVQw7w176pdLu0pLv5apXej11cjgqZT7bEPZ3W7bTavJpeT0Tuk1ZN3ulI/e74A+OLbxZo9r/wAJJp8XmcW7X/2fIubzLbeC2ecHpnHJJ55+0dL0HwxL5flebBLxn9+WzzJ7dQOT7seh6/LfwV8GJ4T8E2+j6lcf2rJlf9InHu+OvY8Adxz1+avpbw6CkEVsSLm2zxbzjOOWzyDznA9OoHIzn9sy+kqOHs1fd30vvK/utbpQV+bo1rdH57jqmHrVniMNgdXu9W2uaV+vXW99E09JJ6+/eEfCWnXVk1lcar9rtZsefpV9bj+845Gc+vcnpycMa+X4vgxoulftI6BPqPie/wDDngnw3P8A8Jhb2XiOc2ukan4m07UT/wAI5p+m6jqYJAGqBdZ5PP8AZ3XPX6U8OaNqBjZ9HvYvJ4/0G+9mbH/Ex+uehzk4J3A10Gqr4n1TT5tC1Lwpa6zbTQfZ/IvzYXdnc8n3J46DqeTg5HPTKHtbrVttt2V+sr6a73XyS3OTD11QUtHdtNNW6XXV/euvM9Vyty7HXfGHhC9smt9S8U+CLq2GMQz3+n5JywPXWTjsPo3Xmvgv4ueBP2S/NvPFOu6jYeG7+HFxceJfA2u6hpV5a5aTH9paj4Y657/20ehHUg56LUf2Ex4svJ7wazr3gbS7jFwNL8K+NPGOl2nBcHOmaZq/9kjHTP8AYfccHGTjS/sw/BL4J+H/ABl8R/EXwutPF0fw+8OeJfGF/wCLPGNx/wAJXq9zZeHtA1bU/wDiWjxMP+QpjTB9eckkA1xY7CUK9F4fEppO3XzlprfrZtNXva7bR6OXYn6nXliMLj7rpo+a92ne/d8199bX2Z+a/wARv2mfhn8PfEujeH/hsfG/xGsJr7Rf7dv/ABz4k/4lH9i6iWx/Z39maR/xNAdLz/xOf7c1nw8c9dbINffei+G73SrpJ7aW1iORcGxx/oZ5fnrgYGO/Py5Oc1/PZr3inXvib8Stc8a+IbmKTWPF/ioahOsHNnbf2iZBp2n6acn/AIleh5/sjkngkZJyT/RrbTx+ZtX/AJYQWOAc+rHHBPHPHU/UGvyCOV5DjP7QTwF8ubVtHteSXqrPXXyumrv7vE59n2W/UsS8ctXZWvfeXbpeN+/vxSkknf2/SodZ0fRdA8RXmnXVno2vT3lvoV9Pb5s9TvdPOmjUtP07OD/zENOP0I5IFeo+Hvio8KMkY8mUYzyT3f39vp2I4GcT4kaxc6N8BP2XU07Ubqw1Wy/4Wv4ot7+xn+yXem/2h8QX0vTtQx/3LnvkZGSSTXQfFvxPq3iDwr8NdL0PxRrPj6bxh4c/tC/M5/tTV7bWfD1hpg1HT+M+K/8AoIc/25/Ec528+XmXh79SviMmzBbbO9170tW2r66Jp9bbtXf2+S+Jntq2Bw2ct5Zt1ja15dXK+q1vtomopplO/wBW0692aokf2u+hAE/+kf6XcWWoFhqWn8cnnTdN69AT1JZj8u/G/Svhx4mh0zV/EXiuXSxo18NQOhXBNrd6neZbH9padnjSwR1z/EPU153498OeL/EEep2dt4m1T4fXMditvYX/AIO13xhaauL0eYD/AGlpuqf8Sr+Hj8T1zn82fi/8Df2whfo/hn4yeI/HmmNi4+z6r4l1DSbu1wW4/s7VNYOk6rkdOeNxGeDn5ajwrm1v9pbenrZuTu7u/RX+/XmTb+nz7ifKaLlh/r/9qXdmld396WtuZ2Vo31d02mm5XR9I/tP/ALR/w8udL8IadeeKdPi8L6Z/pM08FyLvV9aH9pP/AGl/Z2naYc9dN45OAW5Jzu/PP9pf9uzxV8X7ZvCHw90OXwt4dhgGn219qsxOr3Nllv8AmGlv+JX369CRz3rxS7/Ze/aAv724fUfDN/FJBOv2m4vtd0/DfMxH/MY4xkdD7dSxri9P0fw54G8VNpfxL0PxHZ/Y8edBgnN7lgdQ/s4Y/tTS/l9+Gbnhs98chw9JS+s7ZXbVO99amybcUtdfJrmlq7/PVOI8RXv9WaaXRdbt3d23r7qdrvfW6kzrPgL8CJdXvW8W+NJYrq14uPsU85+13V7luxBxjn35ABr7fnCaXp7R2kUUMVnALe3hgPI5YEnB5zn8Mnrwa4H4e+IPCjWU8Xh3UdLlspfsP2d7AkcZbtz24wemc5IbI6/W7qP+y5pN+QZ7LnbjH/EwIPHOc4/DPUnBNY7FfXcRgcP/AMy1NrS/Xm1tfS1/W2m7sfOUKKV8Smvld3S57bOy8klrdK99/N9QvfiHpH2z+xvFVrbW02P3H2A3Z4Lj9OP1xk7jXzv8Uv8AhIfFumjT9e1bzZNNvluIJ/I0/wD0n/iXyDpknnnHf1BJyfoLxhq9pp6TveXEVra2/wDr557g2tmMl+pJ/wB3jqDxySMfNWoXIuL7xSPXVVA44yNB03P5d/qvBOc+vV1wsn6fcp1Nfu19Op5+Dqr2zb66N6Je9KTu+3w39Xa6SueP23hLXobclLeK6zjjrjl/cjtwOvLehNZl7aazYFvMt7+1HA6Y7sOnHTt25Oeor3vRT/o8I/c89c+zHG3+n4Z6VszeWIv3lvye+ewZx+Gcev8AdPJBy8Mm05N7qy9OeS/R/hdtq56NSpzxdm7N+auk5La+19ddb8rtdM/ePw7Ob23jAfyjnjoejOOn+eo4JyT6lpH2K2yLnVpu2fIz6t7cfdz6nJ5BU5+U/Dmoabe6Xa6s/jOwtbCfiG/5tehODjd7ng9c9Sfmr6F8GeH/AAzeOnm69a6zLDi4+w/bj/ecDHHU8kc8HOc8V+9UXaLfZP8A9Lt/X6s/IJUPq/M+m1nq7qUrO/bXbdX1vLQ+lfBOqGSM3GjaVLqnT9/fatj+J/c/3fr05IBz61a+PPENl5X2yysLSIc/uO4JYdM9Tj6/d9ST4hBqElvBFZjX7XRrWLHkQaVb6jd9Gf8A6Bjf/X4PXJJv2Wl+HdZuTHceOdTuunNvbn1cd2I6be+emCSCavnfl9z/APkjn9su6+6X+Z79cfEnSdsUdxfRQSTf8sP+Py7+84/5B2eBz6/xEZIBJ8d+Nbf278MvE9x4g0421h4jsV8H6Vpc85H9pWWoai+o6jqHJ/6Bem6jo/f1xy1ev+A/hZ4daSO4snuruX0nP16HGeikg5/2Tg9flb9qz4kaKPFjeEbe7tLbTfCkH9nnzrkj/ic7mGo/2b8x/wCobnA/5h/OTk18xxjmiy3hnMHhklmOa6W1fWcWt7a8qtra7enU9rhXL3jM1wV1213/AJmk/eu9U7Xbd7qzUrn84vxY07TvDP7Qvi3RdHt4rWws/FWifYoIP+XTFhp2O/ORjPPGRnkMD/Qdb/ETw+7maTSrWWQY/fz2Gn99y9M+/Of7w5yBu/MO8+FngDUfjFq/xHuLe/1m5vtVsr+Dz77/AIlH20M3/Ew03Tv169wM5Bz9daTcLDFMXwAYf3AOOCC2Occ5J/Docg5r4fJaVehgcDfHWvpq31bV9W2rpdLr4la0WehxH7DF13azd9Luyd3LRrmf2krLfVLmve/2EvxM8MzxWMU1ja+VaQD7Pb/YRdfZyC3fHHr+QycEj0bxP8etJ8TfCHwv8KI9L0ewt/C3irV/E9hrsGlWFrq2L+wfTf7A1HUN/wDamp6Xg/2znqf+JyCxABr4VvdWinj0uO2XyTDY/Z5/Ih63n9pN9en9Tz3PWeE7C4e7a7bzWlhgGD90Dlz69enr/EBnAYfTUfrNZNuVldJOy7z2jbfdN31Ts9I2PnMHSWCu7rlSWt29LzWl+vuK3nbVtpH1D8a/jBqvxZ/4V5/bWleHbT/hCvA+i+B7e40PSdP0v+0rPw8XGnah4jGmEnVNU6aN/bOeAVwQRz4HLZ2nlfJb2uO//H/2Z8Yz6859MAE5ORP4z8aXmvX2l3eoRxCSLQ9F0fz4Bn7Te6duzqGpZPPIz19QCMEn72+G37EHjL4x/sXXf7Svw2e61/xF4W8beMdI8YeCfI/0zUvB2n2XhvUdO17w6uMDVNDGo33/AAkGkZI8QeH2dcbgN11sPTdWLnJQUpRpJu6i5OU4wjZPdtaa2bkldysfSUKmIrOTw7k2tXbT3VzrW+r1jpq27Xu1eR+VPirTI/s5khGc/wDTAj+I445Pr74A6qDXxF8VfAGj+L9c/srW/DsWqaZeQcXxz9s0y93MOw74z9Gxmv0D8SJJvvLOSL/U4P3fd/f1B59wM4Ga8l1rwwl1EZDDmWbHOR2Zz6Z9Prkkn72fP/sq2ztttJ3+13fp1vra+km+iji+VS6ba8srdVde8tbX0XTeTauvx98a/szePfB0k/iX4YXl/qunwY8+ygzaeIrXlsYGM6qcg+uM85ya8w034665ZwXvh3x1p0ueLeeaCA2t5a4L/wDIR07POQCR1wN2MndX7JSWEllDPbvHjge4H3gM+ueeOgPqdxr5m+MXwU8CfEa1ml1TTPI1cY+za7Yg2urcmQMeR/xNOgxyeMdcc/NYrJYtN4dJO3zunJbNpWaUXpZ2b0crX9/CZo/eWIvraz06fPlT0d9Unoru938QPr7+K7WebSrv+37Af6+H7ef9G5Yf8TLTM/kDyPm5NYF2dW5jltDDnofOBzyw7Hj7vvkfWuI+IHwO+InwruZ9Z0Vr++0yzx5GraU32W7tuWH/ABMtM6c9uc/e6jJrG0D40XUSR2Xi/Tv7Qi+XN9Yn7HeDk46HnJ6+nAIxk149XB4ijd4l6K2nM9m5Wd3FLq2ru26UuZyPVpVfrGmHa83bfa3Rqz+bd0tLXfolpqsloYra4jliAzgwZ9T6/Tjrwxxklq3BrEcsB8uTzenY9AWxxkHp254JySeDkwahoXiG1a40HUYr4j/j4g/5fLbk4/4lpPucfXkkVx+pQS2xZoZJYpuxzweTnqPb1PXBOcZI1tLKG295dLy1+Hzu/JrfUI0/rCldK2z19enZXi735r21vFuX79+FPCNtBBHb/Z/NtZpxcGC4g/0S4s9QLAdeT7jJIyOSSTX0f4V8Lad4ejsY7m3iu9CAH2c8/bNE+Zz6n/iV/KPxJ5J5oor+hqCTc766R/8ASpr/ANtX+bV7/kNVtR072v8Af5/1p1R9CaDoOu2rRy6N4jurq2m/5cdV0/T9Vs+r5/4mWf7W75zn0POcV7voXgiTXIYjrWlaXdyf8fHn2NibTPXn164I689TyaKK1p04crdt99X0c138vXzdkY4b3nK7von01s2l026/cm7XPBfil+1v4e+FHhrxZ4C+G95dTeP4J/7GE8/9n3WkaLZEyf2n/wATInP9qd+fQgZBOfzTv9Rn8ZD7TqMkPmGc3FxOSbv7VeKSFJOeOpxjsec0UV+U54/r2aYSeItOWHUXTaVu++r6ttNWava7Wp9Xgv8AZME40PdV1a/T36i0tb8b+dzEtrKK3v3jjGYxOuP++iOp64A44/i4OWJr0OzUBtxzJgnJ5OfmYDoe+AQOp+bqRklFLCq+n96K/wDJv6/4JzSWlu8kv/Sv8/y7O/dWNn5UrGX2wfXqOmf6nhjySrZ9Q8KXltZJqcVx+68+xXyP+vzL5zyfbHfOO2KKK9jC7S/xf+2ni4j7f/Xx/wDuQuTab/aFjYo3lCNp/ri9UsM/XGe3uCTX9JX/AARz+FetxfBO68SaX8V9V0iBvHN9qEHg/SbXQLaz1C800abp2pf8JIBozavqmmNpY00DRjrYGg/2m3zlx4Z8SMUV049uGR5hKLV+elF3jFpx9pBWalF7pvzV24tScm+/h2Ef7XwXm5fg6iXn0T1vrbe2vzD/AMFc/wBijwd8PZk/aS+Gcdho2h+L/Ea6N448IQw/ZTo/jC/0/WNS/wCEi8O4RVXTdb/s7Uf+Ek0UcHxCSQM7hpv4TXOjieGXy49zQZyfxfGOw454/nnBRXr8LxWOy3lxLlNRcop395pONrt81/ier956NycuZy6s4bweJaw9op206aOa06ra+nXVWaR4j4sjjjmJ/dBRjBznHMmeM5OTjg+oPQNnxHXbyKFW4lA+XjyDxjeOoOTnnpxyechiSivDxWEoxrzsn9pb9VKKvtu1LXporpu7fZhJuLna2nrrrNa666Rur3ablrds8k1e4jnRiLeWU8dbccYZx3J7H14zjuTXxj8U/gj4V8UyXt9p1l/YOszTjE8H9n/Y7r7/ADqWm59O2ckkZJPUor5rExUlK/SLfzXPb8IrTzbvzanoZdKUKc3F7ar11797v792fEvifwn4u+HuoRyXPmxdPI1ax2m0uuX/AOYlnvt9T3OckkTWPxZu/s7WWvwf2iDgC/gBW74LZ9ePun3GeOtFFeNLD0p6Siu3Xa/e9/x20d0etHG15815LS2ykur/AL3m/v1bP//Z\n" +
                            "\"\n" +
                            "}";
                    String hmac1 = DemoHMAC.doHmacSha256Base64(requestBody);

                    ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService();

                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("Content-Type", "application/x-www-form-urlencoded");
                    Call<ModelFaceResponse> call = apiInterface.facecomp(uuid, str_date, Preference.getInstance(FaceCompActivity.this).getValueString(Preference.KEY_TOKEN), "52ec07383c184a8bb58b0c874a570c91", hmac1, "0000", "", "");
                    call.enqueue(new Callback<ModelFaceResponse>() {
                        @Override
                        public void onResponse(Call<ModelFaceResponse> call, final Response<ModelFaceResponse> response) {
                            if (response.message().equals("OK")) {
                                Intent intent = new Intent(FaceCompActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            }else {
                                Toast.makeText(FaceCompActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelFaceResponse> call, Throwable t) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utility.customDialogAlert(FaceCompActivity.this, "Connection Fail");
                                }
                            });
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ออกจากระบบ");
        builder.setMessage("คุณต้องการยกเลิกการทำรายการ\nทั้งหมดหรือไม่");
        builder.setCancelable(false);
        // cancel
        builder.setPositiveButton("ทำรายการต่อ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // yes
        builder.setNegativeButton("ออกจากระบบ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FaceCompActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                    }
                });
        builder.show();

    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        df.setTimeZone(tz);
        return df.format(date) + TimeZone.getTimeZone(tz.getID()).getDisplayName(false, TimeZone.SHORT).substring(3);
    }

    public static Date fromISO8601UTC(String dateStr) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    private void setCustomToolbar() {
//        Window window = this.getWindow();
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.system_bar));
//
//        View mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowCustomEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setCustomView(mCustomView);
//        }
//
//        Toolbar parent = (Toolbar) mCustomView.getParent();
//        parent.setContentInsetsAbsolute(0, 0);
//    }

}
