package com.votingsystem.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.votingsystem.R;
import com.votingsystem.db.model.VoteSettings;
import com.votingsystem.util.StringConstants;

import java.util.Calendar;
import java.util.Date;

public class VoteSettingsActivity extends BaseActivity implements ValueEventListener {

    private VoteSettings voteSettings;
    private boolean initSpinnerSelected;

    public static void start(Context context) {
        context.startActivity(new Intent(context, VoteSettingsActivity.class));
    }

    View progress;
    Spinner spinner;
    ArrayAdapter<String> stateAdapter;
    EditText startDate, endDate;
    RecyclerView candidateRecyclerView;
    CandidateAdapter candidateAdapter;
    Button update, clear;
    Calendar dateStart = Calendar.getInstance(), dateEnd = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            Date date = calendar.getTime();
            if (date.after(dateEnd.getTime())) {
                showMessage("Start date cannot be after end date", -1);
            } else {
                showUpdate();
                dateStart.setTime(date);
                startDate.setText(String.format("%d/%d/%d", day, month + 1, year));
            }
        }
    };

    DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            Date date = calendar.getTime();
            if (date.before(dateStart.getTime())) {
                showMessage("End date cannot be before start date", -1);
            } else {
                showUpdate();
                dateEnd.setTime(date);
                endDate.setText(String.format("%d/%d/%d", day, month + 1, year));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Vote Settings");
        spinner = findViewById(R.id.spinner);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        progress = findViewById(R.id.progress);
        update = findViewById(R.id.btn_update);
        clear = findViewById(R.id.btn_clear);
        candidateRecyclerView = findViewById(R.id.recyclerview);
        candidateAdapter = new CandidateAdapter(false);
        candidateRecyclerView.setAdapter(candidateAdapter);
        candidateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dateStart = Calendar.getInstance();
        dateEnd = Calendar.getInstance();
        dateEnd.add(Calendar.DATE, 1);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog pickerDialog = new DatePickerDialog(VoteSettingsActivity.this,
                        startDateListener,
                        dateStart.get(Calendar.YEAR),
                        dateStart.get(Calendar.MONTH),
                        dateStart.get(Calendar.DAY_OF_MONTH));
                if (dateEnd != null) {
                    pickerDialog.getDatePicker().setMaxDate(dateEnd.getTimeInMillis());
                }
                pickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog pickerDialog = new DatePickerDialog(VoteSettingsActivity.this,
                        endDateListener,
                        dateEnd.get(Calendar.YEAR),
                        dateEnd.get(Calendar.MONTH),
                        dateEnd.get(Calendar.DAY_OF_MONTH));
                if (dateStart != null) {
                    pickerDialog.getDatePicker().setMinDate(dateStart.getTimeInMillis());
                }
                pickerDialog.show();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (initSpinnerSelected)
                    showUpdate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteSettings.setStartDate(startDate.getText().toString().trim());
                voteSettings.setEndDate(endDate.getText().toString().trim());
                voteSettings.setVotingState(stateAdapter.getItem(spinner.getSelectedItemPosition()));
                FirebaseDatabase.getInstance().getReference()
                        .child(StringConstants.DB_VOTE_SETTINGS)
                        .setValue(voteSettings)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showMessage("Vote Settings Saved", 1);
                                update.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage(e.getMessage(), -1);
                            }
                        });
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voteSettings != null) {
                    if (voteSettings.getCandidates() != null) {
                        for (int i = 0; i < voteSettings.getCandidates().size(); i++) {
                            voteSettings.getCandidates().get(i).setVoteCount(0);
                        }
                    }
                }
                candidateAdapter.notifyDataSetChanged();
                FirebaseDatabase.getInstance().getReference()
                        .child(StringConstants.DB_VOTE_SETTINGS)
                        .setValue(voteSettings);
                FirebaseDatabase.getInstance().getReference()
                        .child(StringConstants.DB_USERS)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(StringConstants.DB_USERS)
                                            .child(userDataSnapshot.getKey())
                                            .child(StringConstants.DB_IS_VOTED)
                                            .setValue(false);
                                }
                                showMessage("Previous Records Cleared", 1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                showMessage(databaseError.getMessage(), -1);
                            }
                        });
            }
        });

        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.voting_state));
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stateAdapter);

        getVoteSettings();
    }

    private void getVoteSettings() {
        FirebaseDatabase.getInstance().getReference().child(StringConstants.DB_VOTE_SETTINGS).addListenerForSingleValueEvent(this);

    }

    private void setVoteSettings(VoteSettings voteSettings) {
        this.voteSettings = voteSettings;
        candidateAdapter.setCandidates(voteSettings.getCandidates());
        startDate.setText(voteSettings.getStartDate());
        endDate.setText(voteSettings.getEndDate());
        if (!TextUtils.isEmpty(voteSettings.getStartDate()) && voteSettings.getStartDate().contains("/")) {
            dateStart = Calendar.getInstance();
            String[] startingDateArray = voteSettings.getStartDate().split("/");
            dateStart.set(Calendar.YEAR, Integer.parseInt(startingDateArray[2]));
            dateStart.set(Calendar.MONTH, Integer.parseInt(startingDateArray[1]) - 1);
            dateStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startingDateArray[0]));
        }

        if (!TextUtils.isEmpty(voteSettings.getEndDate()) && voteSettings.getEndDate().contains("/")) {
            dateEnd = Calendar.getInstance();
            String[] endingDateArray = voteSettings.getEndDate().split("/");
            dateEnd.set(Calendar.YEAR, Integer.parseInt(endingDateArray[2]));
            dateEnd.set(Calendar.MONTH, Integer.parseInt(endingDateArray[1]) - 1);
            dateEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endingDateArray[0]));
        }

        spinner.setSelection(stateAdapter.getPosition(voteSettings.getVotingState()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initSpinnerSelected = true;
            }
        }, 200);
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        progress.setVisibility(View.GONE);
        if (dataSnapshot.exists()) {
            VoteSettings voteSettings = dataSnapshot.getValue(VoteSettings.class);
            if (voteSettings != null) {
                setVoteSettings(voteSettings);
                return;
            }
        }
        showMessage("Settings not found", -1);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        progress.setVisibility(View.GONE);
        showMessage(databaseError.getMessage(), -1);
    }

    private void showUpdate() {
        update.setVisibility(View.VISIBLE);
    }

}
