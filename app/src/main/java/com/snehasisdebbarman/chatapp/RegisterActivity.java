package com.snehasisdebbarman.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mNameET,mEmailEt,mPassEt;
    Button mRegisterBtn;
    ProgressDialog mProgressDialog;
    TextView Have_account;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //action
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        mEmailEt=findViewById(R.id.emailET);
        mPassEt=findViewById(R.id.passET);
        mRegisterBtn=findViewById(R.id.registerBtn);
        Have_account=findViewById(R.id.have_Account);
        mNameET=findViewById(R.id.nameET);


        mProgressDialog =new ProgressDialog(this);
        mProgressDialog.setMessage("registering user......");
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmailEt.getText().toString().trim();
                String password=mPassEt.getText().toString().trim();
                //name

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                }
                else if (password.length()<6){
                    mPassEt.setError("Invalid Password");
                    mPassEt.setFocusable(true);

                }
                else{
                    registerUser(email,password);
                }
            }
        });
        //handle account
        Have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });


    }

    private void registerUser(String email, String password) {
        mProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success , dis,iss dialog start  register activity
                            mProgressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email= user.getEmail();
                            String uid =user.getUid();
                            // get name from Edit Text
                            String name=mNameET.getText().toString().trim();


                            HashMap<Object,String> hashMap =new HashMap<>();

                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name",name);// will add later one edit profile
                            hashMap.put("phone","");// will add later one edit profile
                            hashMap.put("image","");
                            hashMap.put("details","");// will add later one edit profile// will add later one edit profile

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            // path to store in 'Users'
                            DatabaseReference reference = database.getReference("users");
                            // put data in hasmap
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this,"Registered.....\n"+user.getEmail(),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                           mProgressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();// go prev
        return super.onSupportNavigateUp();
    }
}
