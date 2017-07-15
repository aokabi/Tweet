package com.aokabi.tweet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.auth.AccessToken;

import static android.speech.SpeechRecognizer.RESULTS_RECOGNITION;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //FIELDS
    private final int REQUEST_ACCESS_TOKEN = 0;
    private final TwitterFactory factory = new TwitterFactory();
    private final Twitter twitter = factory.getInstance();
    private List<String> recData;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted;
                    Log.d("debug", "Permission granted!");
                } else {
                    // Permission denied;
                    Log.d("debug", "Permission denied");
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View oauth_activity = findViewById(R.id.button_oauth_activity);
        oauth_activity.setOnClickListener(this);
        final View speech_recognizer =  findViewById(R.id.button_speech_recognizer);
        speech_recognizer.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

            } else {
              ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_oauth_activity:
            {
                final Intent intent = new Intent(this, AuthTwitterActivity.class);
                intent.putExtra(AuthTwitterActivity.EXTRA_CONSUMER_KEY, getString(R.string.consumerKey));
                intent.putExtra(AuthTwitterActivity.EXTRA_CONSUMER_SECRET, getString(R.string.consumerSecret));
                startActivityForResult(intent, REQUEST_ACCESS_TOKEN);
            }
            break;
            case R.id.button_speech_recognizer:
            {
                Toast.makeText(this, "音声認識開始", LENGTH_SHORT).show();
                final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
                SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(this);
                recognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle bundle) {
                        Log.d("debug", "onReadyForSpeech");
                        Toast.makeText(MainActivity.this, "話してください", LENGTH_SHORT);
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        Log.d("debug", "onBeginningOfSpeech");
                    }

                    @Override
                    public void onRmsChanged(float v) {

                    }

                    @Override
                    public void onBufferReceived(byte[] bytes) {

                    }

                    @Override
                    public void onEndOfSpeech() {
                        Log.d("debug", "onEndOfSpeech");
                    }

                    @Override
                    public void onError(int i) {
                        String reason = "";
                        switch (i) {
                            case SpeechRecognizer.ERROR_AUDIO:
                                reason = "ERROR_AUDIO";
                                break;
                            // Other client side errors
                            case SpeechRecognizer.ERROR_CLIENT:
                                reason = "ERROR_CLIENT";
                                break;
                            // Insufficient permissions
                            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                                reason = "ERROR_INSUFFICIENT_PERMISSIONS";
                                break;
                            // 	Other network related errors
                            case SpeechRecognizer.ERROR_NETWORK:
                                reason = "ERROR_NETWORK";
                        /* ネットワーク接続をチェックする処理をここに入れる */
                                break;
                            // Network operation timed out
                            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                                reason = "ERROR_NETWORK_TIMEOUT";
                                break;
                            // No recognition result matched
                            case SpeechRecognizer.ERROR_NO_MATCH:
                                reason = "ERROR_NO_MATCH";
                                break;
                            // RecognitionService busy
                            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                                reason = "ERROR_RECOGNIZER_BUSY";
                                break;
                            // Server sends error status
                            case SpeechRecognizer.ERROR_SERVER:
                                reason = "ERROR_SERVER";
                        /* ネットワーク接続をチェックをする処理をここに入れる */
                                break;
                            // No speech input
                            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                                reason = "ERROR_SPEECH_TIMEOUT";
                                break;
                        }

                        Log.d("debug", reason);
                    }

                    @Override
                    public void onResults(Bundle bundle) {
                        recData = bundle.getStringArrayList(RESULTS_RECOGNITION);
                        String getData = new String();
                        for (String s: recData) {
                            getData += s + ",";
                        }
                        Log.d("debug", "音声認識成功");
                        Toast.makeText(MainActivity.this, getData, LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPartialResults(Bundle bundle) {

                    }

                    @Override
                    public void onEvent(int i, Bundle bundle) {

                    }
                });
                recognizer.startListening(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ACCESS_TOKEN && resultCode == Activity.RESULT_OK) {
            final String token = data.getStringExtra(AuthTwitterActivity.EXTRA_ACCESS_TOKEN);
            final String token_secret = data.getStringExtra(AuthTwitterActivity.EXTRA_ACCESS_TOKEN_SECRET);
            twitter.setOAuthConsumer(getString(R.string.consumerKey), getString(R.string.consumerSecret));
            twitter.setOAuthAccessToken(new AccessToken(token, token_secret));
        }
    }
}
