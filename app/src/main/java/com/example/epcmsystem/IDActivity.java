package com.example.epcmsystem;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class IDActivity extends AppCompatActivity {

    Button buttonOne;
    Button buttonTwo;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idactivity);

        buttonOne = findViewById(R.id.activity_main_bt_one);
        buttonTwo = findViewById(R.id.activity_main_bt_two);
        textView = findViewById(R.id.activity_main_tv);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.id);
                idCardRecognition(bitmap);
            }
        });
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.id2);
                idCardRecognition(bitmap);
            }
        });
    }

    public void idCardRecognition(Bitmap bitmap) {
        String appcode = "8GkwS7vvDow9tTYlZtrxtoWF4QEN9b";
        String body = "{\n" +
                "\t\"image\":  \"" + base64ToNoHeaderBase64(bitmapToBase64(bitmap)) + "\",\n" +
                "\t\"configure\": \"{\\\"side\\\":\\\"face\\\"}\" \n" +
                "}";
        final RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), body);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .baseUrl("https://dm-51.data.aliyun.com/")//?????????????????????
                .build();
        IdTestService idTestService = retrofit.create(IdTestService.class);
        Call<ResponseBody> call = idTestService.getTestResult(requestBody, "APPCODE " + appcode);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    textView.setText(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("123", t.getMessage());
            }
        });

    }

    /**
     * ???Bitmap?????????Base64?????????
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);//??????100???????????????
        byte[] bytes = bos.toByteArray();
        //????????????base64??????????????????????????????NO_WRAP??????????????????????????????
        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        //????????????base64???????????????????????????????????????NO_WRAP??????????????????????????????
        //return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * ???base64????????????
     *
     * @param base64
     * @return
     */
    public static String base64ToNoHeaderBase64(String base64) {
        return base64.replace("data:image/jpeg;base64,", "");
    }

    public interface IdTestService {
        @POST("rest/160601/ocr/ocr_idcard.json")
            //????????????
        Call<ResponseBody> getTestResult(@Body RequestBody body,
                                         @Header("Authorization") String authorization
        );
    }
}