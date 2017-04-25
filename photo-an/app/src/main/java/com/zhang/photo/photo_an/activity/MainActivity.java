package com.zhang.photo.photo_an.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.zhang.photo.photo_an.R;
import com.zhang.photo.photo_an.common.Common;
import com.zhang.photo.photo_an.common.SharedPreferenceUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private ImageView imgView;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/photo_an/";

    private String host = "http://localhost";
    private String port = "8888";
    private int id = 1;
    private String path = host + ":" + port + Common.API_GET + id;

    private long timerSecond = 1;
    private Timer timer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.imgView);
        id = SharedPreferenceUtil.getID(this);
        host = SharedPreferenceUtil.getHost(this);
        port = SharedPreferenceUtil.getPort(this);
        File file = new File(ALBUM_PATH + id);
        if (file.exists()) {
            imgView.setImageBitmap(BitmapFactory.decodeFile(ALBUM_PATH + id));
        } else {
            imgView.setImageResource(R.drawable.blur);
        }
        path = host + ":" + port + Common.API_GET + id;
        downloadImage(path, id + 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if (x1 - x2 > 50) {
                nextImage();
            } else if (x2 - x1 > 50) {
                preImage();
            } else if (y1 - y2 > 50) {
                timerSecond++;
                if (timerSecond > 10){
                    timerSecond = 10;
                }
                setTimer(timerSecond);
            } else if (y2 - y1 > 50) {
                timerSecond--;
                if (timerSecond < 1){
                    timerSecond = 1;
                }
                setTimer(timerSecond);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 上一张
     */
    public void preImage() {
        id--;
        chageImage();
        SharedPreferenceUtil.setID(this,id);
    }

    /**
     * 下一张
     */
    public void nextImage() {
        id++;
        chageImage();
        SharedPreferenceUtil.setID(this,id);
        downloadImage(path,id+1);
    }

    /**
     * 切换图片
     */
    private void chageImage() {
        String pathName = ALBUM_PATH + "timg-" + id;
        File file = new File(pathName);
        if (file.exists()) {
            imgView.setImageBitmap(BitmapFactory.decodeFile(pathName));
        } else {
            imgView.setImageResource(R.drawable.blur);
        }
    }

    private void setTimer(long sec){
        if (timer == null){
            timer = new Timer(true);
        }
        if ( sec == 0){
            timer.cancel();
            timer = null;
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis());
            }
        },0,1000 * sec);

    }


    /**
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    /*
    * 连接网络
    * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
    */
    private void downloadImage(final String path, final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    System.out.println("contentType:" + conn.getHeaderField("Content-Type"));
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        System.out.println("error:" + conn.getResponseCode());
                        return;
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    saveFile(bitmap, "timg-" + id);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Common.DOWNLOAD_ERROR);
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Common.DOWNLOAD_ERROR) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("连接服务器出错");
                builder.setPositiveButton("去配置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainIntent = new Intent(MainActivity.this, ConfigActivity.class);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                    }
                });
                builder.create().show();
            } else {

            }
            return true;
        }
    });

}
