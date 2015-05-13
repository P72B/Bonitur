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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

public class DialogFragmentFileSetting extends DialogFragment {
    TextView tvFileName, seekBarDialogSettingValue;
    EditText edtFileName, edtInputInterval;
    NumberPicker npRow, npCol;
    ImageButton imBtnDirectionChange;
    String currentFileName;
    // case direction 0 lrtb and case 1 tblr
    int direction;
    Pointer pointer;

    /*
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. Each method
     * passes the DialogFragment in case the host needs to query it.
     */
    public interface FileSettingDialogListener {
        public void onDialogFileSettingPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    FileSettingDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the
    // NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (FileSettingDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.dialog_file_setting, null);

        initViewElements(v);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onDialogFileSettingPositiveClick(DialogFragmentFileSetting.this);
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
        tvFileName = (TextView) v.findViewById(R.id.tvDialogSettingFileName);
        edtFileName = (EditText) v.findViewById(R.id.edtDialogSettingfileName);
        edtFileName.setText(this.currentFileName);
        
        edtInputInterval = (EditText) v.findViewById(R.id.edtInputInterval);
        edtInputInterval.setText(String.valueOf(this.pointer.getInterval()));
        
        npRow = (NumberPicker) v.findViewById(R.id.numberPickerDialogSettingRow);
        npRow.setMinValue(Helper.MIN_ROW_COL);
        npRow.setMaxValue(Helper.MAX_ROW_COL);
        npRow.setWrapSelectorWheel(false);
        npRow.setValue(pointer.max[0]);

        npCol = (NumberPicker) v.findViewById(R.id.numberPickerDialogSettingCol);
        npCol.setMinValue(Helper.MIN_ROW_COL);
        npCol.setMaxValue(Helper.MAX_ROW_COL);
        npCol.setWrapSelectorWheel(false); 
        npCol.setValue(pointer.max[1]);
        
        this.direction = pointer.getDirection();
        imBtnDirectionChange = (ImageButton) v.findViewById(R.id.imageButtonDialogSettingDirection);
        setDirectionImageBtn(this.direction == 0 ? 1 : 0);
        imBtnDirectionChange.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                setDirectionImageBtn(direction);
            }
        });
    };


    private void setDirectionImageBtn(int val) {
        if (val == 0) {
            this.direction = 1;
            imBtnDirectionChange.setImageResource(R.drawable.ic_arrow_tblr);
        } else {
            this.direction = 0;
            imBtnDirectionChange.setImageResource(R.drawable.ic_arrow_lrtb);
        }
    };


    /**
     * Set the file for settings.
     * @param file
     */
    public void setFileName(String name) {
        currentFileName = name;
    };


    public void setPointer(Pointer p) {
        this.pointer = p;
    };
}