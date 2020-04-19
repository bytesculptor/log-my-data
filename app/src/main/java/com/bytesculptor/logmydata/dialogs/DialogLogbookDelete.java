package com.bytesculptor.logmydata.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bytesculptor.logmydata.R;

/**
 * Created by Ro on 02.01.2016.
 */

public class DialogLogbookDelete extends DialogFragment {

    public DialogLogbookDelete() {
    }

    public interface LogbookDeleteDialogListener {
        void onDialogDeleteLogbookPositiveClick(DialogFragment dialog);
    }

    private LogbookDeleteDialogListener mListener;

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (LogbookDeleteDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must impl NoticeDialogListener interface in host activity");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String logbookName = args.getString("logbook", "");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.szDeleteLogbookPart1) + " '" + logbookName + "' " + getString(R.string.szDeleteLogbookPart2)).setTitle(getString(R.string.szDeleteLogbook));

        builder.setPositiveButton(R.string.szYes, (dialog, which) -> mListener.onDialogDeleteLogbookPositiveClick(DialogLogbookDelete.this));

        builder.setNegativeButton(R.string.szNo, (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }
}