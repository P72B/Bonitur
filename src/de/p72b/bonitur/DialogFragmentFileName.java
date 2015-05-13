package de.p72b.bonitur;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DialogFragmentFileName extends DialogFragment {
    TextView tvFileName;
    EditText edtFileName;
    String currentFileName;

    /*
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. Each method
     * passes the DialogFragment in case the host needs to query it.
     */
    public interface NameDialogListener {
        public void onDialogFileNamePositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NameDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the
    // NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (NameDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.dialog_file_name, null);

        initViewElements(v);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onDialogFileNamePositiveClick(DialogFragmentFileName.this);
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
    };


    /**
     * Initialize the dialog view elements.
     * @param v View
     */
    private void initViewElements(View v) {
        tvFileName = (TextView) v.findViewById(R.id.tvDialogFileName);
        edtFileName = (EditText) v.findViewById(R.id.edtDialogFileName);
        edtFileName.setText(currentFileName);
    };


    /**
     * Set the file for settings.
     * @param file
     */
    public void setFileName(String name) {
        this.currentFileName = name;
    };
}