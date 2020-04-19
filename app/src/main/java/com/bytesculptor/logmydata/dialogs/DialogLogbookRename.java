package com.bytesculptor.logmydata.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bytesculptor.logmydata.R;

/**
 * Created by Ro on 23.07.2016.
 */
public class DialogLogbookRename extends DialogFragment {
    private InputMethodManager imm;

    public interface LogbookRenameDialogListener {
        void onDialogRenameLogbookPositiveClick(DialogFragment dialog, String szNewName);
    }

    LogbookRenameDialogListener mListener;

    /*
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (LogbookRenameDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must impl NoticeDialogListener interface in host activity");
        }
    }
*/
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Bundle args = getArguments();
        final String oldLogbookName = args.getString("oldName", "");

        // Inflate and set the layout for the dialog
        View content = inflater.inflate(R.layout.dialog_rename_logbook, null);
        builder.setView(content);
        EditText etNewLogbookName = content.findViewById(R.id.etNewLogbookname);
        etNewLogbookName.setText(oldLogbookName);
        etNewLogbookName.setSelection(etNewLogbookName.getText().length());
        etNewLogbookName.selectAll();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        // Add action buttons
        builder.setPositiveButton(R.string.szSave, (dialog, id) -> {
            Dialog f = (Dialog) dialog;
            EditText etNewLogbookName1 = f.findViewById(R.id.etNewLogbookname);
            mListener.onDialogRenameLogbookPositiveClick(DialogLogbookRename.this, etNewLogbookName1.getText().toString());
        });

        builder.setNegativeButton(R.string.szAbort, (dialog, id) -> {
            // InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            // imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            DialogLogbookRename.this.getDialog().cancel();
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        });

        return builder.create();
    }
}