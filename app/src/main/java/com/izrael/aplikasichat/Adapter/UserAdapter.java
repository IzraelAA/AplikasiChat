package com.izrael.aplikasichat.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.izrael.aplikasichat.MassageActivity;
import com.izrael.aplikasichat.R;
import com.izrael.aplikasichat.User;

import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context    context;
    private List<User> users;
    private boolean    ischat;
    String theLastMsg;

    public UserAdapter(Context contextt, List<User> userss, boolean ischat) {
        this.context = contextt;
        this.users = userss;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_users, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        if (user.getImageUrl().equals("default")) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageUrl()).into(holder.imageView);
        }
        if (ischat){
            lastMassage(user.getId(),holder.lmg);
        }else {
            holder.lmg.setVisibility(View.GONE);
        }
        if (ischat) {
            if (user.getStatus().equals("online")) {
                holder.imgon.setVisibility(View.VISIBLE);
                holder.imgoff.setVisibility(View.GONE);
            } else {
                holder.imgon.setVisibility(View.GONE);
                holder.imgoff.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imgon.setVisibility(View.GONE);
            holder.imgoff.setVisibility(View.GONE);
        }
        holder.username.setText(user.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MassageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public  TextView  username;
        public  ImageView imageView;
        private ImageView imgon, imgoff;
        private TextView lmg;

        private ViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.textUsername);
            imageView = view.findViewById(R.id.userImageMenu);
            imgon = view.findViewById(R.id.imgeon);
            imgoff = view.findViewById(R.id.imgeoff);
            lmg = view.findViewById(R.id.lmg);

        }
    }

    private void lastMassage(final String userid,final TextView lmg) {
        theLastMsg = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference  reference    = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReciver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMsg = chat.getMessage();
                    }
                }
                switch (theLastMsg){
                    case "default":
                        lmg.setText("No Message");
                        break;

                        default:
                            lmg.setText(theLastMsg);
                            break;
                }
                theLastMsg = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
