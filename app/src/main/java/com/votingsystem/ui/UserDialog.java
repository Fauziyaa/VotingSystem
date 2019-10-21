package com.votingsystem.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.FirebaseDatabase;
import com.votingsystem.R;
import com.votingsystem.db.model.User;
import com.votingsystem.util.StringConstants;

public class UserDialog extends DialogFragment {

    User user;

    ImageView close,save;
    EditText profilePic, name, dob, address;
    RadioButton male,female;
    TextView voterid;
    Spinner stateSpinner;
    ArrayAdapter<String> stateAdapter;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_dialog,container,false);
        voterid = view.findViewById(R.id.voter_id);
        male = view.findViewById(R.id.radio_male);
        female = view.findViewById(R.id.radio_female);
        profilePic = view.findViewById(R.id.profile_pic);
        name = view.findViewById(R.id.elector_name);
        dob = view.findViewById(R.id.dob);
        address = view.findViewById(R.id.address);
        close = view.findViewById(R.id.btn_close);
        save = view.findViewById(R.id.btn_save);
        stateSpinner = view.findViewById(R.id.state_spinner);
        stateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.voting_state));
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setName(name.getText().toString().trim());
                user.setGender(male.isChecked()?"Male":"Female");
                user.setImage(profilePic.getText().toString().trim());
                user.setDob(dob.getText().toString().trim());
                user.setAddress(address.getText().toString().trim());
                user.setState(stateAdapter.getItem(stateSpinner.getSelectedItemPosition()));
                FirebaseDatabase.getInstance().getReference().child(StringConstants.DB_USERS).child(user.getId()).setValue(user);
                Toast.makeText(getActivity(),"User updated",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        if (user!=null){
            voterid.setText(user.getId());
            if (!TextUtils.isEmpty(user.getGender())){
                if (user.getGender().toLowerCase().startsWith("m")){
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
            }
            name.setText(user.getName());
            profilePic.setText(user.getImage());
            dob.setText(user.getDob());
            address.setText(user.getAddress());
            stateSpinner.setSelection(stateAdapter.getPosition(user.getState()));
        }

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog!=null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}