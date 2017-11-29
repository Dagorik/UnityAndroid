package com.unity.sampleassets;

import com.unity3d.player.*;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class UnityPlayerActivity extends Activity {
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private TextToSpeech textToSpeech;


    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        Log.e("YA!!", "AQUI PUEDO HACER MI DESMADRE!");

    }


    private void pedirpermiso() {

        String[] permission = new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS)) {
            ActivityCompat.requestPermissions(this, permission, 300);

        }

    }

    private void initializeTextToSpeech(final String message) {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
//                    textToSpeech.setLanguage(new Locale("es", "MX"));
                    textToSpeech.setLanguage(Locale.getDefault());
                    Log.e("Listo", "YA INICIALZO");
                    textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);

                }
            }
        });
    }


    public void showSoundMessage(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == 300) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //YA se aceptó el permiso
                return;
            }
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS)) {

            ActivityCompat.requestPermissions(this, permissions, 300);

        } else {
            //TODO Indicar al usuario de qudebe de ir a configuraciones y activar el permiso manualmente

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Entre al result", requestCode + " " + resultCode);
        switch (requestCode) {
            case 200: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Log.e("Awebooo", result.get(0));
                    if (result.get(0).equalsIgnoreCase("hola")) {
                        initializeTextToSpeech("Hola, soy tu asistente personal. En que puedo ayudarte?");
                    } else if (result.get(0).equalsIgnoreCase("dime mi saldo")) {
                        initializeTextToSpeech("Tu saldo es de doscientos pesos");
                    } else if (result.get(0).equalsIgnoreCase("Quiero chatear con un agente")) {
                        initializeTextToSpeech("Dame un segundo");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openChatBot();
                            }
                        }, 3000);   //finish();

                    }
                    //showSoundMessage(result.get(0));
                }
                break;
            }

        }
    }


    public void openChatBot() {
        Uri chatbotUri = Uri.parse("https://m.me/217304995439503");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(chatbotUri);
        startActivity(intent);
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "DIME ALGO");
        try {
            startActivityForResult(intent, 200);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "ESTE DISPOSITIVO NO SOPORTA ENTRADAS DE AUDIO",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RESUME", "NO SE QUE PEDO");

        mUnityPlayer.resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("START", "NO SE QUE PEDO");

        mUnityPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.stop();
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("onConfigurationChanged", "NO SE QUE PEDO");
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("onWindowFocusChanged", "NO SE QUE PEDO");

        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("dispatchKeyEvent", event.toString());

        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("onKeyUp", event.toString());

        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("OnKeyDown", event.toString());
        promptSpeechInput();
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("onTouchEvent", event.toString());
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.e("onGenericMotionEvent", event.toString());

        return mUnityPlayer.injectEvent(event);
    }
}
