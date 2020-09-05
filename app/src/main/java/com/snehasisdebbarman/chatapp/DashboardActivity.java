package com.snehasisdebbarman.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //action bar
        actionBar= getSupportActionBar();
       // actionBar.setTitle("Home");
        actionBar.setTitle("Profile");
        firebaseAuth = firebaseAuth.getInstance();



        // init
        //tv=findViewById(R.id.tv);
        navigationView =findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

      /*  HomeFragment homeFragment =new HomeFragment();
        FragmentTransaction ft1= getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,homeFragment,"");
        ft1.addToBackStack(null);
        ft1.commit();
       */
        ProfileFragment profileFragment =new ProfileFragment();
        FragmentTransaction ft2= getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.content,profileFragment,"");
        ft2.addToBackStack(null);
        ft2.commit();



    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                   /*     case R.id.nav_home:
                            actionBar.setTitle("Home");
                            HomeFragment homeFragment =new HomeFragment();
                            FragmentTransaction ft1= getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,homeFragment,"");
                            ft1.commit();
                            return true;

                    */
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            ProfileFragment profileFragment =new ProfileFragment();
                            FragmentTransaction ft2= getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,profileFragment,"");
                            ft2.commit();
                            return true;
                        case R.id.nav_users:
                            actionBar.setTitle("Users");
                            UsersFragment usersFragment =new UsersFragment();
                            FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,usersFragment,"");
                            ft3.commit();
                            return true;
                       /* case R.id.nav_prescription:
                            actionBar.setTitle("Prescription");
                            PrescriptionFragment prescriptionFragment=new PrescriptionFragment();
                            FragmentTransaction ft4=getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,prescriptionFragment,"");
                            ft4.commit();
                            return true;
                            */
                        case R.id.nav_chat:
                            actionBar.setTitle("Chats");
                            ChatFragment chatFragment=new ChatFragment();
                            FragmentTransaction ft5=getSupportFragmentManager().beginTransaction();
                            ft5.replace(R.id.content,chatFragment,"");
                            ft5.commit();
                            return true;
                    }
                    return false;
                }
            };
    private void checkUserStatus(){
        FirebaseUser user =firebaseAuth.getCurrentUser();
        if(user!=null){
            //stay here
        }
        else{
            //no user found go to main
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

   @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
