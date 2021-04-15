package com.example.dechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userET, passET, emailET;
    Button registerBtn;

    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // onCreate metodu uygulamayı başlattığımızda hazır olacak metodlar için
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userET = findViewById(R.id.userEditText);
        passET = findViewById(R.id.editText2);
        emailET = findViewById(R.id.emailEditText);
        registerBtn = findViewById(R.id.buttonRegister);


        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {   // Tıklandığında

                String username_text = userET.getText().toString();
                String email_text    = emailET.getText().toString();
                String pass_text     = passET.getText().toString();

                if (TextUtils.isEmpty(username_text) || TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)){
                    Toast.makeText(RegisterActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }else{
                    RegisterNow(username_text, email_text , pass_text );
                }
            }
        });

    }

    private void RegisterNow(final String username, String email, String password){         // register metodu

        auth.createUserWithEmailAndPassword(email, password)            // email ve şifre kullanıcıdan alınır ve yeni hesabı oluşturulur
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            myRef = FirebaseDatabase.getInstance().getReference("MyUsers")
                                    .child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status" , "offline");



                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {   // işlem tamam ise main activity e kullanıcı gönderilir
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            });


                        }else{
                            Toast.makeText(RegisterActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                        }



                    }
                });




    }


}


