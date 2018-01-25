package cn.flyingspace.prms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private GetInfoTask mGetInfoTask = null;

    private Token token;

    private TextView mInfoView;
    private TextView mDetailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = new Token(getApplicationContext());

        mInfoView = (TextView) findViewById(R.id.info);
        mDetailView = (TextView) findViewById(R.id.detail);

        Button mGetDetailButton = findViewById(R.id.get_detail_button);
        mGetDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetInfoTask = new GetInfoTask();
                mGetInfoTask.execute((Void) null);
            }
        });

        Button mLogoutButton = findViewById(R.id.log_out_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (token.get().isEmpty()) {
            logout();
        } else {
            JWT jwt = new JWT(token.get());
            Claim claim = jwt.getClaim("name");
            mInfoView.setText(claim.asString());
        }
    }



    private void logout() {
        token.clear();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public class GetInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("token", token.get())
                    .build();
            Request request = new Request.Builder()
                    .url("http://192.168.1.223:8000/info")
                    .post(body)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            mGetInfoTask = null;
            if (email.isEmpty()) {
                // token失效
                logout();
            } else {
                mDetailView.setText("邮箱:" + email);
            }
        }

        @Override
        protected void onCancelled() {
            mGetInfoTask = null;
        }
    }
}
