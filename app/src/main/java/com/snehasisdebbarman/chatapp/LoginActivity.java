package com.snehasisdebbarman.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText mEmailEt,mPassEt;
    Button mLoginBtn;
    ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    TextView have_account,forget_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("LOGIN");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mEmailEt=findViewById(R.id.emailET);
        mPassEt=findViewById(R.id.passET);
        mLoginBtn=findViewById(R.id.loginBtn);
        have_account=findViewById(R.id.have_Account);
        forget_pass=findViewById(R.id.forget_password);

        mProgressDialog =new ProgressDialog(this);
        mProgressDialog.setMessage("Logging......");

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmailEt.getText().toString();
                String password=mPassEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                }
                else{
                    loginUser(email,password);
                }
            }
        });


        have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //layout
        LinearLayout linearLayout= new LinearLayout(this);

        final EditText emailET=new EditText(this);
        emailET.setHint("Email");
        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailET.setMinEms(16);
        linearLayout.addView(emailET);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email= emailET.getText().toString().trim();
                beginRecovery(email);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();

    }

    private void beginRecovery(String email) {
        mProgressDialog.setMessage("Sending......");
        mProgressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loginUser(String email, String password) {
        mProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                String email= user.getEmail();
                                String uid =user.getUid();
                                HashMap<Object,String> hashMap =new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");// will add later one edit profile
                                hashMap.put("phone","");// will add later one edit profile
                                hashMap.put("image","");// will add later one edit profile
                                hashMap.put("details","");// will add later one edit profile
                                //firebase database instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                // path to store in 'Users'
                                DatabaseReference reference = database.getReference("users");
                                // put data in hasmap
                                reference.child(uid).setValue(hashMap);


                                //

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            mProgressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();// go prev
        return super.onSupportNavigateUp();
    }
}
