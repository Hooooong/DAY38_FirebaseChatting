package com.hooooong.firebasechatting.view.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hooooong.firebasechatting.R;
import com.hooooong.firebasechatting.model.User;
import com.hooooong.firebasechatting.util.DialogUtil;
import com.hooooong.firebasechatting.view.custom.SignButton;
import com.hooooong.firebasechatting.view.room.RoomListActivity;

public class DetailSignUpActivity extends AppCompatActivity {

    private boolean nameCheck, phoneNumberCheck;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EditText editName, editPhoneNumber;
    private SignButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sign_up);


        initFirebase();
        initView();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        btnSignUp = findViewById(R.id.btnSignUp);
        editName = findViewById(R.id.editName);
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    nameCheck = true;
                }else{
                    nameCheck = false;
                }
                enableSignupButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 13){
                    phoneNumberCheck  = true;
                }else{
                    phoneNumberCheck = false;
                }
                enableSignupButton();
            }
        });
    }

    private void enableSignupButton() {
        if (nameCheck && phoneNumberCheck ) {
            btnSignUp.checkUseable(true);
        } else {
            btnSignUp.checkUseable(false);
        }
    }

    public void detailSignUp(View view){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            User data = new User();
            data.setName(editName.getText().toString());
            data.setPhoneNumber(editPhoneNumber.getText().toString());
            data.setEmail(user.getEmail());
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            data.setToken(refreshedToken);

            userRef.child(user.getUid()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(DetailSignUpActivity.this, "회원가입이 완료되었습니다.",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(DetailSignUpActivity.this, RoomListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(DetailSignUpActivity.this, "회원가입에 실패하였습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            DialogUtil.showDialogPopUp("오류","잘못된 방식으로 접근하였습니다. 다시 시도해 주세요.", DetailSignUpActivity.this, true);
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Appbar 메뉴 선택 이벤트
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
