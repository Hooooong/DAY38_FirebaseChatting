package com.hooooong.firebasechatting.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hooooong.firebasechatting.R;
import com.hooooong.firebasechatting.model.User;
import com.hooooong.firebasechatting.view.room.RoomListActivity;
import com.hooooong.firebasechatting.view.signin.SignInActivity;
import com.hooooong.firebasechatting.view.signup.SignUpActivity;
import com.matthewtamlin.sliding_intro_screen_library.DotIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    private ViewPager viewPager;
    private Button btnSignIn, btnSignUp;
    private DotIndicator indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initView();
        setViewPager();
    }

    FirebaseAuth.AuthStateListener  mStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (user.isEmailVerified()) {
                    checkUserDetailInfo(user);
                }
            }
        }
    };

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
    }

    private void checkUserDetailInfo(final FirebaseUser user) {
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    User data = dataSnapshot.getValue(User.class);
                    if(data.getName() != null && !"".equals(data.getName())){

                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        // 입력하였으면
                        // 2. updateChildren 메소드를 통해
                        // 'User/id에 맞는' Node 를 참조하여 값을 수정한다.
                        userRef.child(user.getUid()).child("token").setValue(refreshedToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mStateListener);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        indicator = findViewById(R.id.indicator);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }


    private void setViewPager() {
        MainAdapter mainAdapter = new MainAdapter(this);
        viewPager.setAdapter(mainAdapter);
        indicator.setNumberOfItems(mainAdapter.getCount());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator.setSelectedItem( viewPager.getCurrentItem(), true );
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSignIn:
                intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSignUp:
                intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mStateListener != null){
            mAuth.removeAuthStateListener(mStateListener);
        }

    }
}