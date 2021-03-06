package edu.skku.team10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private DatabaseReference mPostReference;
    FirebaseAuth firebaseAuth;
    EditText email, pw;
    Button signup;
    String T_email, T_pw;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        mPostReference = FirebaseDatabase.getInstance().getReference();

        signup = (Button)findViewById(R.id.button3);
        email = (EditText)findViewById(R.id.editText);
        pw = (EditText)findViewById(R.id.editText2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                T_email = email.getText().toString();
                T_pw = pw.getText().toString();

                if(T_email.length() * T_pw.length() == 0){
                    Toast.makeText(SignUp.this, "이메일 및 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(T_email, T_pw).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                intent = new Intent(SignUp.this, MainActivity.class);
                                Toast toast = Toast.makeText(SignUp.this, "회원가입 완료", Toast.LENGTH_SHORT);
                                //TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                //if( v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                                Push_init();
                                startActivity(intent);
                                finish();
                            }
                            else{
                                //Log.d("확인", "4");
                                Toast.makeText(SignUp.this, "email형식이 맞지 않거나\n이미 존재하는 email입니다.\n비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void Push_init(){
        UserInfo newuser = new UserInfo(1);

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        postValues = newuser.toMap();
        String [] parsing = T_email.split("@");
        childUpdates.put("/UserInfo/" + parsing[0], postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
