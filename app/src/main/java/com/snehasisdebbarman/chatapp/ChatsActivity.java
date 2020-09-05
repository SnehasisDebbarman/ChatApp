package com.snehasisdebbarman.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatsActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView1;
    TextView nameTv,statusUpdateTv;
    ImageView profileIv;
    EditText messageEt;
    ImageButton sendBtn;
    //declare firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;
    DatabaseReference userRefForSeen;
   // for checking the user  seen msg or not
   ValueEventListener seenListener;

   List<ModelChat> chatList;
   AdapterChat adapterChat;

    String hisUID,myUID;
    String hisImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        //init views
        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        recyclerView1=findViewById(R.id.recyclerView1);
        nameTv=findViewById(R.id.nameTv);
        statusUpdateTv=findViewById(R.id.userStatusTv);
        profileIv=findViewById(R.id.profileIv);
        messageEt=findViewById(R.id.messageEt);
        sendBtn= findViewById(R.id.sendBtn1);
        //Linear layout for Recycler View
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //prpperties of Recycler view
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(linearLayoutManager);





        //clck to get other person user id
        Intent intent=getIntent();
        hisUID=intent.getStringExtra("HisUid");
        //init firebase
        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        userDbRef=firebaseDatabase.getReference("users");
        Query userQuery =userDbRef.orderByChild("uid").equalTo(hisUID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    hisImage=""+ds.child("image").getValue();
                    //set data on activity_chats.xml
                    nameTv.setText(name);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_default_color).into(profileIv);

                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_color).into(profileIv);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message= messageEt.getText().toString().trim();
                //check msg empty or not
                if(TextUtils.isEmpty(message)){
                    // msg is empty
                    Toast.makeText(ChatsActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();

                }
                else {
                    sendMessage(message);

                }

            }
        });
        readMessages();

        seenMessage();



    }

    private void seenMessage() {
        userRefForSeen =FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    HashMap<String,Object> hasSeenHashMap = new HashMap<>();
                    hasSeenHashMap.put("isSeen",true);
                    ds.getRef().updateChildren(hasSeenHashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUID)&&chat.getSender().equals(hisUID) ||
                            chat.getReceiver().equals(hisUID)&&chat.getSender().equals(myUID)){
                        chatList.add(chat);

                    }
                    //adapter
                    adapterChat=new AdapterChat(ChatsActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    //adapter to recycler view
                    recyclerView1.setAdapter(adapterChat);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {
        //add to database
        /*
       "chats" node created..
       hashMap.put("sender",myUID);
        hashMap.put("receiver",hisUID);
        hashMap.put("message",message);
        above lines keep record of chats..


         */
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("sender",myUID);
        hashMap.put("receiver",hisUID);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);

        // reset edit text
        messageEt.setText("");
    }


    private void checkUserStatus(){
        FirebaseUser user =firebaseAuth.getCurrentUser();
        if(user!=null){
            //stay here
            myUID=user.getUid();// get current user uid

        }
        else{
            //no user found go to main
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_search).setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if (id==R.id.logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
