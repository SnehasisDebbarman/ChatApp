package com.snehasisdebbarman.chatapp;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser  extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUser> userList;

    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID= userList.get(position).getUid();
        String userImage =userList.get(position).getImage();
        String userName =userList.get(position).getName();
        final String userEmail =userList.get(position).getEmail();

        holder.mEmailTv.setText(userEmail);
        holder.mNameTv.setText(userName);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_color)
                    .into(holder.mAvatarIv);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            ;
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,ChatsActivity.class);
                intent.putExtra("HisUid",hisUID);
                context.startActivity(intent);
                //Toast.makeText(context,""+userEmail,Toast.LENGTH_SHORT);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIv;
        TextView mNameTv,mEmailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarIv=itemView.findViewById(R.id.avatarIv);
            mNameTv=itemView.findViewById(R.id.nameTv);
            mEmailTv=itemView.findViewById(R.id.emailTv);
        }
    }

}
