package com.bytesculptor.logmydata;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;

import com.bytesculptor.logmydata.database.DbHandler;
import com.bytesculptor.logmydata.database.LogbookContentViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EntryContentViewActivity extends ListActivity {

    private static final String TAG = EntryContentViewActivity.class.getSimpleName();

    private DbHandler dbHandler;
    private LogbookContentViewAdapter listDbAdapter;
    private String szLogbookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logbook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogbookView);


        FloatingActionButton fab = findViewById(R.id.fabNewEntry);
        fab.setOnClickListener(view -> onClickButtonCreate());

        dbHandler = new DbHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            szLogbookName = extras.getString("logbook");
            toolbar.setTitle(szLogbookName);
        } else {
            //Toast.makeText(context, "bundle failed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadData();
    }


    private void loadData() {

        Cursor dbCursor = dbHandler.queryAllFromTable(szLogbookName);
/*
        if (dbCursor != null) {
            if (dbCursor.getCount() == 0) {
                Toast.makeText(this, "szNoCards", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
*/
        listDbAdapter = new LogbookContentViewAdapter(this, dbCursor);
        setListAdapter(listDbAdapter);
        listDbAdapter.notifyDataSetChanged();
    }


    private void onClickButtonCreate() {
        Intent intent = new Intent(EntryContentViewActivity.this, CreateEntryActivity.class);
        intent.putExtra("logbook", szLogbookName);
        startActivity(intent);
    }
}