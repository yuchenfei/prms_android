package cn.flyingspace.prms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.flyingspace.prms.bean.ItemsResult;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.flyingspace.prms.Setting.API_ITEMS;

public class MainActivity extends BaseActivity {
    private GetItemsTask mGetItemsTask = null;
    private Token token;

    private TextView mInfoView;
    private LinearLayout mDailyLayout;
    private LinearLayout mTempLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = new Token(getApplicationContext());

        mInfoView = (TextView) findViewById(R.id.info);

        mDailyLayout = findViewById(R.id.daily_container);
        mTempLayout = findViewById(R.id.temp_container);

        Button mLogoutButton = findViewById(R.id.log_out_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (token.get().isEmpty()) {
            logout();
            return;
        } else {
            JWT jwt = new JWT(token.get());
            Claim claim = jwt.getClaim("name");
            mInfoView.setText(claim.asString());
        }
        mGetItemsTask = new GetItemsTask();
        mGetItemsTask.execute();
    }


    private void logout() {
        token.clear();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public class GetItemsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("token", token.get())
                    .build();
            Request request = new Request.Builder()
                    .url(API_ITEMS)
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
        protected void onPostExecute(String response) {
            mGetItemsTask = null;

            Gson gson = new Gson();
            ItemsResult result = gson.fromJson(response, ItemsResult.class);

            if (result.isAuth_result()) {
                // 日常签到项目
                mDailyLayout.removeAllViews();
                int times = result.getDaily_times();
                if (times > 0) {
                    String[] time_interval = result.getDaily_time_interval().split(";");
                    for (int i = 0; i < times; i++) {
                        final int index = i + 1;
                        Button btn = new Button(getBaseContext());
                        btn.setText(time_interval[i]);
                        if (i + 1 <= result.getDaily_status()) {
                            btn.setEnabled(false);
                        } else {
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                                    intent.putExtra("type", 1);
                                    intent.putExtra("index", index);
                                    startActivity(intent);
                                }
                            });
                        }
                        mDailyLayout.addView(btn);
                    }
                } else {
                    TextView tv = new TextView(getBaseContext());
                    tv.setText("教师还未设置日常签到");
                    mDailyLayout.addView(tv);
                }
                // 临时签到项目
                mTempLayout.removeAllViews();
                List<Integer> temp_id = result.getTemp_id();
                List<Integer> temp_ok = result.getTemp_ok();
                for (int i = 0; i < temp_id.size(); i++) {
                    final int id = temp_id.get(i);
                    String time = result.getTemp_time().get(i);
                    Button btn = new Button(getBaseContext());
                    btn.setText(time);
                    if (temp_ok.contains(id)) {
                        // 已签到
                        btn.setEnabled(false);
                    } else {
                        // 未签到
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                                intent.putExtra("type", 2);
                                intent.putExtra("index", id);
                                startActivity(intent);
                            }
                        });
                    }
                    mTempLayout.addView(btn);
                }
            } else {
                // Token无效
                Toast.makeText(getBaseContext(), "失败：Token失效，请重新登录", Toast.LENGTH_SHORT).show();
                logout();
            }
        }
    }
}
