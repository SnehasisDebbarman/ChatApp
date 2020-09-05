package com.snehasisdebbarman.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUser> userList;

    FirebaseAuth firebaseAuth;


    public UsersFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        firebaseAuth = firebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.user_recyclerView);
       // recyclerView.setElevation((false);


        recyclerView.setHasFixedSize(true);
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.line));
        recyclerView.addItemDecoration(dividerItemDecoration);
         */
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList =new ArrayList<>();

        getAllUser();


        return view;

    }

    private void getAllUser() {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUser modelUser =ds.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }
                    // adapter
                    adapterUser =new AdapterUser(getActivity(),userList);
                    //set adapter into recycler view
                    recyclerView.setAdapter(adapterUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUsers(final String query1) {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            String query=query1;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUser modelUser =ds.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(fUser.getUid())){
                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase())||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){

                                userList.add(modelUser);

                        }
                    }
                    // adapter
                    adapterUser =new AdapterUser(getActivity(),userList);

                    adapterUser.notifyDataSetChanged();
                    //set adapter into recycler view
                    recyclerView.setAdapter(adapterUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void checkUserStatus(){
        FirebaseUser user =firebaseAuth.getCurrentUser();
        if(user!=null){
            //stay here
        }
        else{
            //no user found go to main
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.menu_main,menu);
        MenuItem menuItem =menu.findItem(R.id.action_search);

        SearchView searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);
                }
                else{
                    getAllUser();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);

                }
                else{
                    getAllUser();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
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
