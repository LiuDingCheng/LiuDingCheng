package digitalking_for_ldc.singlemap;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import digitalking_for_ldc.singlemap.ToolsUtil.ToastUtil;

/**
 * Created by Administrator on 2016/5/22.
 */
public class HelloActivity extends Activity {

    private String[] PSN;


    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_activity);
        PSN = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.INTERNET
        };


        CheckPermission();

    }

    public void CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(HelloActivity.this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(HelloActivity.this, Manifest.permission.BODY_SENSORS)) {
                    //已经获取权限
                    Timer timer = new Timer();
                    timer.schedule(task, 1000 * 2); // 设置时间
                } else { //请求获取权限
                    ActivityCompat.requestPermissions(HelloActivity.this, PSN, 0x000);
                }
            } else {
                //以获取权限
                Timer timer = new Timer();
                timer.schedule(task, 1000 * 2); // 设置时间
            }
        } else {
            //Android6.0以下版本
            Timer timer = new Timer();
            timer.schedule(task, 1000 * 2); // 设置时间
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0x000:
                //如果长度大于0  说明有权限没有赋予
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[2] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[3] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[4] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[5] == PackageManager.PERMISSION_GRANTED||
                            grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                        Timer timer = new Timer();
                        timer.schedule(task, 1000 * 2); // 设置时间
                    }
                }
                break;


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            startActivity(new Intent(HelloActivity.this, MainActivity.class));
            HelloActivity.this.finish();// 结束当前的主页面

        }
    };
}
