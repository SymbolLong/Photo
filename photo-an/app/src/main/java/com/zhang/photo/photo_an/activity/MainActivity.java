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
import android.widget.Toast;
import com.zhang.photo.photo_an.R;
import com.zhang.photo.photo_an.common.Common;
import com.zhang.photo.photo_an.common.SharedPreferenceUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private ImageView imgView;
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/photo_an/";
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    private float startX = 0;
    private float startY = 0;

    private int id = 1;
    private String path = "";

    private long timerSecond = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.imgView);
        id = SharedPreferenceUtil.getID(this);
        String host = SharedPreferenceUtil.getHost(this);
        String port = SharedPreferenceUtil.getPort(this);
        path = String.format("%s:%s%s", host, port, Common.API_GET);

        File file = new File(getPath(id));
        if (file.exists()) {
            imgView.setImageBitmap(BitmapFactory.decodeFile(getPath(id)));
        } else {
            imgView.setImageResource(R.drawable.blur);
            downloadImage(id);
        }
        downloadImage(id + 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            startX = event.getX();
            startY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            float endX = event.getX();
            float endY = event.getY();
            if (startX - endX > 50) {//Left
                nextImage();
            } else if (endX - startX > 50) {//Right
                preImage();
            } else if (startY - endY > 50) {//Up
                timerSecond++;
                if (timerSecond > 10) {
                    timerSecond = 10;
                }
                setTimer(timerSecond);
            } else if (endY - startY > 50) {//Down
                timerSecond--;
                if (timerSecond < 0) {
                    timerSecond = 0;
                }
                setTimer(timerSecond);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 上一张
     */
    private void preImage() {
        id = id - 1 <= 0 ? 1 : id - 1;
        changeImage();
        SharedPreferenceUtil.setID(this, id);
    }

    /**
     * 下一张
     */
    private void nextImage() {
        id++;
        changeImage();
        SharedPreferenceUtil.setID(this, id);
        downloadImage(id + 1);
    }

    /**
     * 切换图片
     */
    private void changeImage() {
        File file = new File(getPath(id));
        if (file.exists()) {
            imgView.setImageBitmap(BitmapFactory.decodeFile(getPath(id)));
        } else {
            imgView.setImageResource(R.drawable.blur);
        }
    }

    private void setTimer(long sec) {
        if (timer == null) {
            timer = new Timer();
        }
        if (sec == 0) {
            timer.cancel();
            timer = null;
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(new Date());
            }
        }, 0, 1000 * sec);
    }


    /**
     * 保存文件
     *
     * @param bm Bitmap
     * @param id 资源id
     * @throws IOException 异常抛出
     */
    private void saveFile(Bitmap bm, int id) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(getPath(id));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    /*
    * 连接网络
    * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
    */
    private void downloadImage(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path + id);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    String contentType = conn.getHeaderField("Content-type");
                    if (contentType.contains("json")) {
                        handler.sendEmptyMessage(Common.DOWNLOAD_NOT_EXIST);
                        return;
                    }
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        handler.sendEmptyMessage(Common.DOWNLOAD_ERROR);
                        return;
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    saveFile(bitmap, id);
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
            } else if (msg.what == Common.DOWNLOAD_NOT_EXIST) {
                Toast.makeText(MainActivity.this, "资源暂时不可用或被移除", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    private String getFileName(int id) {
        return "timg-" + id + ".jpeg";
    }

    private String getPath(int id){
        return ALBUM_PATH+getFileName(id);
    }

}
