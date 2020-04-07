package com.kanyideveloper.readforme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;
import java.io.*;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button btn,btn2;
    TextToSpeech tts;
    TextView tvFile;
    String fileName;
    String textToSpeak;
    private static final int NO_OF_FILES = 0;
    private static  final int PERMISSION_REQUEST_STORAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
         != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

        btn = findViewById(R.id.submitBtn);
        btn2 = findViewById(R.id.chooseBtn);
        tts = new TextToSpeech(this,this);
        tvFile = findViewById(R.id.file);
        tvFile.setMovementMethod(new ScrollingMovementMethod());

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExFilePicker exFilePicker = new ExFilePicker();
                exFilePicker.start(MainActivity.this,NO_OF_FILES);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speakOut(){
        tts.speak(textToSpeak,TextToSpeech.QUEUE_FLUSH,null);
    }

    public void readContent() {
        File fl = new File(fileName);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(fl));

            String line = null;

            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }

            bf.close();
            
             textToSpeak= sb.toString();
            tvFile.setText(sb.toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_STORAGE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Permission granted",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this,"permission not granted",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.ENGLISH);
            if(result == TextToSpeech.LANG_MISSING_DATA || result ==TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(MainActivity.this,"Language not found",Toast.LENGTH_SHORT).show();
            }
            else {
                btn.setEnabled(true);
                speakOut();
            }
        }
        else {
            Toast.makeText(MainActivity.this,"Initialization failed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NO_OF_FILES ){
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                fileName = (result.getPath()+stringBuilder.toString());
                readContent();
            }
        }
    }
    }
