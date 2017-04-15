package com.zwe.zxingdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEditText;
    private Button btnGetCode,btnScanning;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mEditText= (EditText) findViewById(R.id.content);
        btnGetCode= (Button) findViewById(R.id.getCode);
        btnScanning= (Button) findViewById(R.id.scanning);
        mImageView= (ImageView) findViewById(R.id.code);
        btnGetCode.setOnClickListener(this);
        btnScanning.setOnClickListener(this);
    }

    private Bitmap encodeAsBitmap(String str){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getCode:
                showImage();
                break;
            case R.id.scanning:
                customScan();
                break;
        }
    }

    public void customScan(){
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Toast.makeText(this,"内容为空",Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this,"扫描成功",Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                String ScanResult = intentResult.getContents();
                Toast.makeText(this,ScanResult,Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void showImage() {
        String content=mEditText.getText().toString().trim();
        //recode(content);
        try {
            content=new String(content.getBytes("UTF-8"),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(content)){
            Toast.makeText(MainActivity.this,"内容不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        Bitmap bitmap=encodeAsBitmap(content);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes=baos.toByteArray();
        Glide.with(MainActivity.this).load(bytes).into(mImageView);
    }
}
