package com.szhklt.www.modifysysclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button changeSysClock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeSysClock = findViewById(R.id.button);
        changeSysClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testDate();
                try {
                    SystemDateTime.setDateTime(2020,9,10,10,13);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //网络操作应在子线程中操作，避免阻塞UI线程，导致ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                PingNetEntity pingNetEntity=new PingNetEntity("www.baidu.com",3,5,new StringBuffer());
                pingNetEntity=PingNet.ping(pingNetEntity);
                Log.e("testPing",pingNetEntity.getIp());
                Log.e("testPing","time="+pingNetEntity.getPingTime());
                Log.e("testPing",pingNetEntity.isResult()+"");
                if(pingNetEntity.isResult()){
//                    Log.e("testPing",MainActivity.getNetTime().getTime()+"---");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr = simpleDateFormat.format(MainActivity.getNetTime().getTime());
                    String[] arr = dateStr.split(" ");
                    String[] arr2 = arr[0].split("-");
                    String[] arr3 = arr[1].split(":");

                    Log.e("testPing","年:"+arr2[0]);
                    Log.e("testPing","月:"+arr2[1]);
                    Log.e("testPing","日:"+arr2[2]);
                    Log.e("testPing","时:"+arr3[0]);
                    Log.e("testPing","分:"+arr3[1]);
                    Log.e("testPing","秒:"+arr3[2]);

                    try {
                        SystemDateTime.setDateTime(
                                Integer.parseInt(arr2[0])+1,
                                Integer.parseInt(arr2[1]),
                                Integer.parseInt(arr2[2]),
                                Integer.parseInt(arr3[0]),
                                Integer.parseInt(arr3[1]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public static Date getNetTime(){
        String webUrl = "http://www.ntsc.ac.cn";//中国科学院国家授时中心
        try {
            URL url = new URL(webUrl);
            URLConnection uc = url.openConnection();
            uc.setReadTimeout(5000);
            uc.setConnectTimeout(5000);
            uc.connect();
            long correctTime = uc.getDate();
            Date date = new Date(correctTime);
            return date;
        } catch (Exception e) {
            return new Date();
        }
    }

    public void testDate(){
        Log.e("test","--- testDate ---");
        try {
            Process process = Runtime.getRuntime().exec("su");
            String datetime="20131023.112800"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s "+datetime+"\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
