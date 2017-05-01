package com.zhang.photo.photo_an.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.zhang.photo.photo_an.R;
import com.zhang.photo.photo_an.common.SharedPreferenceUtil;

/**
 * Created by zhangsl on 2017/4/25.
 */
public class ConfigActivity extends Activity {

    private EditText host;
    private EditText port;
    private EditText imageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        host = (EditText) findViewById(R.id.host);
        host.setText(SharedPreferenceUtil.getHost(this));
        port = (EditText) findViewById(R.id.port);
        port.setText(SharedPreferenceUtil.getPort(this));
        imageID = (EditText) findViewById(R.id.imageID);
        int id = SharedPreferenceUtil.getID(this);
        imageID.setText(SharedPreferenceUtil.getID(this)+"");
    }

    public void confirm(View view){
        String hostValue = host.getText().toString();
        String portValue = port.getText().toString();
        int idValue = Integer.parseInt(imageID.getText().toString());
        SharedPreferenceUtil.setID(this,idValue);
        SharedPreferenceUtil.setHost(this,hostValue);
        SharedPreferenceUtil.setPort(this,portValue);
        Intent intent = new Intent(this,MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}
