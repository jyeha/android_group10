package edu.skku.team10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText email, pw;
    Button login, signup;
    String T_email, T_pw;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        login = (Button)findViewById(R.id.button);
        signup = (Button)findViewById(R.id.button2);
        email = (EditText)findViewById(R.id.editText);
        pw = (EditText)findViewById(R.id.editText2);

        /*logout = (Button)findViewById(R.id.button4);
        firebaseAuth = FirebaseAuth.getInstance();
        Toast.makeText(GroundActivity.this, firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                intent = new Intent(GroundActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/

        if(firebaseAuth.getCurrentUser() != null) {
            intent = new Intent(MainActivity.this, GroundActivity.class);
            startActivity(intent);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                T_email = email.getText().toString();
                T_pw = pw.getText().toString();
                //Log.d("확인", "1");
                //Log.d("확인", T_email);
                //Log.d("확인", T_pw);
                if(T_email.length() * T_pw.length() == 0){
                    Toast.makeText(MainActivity.this, "이메일 및 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.signInWithEmailAndPassword(T_email, T_pw).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Log.d("확인", "2");
                            if(task.isSuccessful()){
                                //Log.d("확인", "3");
                                intent = new Intent(MainActivity.this, GroundActivity.class);
                                //intent.putExtra("my_email", T_email);
                                Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                            else{
                                //Log.d("확인", "4");
                                Toast.makeText(MainActivity.this, "이메일 및 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, Sign_up.class);
                startActivity(intent);
            }
        });
    }
}
