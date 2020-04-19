package com.bytesculptor.logmydata;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.bytesculptor.logmydata.database.DbHandler;

public class CreateEntryActivity extends AppCompatActivity {

    private EditText etTime, etNumber;
    private EditText etComment;
    private CheckBox cbOpt1, cbOpt2;
    private DbHandler dbHandler;
    private String szLogbookName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        dbHandler = new DbHandler(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            szLogbookName = extras.getString("logbook");
        }
        initLayout();

        Button btSave = findViewById(R.id.btSaveNewEntry);
        btSave.setOnClickListener(v -> {
            if (!etNumber.getText().toString().isEmpty()) {
                dbHandler.insertNewEntryTypeNumeric(szLogbookName, etNumber.getText().toString(), etComment.getText().toString(), cbOpt1.isChecked(), cbOpt2.isChecked());
                Toast.makeText(getBaseContext(), "Logbook entry done", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initLayout() {
        //dbHandler.queryAllFromTable("");
        etTime = findViewById(R.id.etTime);
        etNumber = findViewById(R.id.etNumber);
        etComment = findViewById(R.id.etComment);
        cbOpt1 = findViewById(R.id.cbOpt1);
        cbOpt2 = findViewById(R.id.cbOpt2);
    }
}