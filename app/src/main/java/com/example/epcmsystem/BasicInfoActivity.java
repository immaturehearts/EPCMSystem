package com.example.epcmsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BasicInfoActivity extends AppCompatActivity {

    EditText name_tv, id_tv, phone_tv, email_tv;
    Spinner gender_spinner;
    Button submit_btn, edit_btn;
    final String PREFS_NAME = "userinfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        name_tv = findViewById(R.id.editTextTextPersonName);
        id_tv = findViewById(R.id.editTextNumber);
        phone_tv = findViewById(R.id.editTextPhone);
        email_tv = findViewById(R.id.editTextEmail);
        gender_spinner = findViewById(R.id.spinner2);
        submit_btn = findViewById(R.id.info_submit);
        edit_btn = findViewById(R.id.info_edit);
        submit_btn.setVisibility(View.VISIBLE);

        inputInfo();
        submit_btn.setOnClickListener(v -> {
            if(TextUtils.isEmpty(name_tv.getText()) || TextUtils.isEmpty(id_tv.getText()) || TextUtils.isEmpty(phone_tv.getText()) || TextUtils.isEmpty(email_tv.getText())){
                AlertDialog alertDialog = new AlertDialog.Builder(BasicInfoActivity.this)
                        .setTitle("信息不全")
                        .setMessage("请填写所有信息后提交")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create();
                alertDialog.show();
            } else if(!isMobile(phone_tv.getText().toString())){
                AlertDialog alertDialog1 = new AlertDialog.Builder(BasicInfoActivity.this)
                        .setTitle("手机号码错误")
                        .setMessage("手机号码格式错误，请重新填写")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create();
                alertDialog1.show();
                phone_tv.requestFocus();
            } else if(!isEmail(email_tv.getText().toString())){
                AlertDialog alertDialog2 = new AlertDialog.Builder(BasicInfoActivity.this)
                        .setTitle("邮箱地址错误")
                        .setMessage("邮箱地址格式错误，请重新填写")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create();
                alertDialog2.show();
                email_tv.requestFocus();
            } else if(!isIDNumber(id_tv.getText().toString())){
                AlertDialog alertDialog3 = new AlertDialog.Builder(BasicInfoActivity.this)
                        .setTitle("身份证号错误")
                        .setMessage("身份证号码格式错误，请重新填写")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create();
                alertDialog3.show();
                id_tv.requestFocus();
            } else {
                basicInfoUpload(email_tv.getText().toString(), gender_spinner.getSelectedItemPosition(),
                        id_tv.getText().toString(), name_tv.getText().toString());
//                saveInfo(name_tv.getText().toString(), gender_spinner.getSelectedItemPosition(),
//                id_tv.getText().toString(), phone_tv.getText().toString(), email_tv.getText().toString());
            }
        });

        edit_btn.setOnClickListener(v -> {
//            SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//            SharedPreferences.Editor editor = userInfo.edit();
//            editor.clear();
//            editor.apply();
            name_tv.setEnabled(true);
            id_tv.setEnabled(true);
            phone_tv.setEnabled(true);
            email_tv.setEnabled(true);
            gender_spinner.setEnabled(true);
            edit_btn.setVisibility(View.GONE);
            submit_btn.setVisibility(View.VISIBLE);
        });
    }

    private void saveInfo(String name, int gender, String id, String phone, String email){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("personName", name);
        editor.putInt("personGender", gender);
        editor.putString("personID", id);
        editor.putString("personPhone", phone);
        editor.putString("personEmail", email);
        editor.apply();
    }

    private void inputInfo(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        name_tv.setText(userInfo.getString("personName", ""));
        gender_spinner.setSelection(userInfo.getInt("personGender", 0));
        id_tv.setText(userInfo.getString("personID", ""));
        phone_tv.setText(userInfo.getString("personPhone", ""));
        email_tv.setText(userInfo.getString("personEmail", ""));

        if(userInfo.contains("personName") && userInfo.contains("personGender") && userInfo.contains("personID")
                && userInfo.contains("personPhone") && userInfo.contains("personEmail")) {
            submit_btn.setVisibility(View.GONE);
            edit_btn.setVisibility(View.VISIBLE);
            name_tv.setEnabled(false);
            id_tv.setEnabled(false);
            phone_tv.setEnabled(false);
            email_tv.setEnabled(false);
            gender_spinner.setEnabled(false);
        }
    }

    //检查手机号码是否合法
    public static boolean isMobile(String mobile) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1、5、8、9]))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    //检查邮箱地址是否合法
    public static boolean isEmail(String email) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    //检查身份证号是否合法
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        boolean matches = IDNumber.matches(regularExpression);
        if (matches) {
            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    return idCardY[idCardMod].equalsIgnoreCase(String.valueOf(idCardLast));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return matches;
    }

//    private void setEditTextEnable(EditText editText, boolean mode){
//        editText.setFocusable(mode);
//        editText.setFocusableInTouchMode(mode);
//        editText.setLongClickable(mode);
//        editText.setInputType(mode ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
//    }

    private void basicInfoUpload(String email, Integer gender, String id_card, String trueName)  {
        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("gender", String.valueOf(gender))
                .add("id_card", id_card)
                .add("true_name", trueName)
                .build();
        Request request = new Request.Builder()
                .url("http://124.70.222.113:9080/user/basicInfo")
                .header("Cookie", "token="+token)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("basicInfo", "onFailure: ");
                e.printStackTrace();
                showResult("基本信息上传失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("basicInfo", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("basicInfo", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    Log.d("basicInfo","基本信息上传成功");
                    saveInfo(trueName, gender, id_card, phone_tv.getText().toString(), email);
                    showResult("基本信息保存成功");
                    finish();
                }
                else{
                    Log.d("basicInfo", "logout failed");
                    showResult("基本信息上传失败");
                }

            }
        });
//        Thread.sleep(500);
    }

    private String getToken(){
        SharedPreferences userInfo = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

    public void showResult(final String msg) {
        runOnUiThread(() -> Toast.makeText(BasicInfoActivity.this, msg, Toast.LENGTH_SHORT).show());
    }
}