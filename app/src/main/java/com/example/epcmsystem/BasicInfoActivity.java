package com.example.epcmsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class BasicInfoActivity extends AppCompatActivity {

    EditText name_tv, id_tv, phone_tv, email_tv;
    Spinner gender_spinner;
    Button submit_btn;
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
            } else {
                saveInfo(name_tv.getText().toString(), gender_spinner.getSelectedItemPosition(), id_tv.getText().toString(), phone_tv.getText().toString(), email_tv.getText().toString());
                showResult("基本信息保存成功");
                finish();
            }
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
        name_tv.setText(userInfo.getString("personName", "Name"));
        gender_spinner.setSelection(userInfo.getInt("personGender", 0));
        id_tv.setText(userInfo.getString("personID", "ID card"));
        phone_tv.setText(userInfo.getString("personPhone", "Phone number"));
        email_tv.setText(userInfo.getString("personEmail", "Email address"));
    }

    public void showResult(final String msg) {
        runOnUiThread(() -> Toast.makeText(BasicInfoActivity.this, msg, Toast.LENGTH_SHORT).show());
    }
}