package com.example.testota;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.testota.download.Constant;
import com.example.testota.download.DownloadService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.start_download)
    Button startDownload;
    @BindView(R.id.pause_download)
    Button pauseDownload;
    @BindView(R.id.cancel_download)
    Button cancelDownload;
    private Context mContext;

    private String url = "https://raw.githubusercontent.com/Wodner/Wodner.github.io/master/src/ota/ota.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        init();
        Toasty.info(mContext, Constant.FILE_PATH,Toasty.LENGTH_LONG).show();
    }

    private void init(){
        Intent intent=new Intent(this, DownloadService.class);
        //这一点至关重要，因为启动服务可以保证DownloadService一直在后台运行，绑定服务则可以让MaiinActivity和DownloadService
        //进行通信，因此两个方法的调用都必不可少。
        startService(intent);  //启动服务
        bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
    }



    @OnClick({R.id.start_download, R.id.pause_download, R.id.cancel_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_download:
                downloadBinder.startDownload(url);
                Toasty.info(mContext, "start download file", Toasty.LENGTH_LONG).show();
                break;
            case R.id.pause_download:
                downloadBinder.pauseDownload();
                Toasty.info(mContext, "pause download file", Toasty.LENGTH_LONG).show();
                break;
            case R.id.cancel_download:
                downloadBinder.cancelDownload();
                Toasty.info(mContext, "cancel download file", Toasty.LENGTH_LONG).show();
                break;
        }
    }



    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder=(DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定服务
        unbindService(connection);
    }
}