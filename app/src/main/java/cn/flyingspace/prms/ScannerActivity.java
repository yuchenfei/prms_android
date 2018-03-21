package cn.flyingspace.prms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.util.List;

import cn.flyingspace.prms.bean.CheckInResult;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.flyingspace.prms.RSA.encryptData;
import static cn.flyingspace.prms.Setting.API_CHECKIN;


public class ScannerActivity extends BaseActivity {
    private CheckInTask mCheckInTask = null;
    private int type = 0;
    private int index = 0;
    private Token token;
    private String lastText;

    private DecoratedBarcodeView barcodeView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                return;
            }

            pause(barcodeView);
            mCheckInTask = new CheckInTask();
            mCheckInTask.execute(result.getText());

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        index = intent.getIntExtra("index", 0);

        token = new Token(getApplicationContext());

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public class CheckInTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("token", token.get())
                    .add("code", params[0])
                    .add("type", String.valueOf(type))
                    .add("index", String.valueOf(index))
                    .build();
            Request request = new Request.Builder()
                    .url(API_CHECKIN)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String response) {
            mCheckInTask = null;

            Gson gson = new Gson();
            CheckInResult result = gson.fromJson(response, CheckInResult.class);

            if (result.isAuth_result()) {
                switch (result.getStatus_code()) {
                    case -1:
                        // 二维码不匹配
                        Toast.makeText(getBaseContext(), "签到失败", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case -2:
                        // 时间不符
                        Toast.makeText(getBaseContext(), "时间不符", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 0:
                        // 长期二维码，等待二维码刷新，继续扫描
                        resume(barcodeView);
                        break;
                    case 1:
                        // 短期二维码，签到成功
                        Toast.makeText(getBaseContext(), "签到成功", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            } else {
                // token失效
                Toast.makeText(getBaseContext(), "失败：Token失效，请重新登录", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
