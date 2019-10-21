package com.votingsystem.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.votingsystem.R;
import com.votingsystem.db.model.User;
import com.votingsystem.util.StringConstants;

import java.util.ArrayList;
import java.util.List;

public class UserTableActivity extends BaseActivity implements ValueEventListener {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;

    public static void start(Context context) {
        context.startActivity(new Intent(context, UserTableActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_table);
        setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progress);
        userAdapter = new UserAdapter();
        linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        getUsers();
    }

    private void getUsers() {
        FirebaseDatabase.getInstance().getReference().child(StringConstants.DB_USERS).addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        progressBar.setVisibility(View.GONE);
        if (dataSnapshot.exists()) {
            List<User> users = new ArrayList<>();
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                users.add(dataSnapshot1.getValue(User.class));
            }
            userAdapter.setUsers(users);
            userAdapter.notifyDataSetChanged();
            return;
        }
        showMessage("Could not get users", -1);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        showMessage(databaseError.getMessage(), -1);
    }


    class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        List<User> users;

        public void setUsers(List<User> users) {
            this.users = users;
            UserAdapter.this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            if (users != null && position >= 0 && position < users.size()) {
                holder.setUserData(users.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (users != null)
                return users.size();
            return 0;
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {
            TextView voterId, electorname, dob, sex, address,state;
            ImageView profilePic;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                profilePic = itemView.findViewById(R.id.profile_pic);
                voterId = itemView.findViewById(R.id.voter_id);
                electorname = itemView.findViewById(R.id.elector_name);
                dob = itemView.findViewById(R.id.dob);
                sex = itemView.findViewById(R.id.gender);
                address = itemView.findViewById(R.id.address);
                state = itemView.findViewById(R.id.state);
            }

            private void setUserData(final User user) {
                Glide.with(UserTableActivity.this).load(user.getImage()).placeholder(R.drawable.ic_person).into(profilePic);
                voterId.setText(user.getId());
                electorname.setText(user.getName());
                dob.setText(user.getDob());
                sex.setText(user.getGender().toUpperCase());
                address.setText(user.getAddress());
                state.setText(user.getState());
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        UserDialog dialog = new UserDialog();
                        dialog.setUser(user);
                        dialog.show(getSupportFragmentManager(),getClass().getSimpleName());
                        return true;
                    }
                });
            }
        }
    }

}
