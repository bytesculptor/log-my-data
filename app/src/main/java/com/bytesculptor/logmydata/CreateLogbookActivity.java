package com.bytesculptor.logmydata;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bytesculptor.logmydata.database.DbHandler;
import com.google.android.material.snackbar.Snackbar;


public class CreateLogbookActivity extends AppCompatActivity {

    private static final String TAG = CreateLogbookActivity.class.getSimpleName();
    private DbHandler dbHandler;
    private RadioButton rbNumerical, rbTime, rbText;
    private EditText etLogbookname, etOptionCb1, etOptionCb2;
    private CheckBox cbOption1, cbOption2;
    private TextView tvOpt1Cnt20, tvOpt2Cnt20, tvLogbooknameCnt20;
    private CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_logbook);
        Toolbar toolbar = findViewById(R.id.toolbarCreateLogbook);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout = findViewById(R.id.layout_coordinator_create_logbook);

        initTextViews();
        initRadioButtons();
        initCheckboxes();
        initEditTexts();

        dbHandler = new DbHandler(this);

        Button btSave = findViewById(R.id.btCreateLogbook);
        btSave.setOnClickListener(v -> onSaveButtonPressed());
    }


    /**
     *
     */
    private void initTextViews() {
        tvOpt1Cnt20 = findViewById(R.id.tvOpt1Cnt20);
        tvOpt1Cnt20.setText(Constants.STRING_LENGTH_SHORT + "/" + Constants.STRING_LENGTH_SHORT);

        tvOpt2Cnt20 = findViewById(R.id.tvOpt2Cnt20);
        tvOpt2Cnt20.setText(Constants.STRING_LENGTH_SHORT + "/" + Constants.STRING_LENGTH_SHORT);

        tvLogbooknameCnt20 = findViewById(R.id.tvLogbooknameCnt20);
        tvLogbooknameCnt20.setText(Constants.STRING_LENGTH_SHORT + "/" + Constants.STRING_LENGTH_SHORT);
    }


    /**
     *
     */
    private void initEditTexts() {
        etOptionCb1 = findViewById(R.id.etOptionCb1);
        etOptionCb2 = findViewById(R.id.etOptionCb2);
        etLogbookname = findViewById(R.id.etLogbookname);
        setOptionsCheckboxes();

        etOptionCb1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvOpt1Cnt20.setText(Constants.STRING_LENGTH_SHORT - s.toString().length() + "/" + Constants.STRING_LENGTH_SHORT);

                if (!cbOption1.isChecked() && (s.toString().length() > 0)) {
                    cbOption1.setChecked(true);
                } else if (s.toString().length() < 1) {
                    cbOption1.setChecked(false);
                }
                setOptionsCheckboxes();
            }
        });

        etOptionCb2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvOpt2Cnt20.setText(Constants.STRING_LENGTH_SHORT - s.toString().length() + "/" + Constants.STRING_LENGTH_SHORT);

                if (!cbOption2.isChecked() && (s.toString().length() > 0)) {
                    cbOption2.setChecked(true);
                }
                if (s.toString().length() < 1) {
                    cbOption2.setChecked(false);
                }
            }
        });

        etLogbookname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvLogbooknameCnt20.setText(Constants.STRING_LENGTH_SHORT - s.toString().length() + "/" + Constants.STRING_LENGTH_SHORT);
            }
        });


        cbOption1.setOnClickListener(v -> {
            if (cbOption1.isChecked() == false) {
                cbOption2.setChecked(false);
            }
        });

    }


    /**
     *
     */
    private void initCheckboxes() {
        cbOption1 = findViewById(R.id.cbOption1);
        cbOption2 = findViewById(R.id.cbOption2);
        cbOption2.setEnabled(false);
    }


    /**
     *
     */
    private void setOptionsCheckboxes() {
        if (cbOption1.isChecked()) {
            cbOption2.setEnabled(true);
            etOptionCb2.setEnabled(true);
        } else {
            cbOption2.setChecked(false);
            cbOption2.setEnabled(false);
            etOptionCb2.setEnabled(false);
        }
    }


    /**
     *
     */
    private void onSaveButtonPressed() {
        if (checkForConsistency()) {
            createNewLogbookInDb();
        }
    }


    private boolean checkForConsistency() {
        if (etLogbookname.getText().toString().isEmpty()) {
            Snackbar.make(layout, R.string.szEnterLogbookName, Snackbar.LENGTH_LONG).show();
            return false;
        } else if ((cbOption1.isChecked()) && (etOptionCb1.getText().toString().isEmpty())) {
            Snackbar.make(layout, R.string.szOpt1NoText, Snackbar.LENGTH_LONG).show();
            return false;
        } else if ((cbOption2.isChecked()) && (etOptionCb2.getText().toString().isEmpty())) {
            Snackbar.make(layout, R.string.szOpt2NoText, Snackbar.LENGTH_LONG).show();
            return false;
            //} else if (isLogbookNameInvalid(etLogbookname.getText().toString())) {
            //    Toast.makeText(this, "invalid name: no special characters or spaces allowed. Use underscore or camel case", Toast.LENGTH_LONG).show();
            //    return false;
        }
        return true;
    }


    private void createNewLogbookInDb() {
        String bookName = etLogbookname.getText().toString();
        Log.d(TAG, "createNewLogbookInDb: bookName " + bookName);
        int hashCode = Math.abs(bookName.trim().hashCode());
        Log.d(TAG, "createNewLogbookInDb: hashCode " + hashCode + ", trim: " + bookName.trim());

        boolean bRes = dbHandler.createNewLogbookTable(bookName, "T" + hashCode, getCheckedRadioButton(),
                cbOption1.isChecked(), etOptionCb1.getText().toString(), cbOption2.isChecked(),
                etOptionCb2.getText().toString());

        if (bRes) {
            Toast.makeText(this, R.string.szMadeNewLogbook, Toast.LENGTH_SHORT).show();
            dbHandler.closeDb();
            restartApp();
        } else {
            Toast.makeText(this, "error to make a new logbook", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param str
     * @return
     */
    private boolean isLogbookNameInvalid(String str) {
       /* if (Character.isDigit(str.charAt(0))) {
            Toast.makeText(this, "no number for leading character, start with a letter", Toast.LENGTH_SHORT).show();
            return true;
        }
       */
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) || (str.charAt(i) == '_') || (str.charAt(i) == ' ')) {
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private void initRadioButtons() {
        rbNumerical = findViewById(R.id.rbNumericalVal);
        rbTime = findViewById(R.id.rbTime);
        rbText = findViewById(R.id.rbText);

        rbNumerical.setChecked(true);
    }


    private Constants.logType getCheckedRadioButton() {
        if (rbNumerical.isChecked())
            return Constants.logType.NUMBER;
        else if (rbTime.isChecked())
            return Constants.logType.TIME;
        else
            return Constants.logType.TEXT;
    }

    /**
     *
     */
    private void restartApp() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}