package cn.flyingspace.prms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private GetItemsTask mGetItemsTask = null;
    private Token token;

    private TextView mInfoView;
    private Button mFCheckInButton;
    private Button mFCheckOutButton;
    private Button mACheckInButton;
    private Button mACheckOutButton;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = new Token(getApplicationContext());

        mInfoView = (TextView) findViewById(R.id.info);

        mLayout = findViewById(R.id.meeting_container);

        mFCheckInButton = findViewById(R.id.f_check_in_button);
        mFCheckInButton.setEnabled(false);
        mFCheckInButton.setOnClickListener(this);

        mFCheckOutButton = findViewById(R.id.f_check_out_button);
        mFCheckOutButton.setEnabled(false);
        mFCheckOutButton.setOnClickListener(this);

        mACheckInButton = findViewById(R.id.a_check_in_button);
        mACheckInButton.setEnabled(false);
        mACheckInButton.setOnClickListener(this);

        mACheckOutButton = findViewById(R.id.a_check_out_button);
        mACheckOutButton.setEnabled(false);
        mACheckOutButton.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int index = 0;
        switch (v.getId()) {
            case R.id.f_check_in_button:
                index = 1;
                break;
            case R.id.f_check_out_button:
                index = 2;
                break;
            case R.id.a_check_in_button:
                index = 3;
                break;
            case R.id.a_check_out_button:
                index = 4;
                break;
        }
        Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("index", index);
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
                int index = result.getIndex();
                switch (index) {
                    case 0:
                        mFCheckInButton.setEnabled(true);
                        break;
                    case 1:
                        mFCheckInButton.setEnabled(false);
                        mFCheckInButton.setText("已签");
                        mFCheckOutButton.setEnabled(true);
                        break;
                    case 2:
                        mFCheckInButton.setEnabled(false);
                        mFCheckInButton.setText("已签");
                        mFCheckOutButton.setEnabled(false);
                        mFCheckOutButton.setText("已签");
                        mACheckInButton.setEnabled(true);
                        break;
                    case 3:
                        mFCheckInButton.setEnabled(false);
                        mFCheckInButton.setText("已签");
                        mFCheckOutButton.setEnabled(false);
                        mFCheckOutButton.setText("已签");
                        mACheckInButton.setEnabled(false);
                        mACheckInButton.setText("已签");
                        mACheckOutButton.setEnabled(true);
                        break;
                    case 4:
                        mFCheckInButton.setEnabled(false);
                        mFCheckInButton.setText("已签");
                        mFCheckOutButton.setEnabled(false);
                        mFCheckOutButton.setText("已签");
                        mACheckInButton.setEnabled(false);
                        mACheckInButton.setText("已签");
                        mACheckOutButton.setEnabled(false);
                        mACheckOutButton.setText("已签");
                        break;
                }
                mLayout.removeAllViews();
                List<Integer> meeting_list = result.getMeeting_index();
                List<Integer> meeting_ok = result.getMeeting_ok();
                for (int i = 0; i < meeting_list.size(); i++) {
                    final int meeting_index = meeting_list.get(i);
                    String time = result.getMeeting_time().get(i);
                    Button btn = new Button(getBaseContext());
                    btn.setText(time);
                    if (meeting_ok.contains(meeting_index)) {
                        // 已签到
                        btn.setEnabled(false);
                    } else {
                        // 未签到
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                                intent.putExtra("type", 2);
                                intent.putExtra("index", meeting_index);
                                startActivity(intent);
                            }
                        });
                    }
                    mLayout.addView(btn);
                }

            } else {
                Toast.makeText(getBaseContext(), "失败：Token失效，请重新登录", Toast.LENGTH_SHORT).show();
                logout();
            }
        }
    }
}
