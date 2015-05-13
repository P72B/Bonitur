package de.p72b.bonitur;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.p72b.bonitur.DialogFragmentFileName.NameDialogListener;
import de.p72b.bonitur.DialogFragmentFileSetting.FileSettingDialogListener;
import de.p72b.bonitur.R.color;
 
public class ActivityBonitur extends ActionBarActivity implements FileSettingDialogListener, NameDialogListener{
    ActionBar actionBar;
    String filePath, fileName;
    FileHelper fileHelper = new FileHelper();
    BoniturTable table;
    Boolean isTableChanged = false;
    Boolean isRecognizing = false;
    ImageButton imBtnVoiceInput;
    Button btnNumbers, btnLetters;
    EditText speech;
    TableLayout tableLayout;
    TextView rowPosition, colPosition, tvRowColSign;
    ScrollView scViewTable;
    HorizontalScrollView hzScViewTable;
    public static final int DIALOG_NAME = 1;

    // Handels the timeout between two inputs
    Handler mHandler = new Handler();
    Pointer pointer;

    SpeechRecognizer mSpeechRecognizer = new SpeechRecognizer(ActivityBonitur.this);
    MediaPlayerHelper mediaPlayerHelper = new MediaPlayerHelper(ActivityBonitur.this);
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonitur);
 
        // Prepare the actionbar with back navigation
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        File oldFile = null;
        
        tableLayout = (TableLayout) findViewById(R.id.table);

        if (fileName != null) {
            // open file
            actionBar.setSubtitle(fileName);
            oldFile = fileHelper.getFile(intent.getStringExtra("filePath"));
        } else {
            // new bonitur table
            fileName = fileHelper.getUniqueFileName(intent.getStringExtra("newFile"));
            actionBar.setSubtitle(fileName);
            isTableChanged = true;
        }
        table = new BoniturTable(tableLayout, ActivityBonitur.this, oldFile, fileName);
        pointer = table.getPointer();

        save();
        highlight();

        initViewElements();
        table.setValueEditField(speech);
        table.setCursorPositionField(rowPosition, colPosition, tvRowColSign);
    };


    @Override
    protected void onPause() {
        save();
        super.onPause();
    }


    public void onNumbers(View v) {
        speech.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        btnLetters.setTextColor(getResources().getColor(color.ownBlack));
        btnNumbers.setTextColor(getResources().getColor(color.ownAndroidDarkBlue));
    };


    public void onLetters(View v) {
        speech.setInputType(InputType.TYPE_CLASS_TEXT);
        btnNumbers.setTextColor(getResources().getColor(color.ownBlack));
        btnLetters.setTextColor(getResources().getColor(color.ownAndroidDarkBlue));
    };


    private void initViewElements() {
        btnNumbers = (Button) findViewById(R.id.btnNumbers);
        btnLetters = (Button) findViewById(R.id.btnLetters);
        speech = (EditText) findViewById(R.id.editTextSpeechInput);
        speech.setSelection(speech.getText().length());
        speech.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                if (len > 8) {
                    if (len > 35) {
                        speech.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    } else {
                        speech.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                    }
                } else {
                    speech.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45);
                }

                int[] pos = pointer.getPosition();
                String newText = String.valueOf(speech.getText());
                table.setText(pos, newText);
                isTableChanged = true;
                if (pos[0] == 0) {
                    colPosition.setText(Helper.removeLineBreaks(newText));
                }
                if (pos[1] == 0) {
                    rowPosition.setText(Helper.removeLineBreaks(newText));
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                save();
            }
        });
        
        speech.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                  if (actionId == EditorInfo.IME_ACTION_DONE) {
                      moveCoursorForward();
                      table.highlightField(pointer.getPosition());
                      centerScreenToInput();
                  }
                return true;
            }
        });
        
        rowPosition = (TextView) findViewById(R.id.tvRowPosition);
        colPosition = (TextView) findViewById(R.id.tvColPosition);
        tvRowColSign = (TextView) findViewById(R.id.tvRowColSign);
        
        imBtnVoiceInput = (ImageButton) findViewById(R.id.imageButtonStartVoiceRecord);
        imBtnVoiceInput.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (!isRecognizing) {
                    startVoiceRecognizing();
                } else {
                    stopVoiceRecognizing();
                }
            }
        });
        
        scViewTable = (ScrollView) findViewById(R.id.scrollViewTable);
        hzScViewTable = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewTable);
    };


    private void startVoiceRecognizing() {
        if (pointer.isStartPositionSpecified() && pointer.getPosition()[0] > 0 && pointer.getPosition()[1] > 0) {
            isRecognizing = true;
            imBtnVoiceInput.setImageResource(R.drawable.ic_voice_rec);

            mHandler.removeCallbacksAndMessages(null);
            initSpeechRecognizer();
        } else {
            // gives the user a hint to specify the pointer start position
            toast(getResources().getString(R.string.toastSpecifyInputFiled));
            table.higlightStartFields(1);
        }
    };


    private void initSpeechRecognizer() {
        int[] pos = pointer.getPosition();
        String msg = "( " + table.dom.getTextContent(pos[0], 0) + " / " + table.dom.getTextContent(0, pos[1]) + " )";
        msg = Helper.removeLineBreaks(msg);
        msg = "Field:" + System.getProperty("line.separator") + msg;
        mSpeechRecognizer.startListening(msg);
    };


    private void stopVoiceRecognizing() {
        isRecognizing = false;
        imBtnVoiceInput.setImageResource(R.drawable.ic_voice_start);
        mHandler.removeCallbacksAndMessages(null);
    };


    /**
     * Moves the pointer automatically forward. The direction depends on the user specified setting.
     * @return
     */
    private boolean moveCoursorBackward() {
        boolean end = false;
        // mark the next input field
        int[] pos = pointer.getPosition();
        int[] max = pointer.getMaxRowCol();
        
        if (pos[0] > 1 || pos[1] > 1) {
            // move backward
            if (pointer.getDirection() == 0) {
                // case left to right and top to bottom
                if (pos[1] > 1) {
                    // go one to right
                    pointer.setPosition(pos[0], pos[1] - 1);
                } else {
                    // go to last entry in line before
                    pointer.setPosition(pos[0] - 1, max[1]);
                }
            } else {
                // case top to bottom and left to right
                if (pos[0] > 1) {
                    // go one row up
                    pointer.setPosition(pos[0] - 1, pos[1]);
                } else {
                    // go to the last element in the left column
                    pointer.setPosition(max[0], pos[1] - 1);
                }
            }
        } else {
            end = true;
        }
        return end;
    };


    /**
     * Moves the pointer automatically forward. The direction depends on the user specified setting.
     * @return
     */
    private boolean moveCoursorForward() {
        boolean end = false;
        // mark the next input field
        int[] pos = pointer.getPosition();
        int[] max = pointer.getMaxRowCol();
        
        if (max[0] > pos[0] || max[1] > pos[1]) {
            // move forward
            if (pointer.getDirection() == 0) {
                // case left to right and top to bottom
                if (max[1] > pos[1]) {
                    // go one to left
                    pointer.setPosition(pos[0], pos[1]  + 1);
                } else {
                    // go to first left entry of a new row
                    pointer.setPosition(pos[0] + 1, 1);
                }
            } else {
                // case top to bottom and left to right
                if (max[0] > pos[0]) {
                    // go one row down
                    pointer.setPosition(pos[0] + 1, pos[1]);
                } else {
                    // go to the first top element in new column
                    pointer.setPosition(1, pos[1] + 1);
                }
            }
        } else {
            end = true;
        }
        return end;
    };


    /**
     * Moves the courser to a new line or column.
     * @return
     */
    private boolean moveCourserToNewInput() {
        boolean end = false;
        int[] pos = pointer.getPosition();
        int[] max = pointer.getMaxRowCol();

        if (max[0] > pos[0] || max[1] > pos[1]) {
            // move forward
            if (pointer.getDirection() == 0) {
                // case left to right and top to bottom
                // go to first left entry of a new row
                pointer.setPosition(pos[0] + 1, 1);
            } else {
                // case top to bottom and left to right
                // go to the first top element in new column
                pointer.setPosition(1, pos[1] + 1);
            }
        } else {
            end = true;
        }
        return end;
    };


    /**
     * Checks if table was changed and if true the update will be safed.
     */
    private void save() {
        // ensure that table has been changed, else the table was only viewed (don't save)
        if (isTableChanged) {
            // override old file
            table.saveFile(fileHelper.getFilesDir(), fileName);
        }
    };


    /**
     * Highlight the last clicked field.
     */
    private void highlight() {
        if (pointer.isStartPositionSpecified()) {
            table.highlightField(pointer.getPosition());
        } else {
            table.removeHighlight();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bonitur_activity, menu);
        return true;
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            // Show settings dialog
            DialogFragment settingDialog = new DialogFragmentFileSetting();
            ((DialogFragmentFileSetting) settingDialog).setFileName(fileName);
            ((DialogFragmentFileSetting) settingDialog).setPointer(pointer);
            settingDialog.show(getFragmentManager(), "DialogFragmentFileSetting");
            return true;
        case R.id.action_about:
            Intent aboutIntent = new Intent(ActivityBonitur.this, ActivityAbout.class);
            ActivityBonitur.this.startActivity(aboutIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        case R.id.action_export_to_txt:
            FileExporter exporter = new FileExporter(null, table.dom, fileName);
            exporter.convert();
            Toast.makeText(getApplicationContext(), fileName + " was exported.", Toast.LENGTH_LONG).show();
            return true;
        case R.id.action_save_as_template:
            DialogFragment nameDialog = new DialogFragmentFileName();
            ((DialogFragmentFileName) nameDialog).setFileName(fileName);
            nameDialog.show(getFragmentManager(), "DialogFragmentFileName");
            return true;
        default: 
            return super.onOptionsItemSelected(item);
        }
    };


    /**
     * Is called after file setting OK button was pressed.
     */
    @Override
    public void onDialogFileSettingPositiveClick(DialogFragment dialog) {
        // Get file name
        String newName = ((DialogFragmentFileSetting) dialog).edtFileName.getText().toString();
        newName = Helper.getFileNameWithExtension(newName, ".xml");
        if (!fileName.equals(newName)) {
            // Change file name
            String dir = fileHelper.getFilesDir();
            fileHelper.renameFile(dir, fileHelper.getFile(dir + fileName), newName);
            tableLayout.removeAllViews();
            table = new BoniturTable(tableLayout, ActivityBonitur.this, fileHelper.getFile(dir + newName), null);
            pointer = table.getPointer();
            actionBar.setSubtitle(newName);
            isTableChanged = true;
            fileName = newName;
        }

        int dialogRows = ((DialogFragmentFileSetting) dialog).npRow.getValue();
        int dialogCols = ((DialogFragmentFileSetting) dialog).npCol.getValue();
        if (pointer.max[0] != dialogRows || pointer.max[1] != dialogCols) {
            // Change table size
            table.updateTable(dialogRows, dialogCols);
            isTableChanged = true;
        }

        double interval = Double.valueOf((((DialogFragmentFileSetting) dialog).edtInputInterval.getText().toString()));
        if (pointer.getInterval() != interval) {
            pointer.setInterval(interval);
        }

        int direction = ((DialogFragmentFileSetting) dialog).direction;
        if (pointer.getDirection() != direction) {
            pointer.setDirection(direction);
        }

        save();
        highlight();
    };


    /**
     * Is called after template save with new file name.
     */
    @Override
    public void onDialogFileNamePositiveClick(DialogFragment dialog) {
        String name = ((DialogFragmentFileName) dialog).edtFileName.getText().toString();
        name = Helper.getFileNameWithExtension(name, ".xml");
        table.saveFile(fileHelper.getTemplateDir(), name);
    };


    /**
     * Moves the scrollView to input textView.
     */
    private void centerScreenToInput() {
        TextView tv = table.getTextView(pointer.getPosition());
        hzScViewTable.smoothScrollTo(tv.getLeft() - 190, 0);
        scViewTable.smoothScrollTo(0, pointer.getPosition()[0] * tv.getHeight() - 80);
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Helper.REQUEST_CODE && resultCode == RESULT_OK) {
            int inputType = mSpeechRecognizer.newInput(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
            Boolean endOfInput = true;
            
            

            switch (inputType) {
                case 0:
                    // Voice cmd
                    int indexCmd = mSpeechRecognizer.voiceCmdIndex;
                    endOfInput = false;
                    if (indexCmd >= 0 && indexCmd <= 4) {
                        // "Neue Pflanze" go to next row
                        mediaPlayerHelper.play(mediaPlayerHelper.ERR);
                        endOfInput = moveCourserToNewInput();
                    } else if (indexCmd >= 5 && indexCmd <= 6) {
                        // "Zurück"
                        mediaPlayerHelper.play(mediaPlayerHelper.ERR);
                        endOfInput = moveCoursorBackward();
                    }
                    break;
                case 1:
                    // Number
                    table.setText(pointer.getPosition(), mSpeechRecognizer.result);
                    speech.setText(mSpeechRecognizer.result);
                    endOfInput = moveCoursorForward();
                    break;
                case 2:
                    // Free form input (text or unknown)
                    table.setText(pointer.getPosition(), mSpeechRecognizer.result);
                    speech.setText(mSpeechRecognizer.result);
                    endOfInput = moveCoursorForward();
                    break;
            };

            centerScreenToInput();
            // if not end of table
            if (!endOfInput) {
                // set timeout before new speech input
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        table.highlightField(pointer.getPosition());
                        initSpeechRecognizer();
                    }
                }, (long) (pointer.getInterval() * 1000));
            } else {
                // table ends
                toast(getResources().getString(R.string.toastEnd));
                stopVoiceRecognizing();
            }
            isTableChanged = true;
        } else {
            // user cancel
            stopVoiceRecognizing();
        }
        super.onActivityResult(requestCode, resultCode, data);
    };


    // TODO: only for debugging. Remove after release.
    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    };


    // TODO: only for debugging. Remove after release.
    public void print(String msg) {
        System.out.println(msg);
    };
}
