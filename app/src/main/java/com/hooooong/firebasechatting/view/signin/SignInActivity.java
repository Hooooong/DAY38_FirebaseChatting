package com.hooooong.firebasechatting.view.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hooooong.firebasechatting.R;
import com.hooooong.firebasechatting.model.User;
import com.hooooong.firebasechatting.util.VerificationUtil;
import com.hooooong.firebasechatting.view.custom.SignButton;
import com.hooooong.firebasechatting.view.room.RoomListActivity;
import com.hooooong.firebasechatting.view.signup.DetailSignUpActivity;

/**
 * Created by Android Hong on 2017-11-01.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean emailCheck, passwordCheck;

    private static final int RC_SIGN_IN = 999;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    private Toolbar toolbar;
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText editEmail, editPassword;
    private SignInButton btnGoogleSignIn;
    private ProgressBar progressBar;
    private SignButton btnSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initFirebaseAndGooglApi();
        initView();
    }

    private void initFirebaseAndGooglApi() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignInActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        btnSignIn = findViewById(R.id.btnSignIn);

        editEmail = findViewById(R.id.editEmail);
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailCheck = VerificationUtil.isValidEmail(s.toString());
                enableSignupButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPassword = findViewById(R.id.editPassword);
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() >= 8){
                    passwordCheck = true;
                }else{
                    passwordCheck = false;
                }
                enableSignupButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        progressBar = findViewById(R.id.progressBar);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE);
        btnGoogleSignIn.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {

            String email = intent.getStringExtra("EMAIL");
            editEmail.setText(email);

            if (!TextUtils.isEmpty(email)) {
                editPassword.requestFocus();
            }
        }
    }

    /**
     * 로그인 메소드
     *
     * @param view
     */
    public void signIn(View view){

        final String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        emailLayout.setError(null);
        passwordLayout.setError(null);

        if(TextUtils.isEmpty(email) ){
            emailLayout.setError("Email 을 입력해주시기 바랍니다.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordLayout.setError("Password 를 입력해주시기 바랍니다.");
            return;
        }

        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // 이메일 검증 확인
                            if (user.isEmailVerified()) {
                                checkUserDetailInfo(user);
                            } else {
                                hideProgressBar();
                                // 이메일을 확인하셔야 됩니다.
                                Toast.makeText(SignInActivity.this, "Email 을 검증해주시기 바랍니다.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            hideProgressBar();
                            Toast.makeText(SignInActivity.this, "Email 또는 Password 가 틀렸습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Appbar 메뉴 생성
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
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
            case R.id.action_close:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * GoogleSignIn 버튼 눌렀을 경우
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGoogleSignIn:
                googleSignin();
                break;
        }
    }

    public void googleSignin(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                showProgressBar();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Google SignIn 실패!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUserDetailInfo(user);
                        } else {
                            hideProgressBar();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserDetailInfo(final FirebaseUser user) {
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressBar();

                if (dataSnapshot.getChildrenCount() == 0) {
                    /**
                     *  1. database Email 저장
                     */
                    User data = new User();
                    data.setEmail(user.getEmail());
                    userRef.child(user.getUid()).setValue(data);

                    /**
                     *  2. 추가적인 정보를 저장하는 페이지로 이동
                     */
                    Intent intent = new Intent(SignInActivity.this, DetailSignUpActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    User data = dataSnapshot.getValue(User.class);
                    if (data.getName() == null || "".equals(data.getName())) {
                        // 기본정보를 입력을 히지 않았으면
                        /**
                         *   추가적인 정보를 저장하는 페이지로 이동
                         */
                        Intent intent = new Intent(SignInActivity.this, DetailSignUpActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        // 입력하였으면
                        // 2. updateChildren 메소드를 통해
                        // 'User/id에 맞는' Node 를 참조하여 값을 수정한다.
                        userRef.child(user.getUid()).child("token").setValue(refreshedToken);
                        // 다음 페이지로 이동
                        Intent intent = new Intent(SignInActivity.this, RoomListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    private void enableSignupButton(){
        if(emailCheck && passwordCheck){
            btnSignIn.checkUseable(true);
        }else{
            btnSignIn.checkUseable(false);
        }
    }


}

