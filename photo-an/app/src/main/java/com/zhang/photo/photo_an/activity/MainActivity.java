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
import com.zhang.photo.photo_an.common.Config;
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
        init();
    }

    private void init() {
        imgView = (ImageView) findViewById(R.id.imgView);
        id = SharedPreferenceUtil.getID(this);
        String host = SharedPreferenceUtil.getHost(this);
        String port = SharedPreferenceUtil.getPort(this);
        path = String.format("%s:%s%s", host, port, Config.API_GET);

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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//当手指按下的时候
            startX = event.getX();
            startY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {//当手指抬起的时候
            if (startX - event.getX() > Config.GESTURE_SHORTEST_LENGTH) {//Left
                setTimer(0);
                nextImage();
            } else if (event.getX() - startX > Config.GESTURE_SHORTEST_LENGTH) {//Right
                setTimer(0);
                preImage();
            } else if (startY - event.getY() > Config.GESTURE_SHORTEST_LENGTH) {//Up
                timerSecond = timerSecond + 1 > 10 ? 10 : timerSecond + 1;
                setTimer(timerSecond);
            } else if (event.getY() - startY > Config.GESTURE_SHORTEST_LENGTH) {//Down
                timerSecond = timerSecond - 1 < 0 ? 0 : timerSecond - 1;
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
    }

    /**
     * 下一张
     */
    private void nextImage() {
        id++;
        changeImage();
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
        SharedPreferenceUtil.setID(this, id);
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
                handler.sendEmptyMessage(Config.TIMER_CHANGE_IMAGE);
            }
        }, 0, 1000 * sec);
    }


    /**
     * 保存文件
     *
     * @param bitmap Bitmap
     * @param id 资源id
     * @throws IOException 异常抛出
     */
    private void saveFile(Bitmap bitmap, int id) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(getPath(id));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    /*
    * 下载图片
    * @param id 资源id
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
                        handler.sendEmptyMessage(Config.DOWNLOAD_NOT_EXIST);
                        return;
                    }
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        handler.sendEmptyMessage(Config.DOWNLOAD_ERROR);
                        return;
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    saveFile(bitmap, id);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Config.DOWNLOAD_ERROR);
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Config.DOWNLOAD_ERROR) {
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
            } else if (msg.what == Config.DOWNLOAD_NOT_EXIST) {
                Toast.makeText(MainActivity.this, "资源暂时不可用或被移除", Toast.LENGTH_SHORT).show();
            } else if (msg.what == Config.TIMER_CHANGE_IMAGE ){
                nextImage();
            }
            return true;
        }
    });

    private String getFileName(int id) {
        return "img-" + id + ".jpeg";
    }

    private String getPath(int id) {
        return ALBUM_PATH + getFileName(id);
    }

}
