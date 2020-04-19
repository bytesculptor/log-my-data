package com.bytesculptor.logmydata;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.bytesculptor.logmydata.database.DbHandler;
import com.bytesculptor.logmydata.dialogs.DialogLogbookDelete;
import com.bytesculptor.logmydata.dialogs.DialogLogbookRename;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DialogLogbookDelete.LogbookDeleteDialogListener, DialogLogbookRename.LogbookRenameDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView lvLogbookList;

    private DbHandler dbHandler;
    private ArrayList<String> logbookList = new ArrayList<>();
    private String szClickedLogbookContextMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fabMain);
        fab.setOnClickListener(view -> {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            onClickButtonCreate();
        });

        lvLogbookList = findViewById(R.id.lvLogbookList);
        registerForContextMenu(lvLogbookList);

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHandler = new DbHandler(this);
        logbookList = LoadBlockNameList();

        if ((logbookList != null)) {
            LogbookListAdapter logbookListAdapter = new LogbookListAdapter(this, logbookList);
            lvLogbookList.setAdapter(logbookListAdapter);

            lvLogbookList.setOnItemClickListener((parent, view, position, id) -> {
                String szActualLogbook = logbookList.get(position);
                Intent intent = new Intent(MainActivity.this, EntryContentViewActivity.class);
                intent.putExtra("logbook", szActualLogbook);
                startActivity(intent);
            });
        }
    }

    private void onClickButtonCreate() {
        Intent intent = new Intent(this, CreateLogbookActivity.class);
        startActivity(intent);
    }


    private ArrayList<String> LoadBlockNameList() {
        ArrayList<String> logbookL = new ArrayList<>();

        Cursor cursor;
        try {
            cursor = dbHandler.queryAllLogbookNameFromTable("ASC");
            if (cursor.getCount() == 0) {
                return null;
            }
        } catch (Exception e) {
            Log.d(TAG, "LoadBlockNameList: null excepetion " + e.toString());
            return null;
        }
        long bla = cursor.getLong(cursor.getColumnIndex(DbHandler._ID));
        logbookL.clear();
        while (cursor.moveToNext()) {
            logbookL.add(cursor.getString(cursor.getColumnIndex(DbHandler.LOGBOOK)));
        }
        return logbookL;
    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_main, menu);
    }


    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        szClickedLogbookContextMenu = logbookList.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_delete:
                DialogFragment dialogDelete = new DialogLogbookDelete();
                Bundle args = new Bundle();
                args.putString("logbook", logbookList.get(info.position));
                dialogDelete.setArguments(args);
                dialogDelete.show(getFragmentManager(), "gs");
                break;

            case R.id.menu_renameLogbook:
                DialogFragment dialogRename = new DialogLogbookRename();
                Bundle logbookName = new Bundle();
                logbookName.putString("oldName", szClickedLogbookContextMenu);
                dialogRename.setArguments(logbookName);
                dialogRename.show(getFragmentManager(), "re");
                break;

        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void restartApp() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onDialogDeleteLogbookPositiveClick(DialogFragment dialog) {
        dbHandler.deleteLogbookTable(szClickedLogbookContextMenu);
        restartApp();
    }

    @Override
    public void onDialogRenameLogbookPositiveClick(DialogFragment dialog, String szNewName) {
        dbHandler.renameLogbook(szClickedLogbookContextMenu, szNewName);
        restartApp();
    }
}
