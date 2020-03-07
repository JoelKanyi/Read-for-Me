package com.kanyideveloper.readforme;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    EditText textFld;
    Button btn;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textFld = findViewById(R.id.textArea);
        btn = findViewById(R.id.submitBtn);
        tts = new TextToSpeech(this,this);

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
        String text  = textFld.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
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
}
