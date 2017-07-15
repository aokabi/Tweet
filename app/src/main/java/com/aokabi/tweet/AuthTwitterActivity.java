package com.aokabi.tweet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by aokabi on 2017/07/15.
 */

public class AuthTwitterActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String EXTRA_CONSUMER_KEY = "consumer_key";
    public final static String EXTRA_CONSUMER_SECRET = "consumer_secret";
    public final static String EXTRA_ACCESS_TOKEN = "access_token";
    public final static String EXTRA_ACCESS_TOKEN_SECRET = "access_token_secret";

    private RequestToken _mRequestToken;
    final AsyncTwitterFactory factory = new AsyncTwitterFactory();
    final AsyncTwitter twitter = factory.getInstance();

    private final TwitterListener twitterlistener = new TwitterAdapter(){
        @Override
        public void gotOAuthRequestToken(RequestToken token) {
            Log.d("debug", "success");
            _mRequestToken = token;
        }

        @Override
        public void gotOAuthAccessToken(AccessToken token) {
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_ACCESS_TOKEN, token.getToken());
            intent.putExtra(EXTRA_ACCESS_TOKEN_SECRET, token.getTokenSecret());
            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        // Request Token をリクエスト
        final Intent intent = getIntent();
        final String consumer_key = intent.getStringExtra(EXTRA_CONSUMER_KEY);
        final String consumer_key_secret = intent.getStringExtra(EXTRA_CONSUMER_SECRET);
        twitter.addListener(twitterlistener);
        twitter.setOAuthConsumer(consumer_key, consumer_key_secret);
        twitter.getOAuthRequestTokenAsync();

        //EventListener をセット
        final View start_login = findViewById(R.id.button_start_login);
        start_login.setOnClickListener(this);
        final View login = findViewById(R.id.button_login);
        login.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button_start_login:
            {
                //認証画面
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_mRequestToken.getAuthorizationURL()));
                startActivity(intent);
            }
            break;
            case R.id.button_login:
            {
                //pinコードを取得
                final EditText editPin = (EditText) findViewById(R.id.edit_pin_code);
                final String pin = editPin.getText().toString();
                //Access Tokenをリクエスト
                twitter.getOAuthAccessTokenAsync(_mRequestToken, pin);
            }
            break;
        }
    }
}
