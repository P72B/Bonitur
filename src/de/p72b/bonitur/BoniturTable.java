package de.p72b.bonitur;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Node;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class BoniturTable {
    TableLayout tableLayout;
    TextView lastEntry, lastRowHead, lastColHead, tvRow, tvCol, tvRowColSign;
    EditText speech;
    ActivityBonitur ctx;
    Pointer pointer;
    // Holds the table content in DOM format
    DOM dom = new DOM();
    FileHelper fileHelper = new FileHelper();
    Boolean isStartFieldHiglighted = false;
    
    // tableData.get(row).getChildAt(col);
    ArrayList<TableRow> tableData = new ArrayList<TableRow>();


    /**
     * Creates a new table.
     * @param tblL TableLayout must given
     * @param contex Activity context
     * @param file Can be null or a table file
     */
    public BoniturTable(TableLayout tblL, ActivityBonitur contex, File file, String fileName) {
        tableLayout = tblL;
        ctx = contex;
        
        this.setFile(file, fileName);
    };


    public void setFile(File file, String name) {
        if (file != null) {
            // load table from file
            dom.readFile(file);
        } else {
            // new table
            dom.newDOM(name);
        }
        pointer = new Pointer(dom);
        createTableFromDOM();
    }


    /**
     * Create the TableLayout table content from the DOM.
     */
    private void createTableFromDOM() {
        int lenDOMRows = dom.getRowsLength();
        int lenDOMCols = dom.getColsLength();
        TableRow currentRow;
        TextView currentField;

        for (int i = 0; i < lenDOMRows; i++) {
            currentRow = newTableRow();
            tableData.add(currentRow);
            for (int ii = 0; ii < lenDOMCols; ii++) {
                currentField = newTextView(dom.getTextContent(i, ii), i, ii);
                tableData.get(i).addView(currentField);
            }
            tableLayout.addView(currentRow);
        }
    };


    /**
     * Updates the entire table when the amount of rows or columns or both was changed. All text input information
     * from the TableView content where used to build the DOM.
     * @param newRow Amount of rows
     * @param newCol Amount of columns
     */
    public void updateTable(int newRow, int newCol) {
        // First row and first column where not count!
        pointer.setMaxRowCol(newRow, newCol);
        // ensures that pointer is on valid table field
        pointer.setPosition(0, 0);
        pointer.setStartPosition(false);
        
        newRow = newRow + 1;
        newCol = newCol + 1;
        int totalRows = tableData.size();
        int totalCols = tableData.get(0).getChildCount();
        TableRow currentRow;
        TextView currentField;
        int indexLastRow, indexLastCol;

        // in case NumberPicker Rows was changed
        if (newRow != totalRows) {
            if (newRow > totalRows) {
                // increase table rows
                int deltaRows = newRow - totalRows; // 1
                for (int i = 0; i < deltaRows; i++) {
                    currentRow = newTableRow();
                    tableData.add(currentRow);
                    indexLastRow = totalRows + i;
                    for (int ii = 0; ii < totalCols; ii++) {
                        if (ii == 0) {
                            currentField = newTextView(String.valueOf(indexLastRow), indexLastRow, ii);
                        } else {
                            currentField = newTextView("", indexLastRow, ii);
                        }
                        currentRow.addView(currentField);
                    }
                    tableLayout.addView(currentRow);
                }
            } else {
                // reduce table rows
                int deltaRowsCut = totalRows - newRow;
                for (int i = 0;i < deltaRowsCut; i++) {
                    indexLastRow = totalRows - i - 1;
                    tableData.remove(indexLastRow);
                    tableLayout.removeViewAt(indexLastRow);
                }
            }
        }
        totalRows = tableData.size();
        totalCols = tableData.get(0).getChildCount();
        // in case NumberPicker Cols was changed
        if (newCol != totalCols) {
            if (newCol > totalCols) {
                // increase table cols
                int deltaCols = newCol - totalCols;
                for (int i = 0;i < totalRows; i++) {
                    currentRow = tableData.get(i);
                    for (int ii = 0;ii < deltaCols; ii++) {
                        indexLastCol = totalCols + ii;
                        if (i == 0) {
                            currentRow.addView(newTextView(String.valueOf(indexLastCol), i, indexLastCol));
                        } else {
                            currentRow.addView(newTextView("", i, indexLastCol));
                        }
                    }
                }
            } else {
                // reduce table cols
                int deltaColsCut = totalCols - newCol;
                for (int i = 0;i < totalRows; i++) {
                    currentRow = tableData.get(i);
                    for (int ii = 0;ii < deltaColsCut; ii++) {
                        indexLastCol = totalCols - ii - 1;
                        currentRow.removeViewAt(indexLastCol);
                    }
                }
            }
        }
        updateDOM();
    };
    
    
    /**
     * Updates the DOM. It rewrites the entire DOM.
     */
    private void updateDOM() {
        dom.clearRoot();
        int totalRows = tableData.size();
        int totalCols = tableData.get(0).getChildCount();
        for (int i = 0; i < totalRows; i++) {
            Node row = dom.newRow(i);
            for (int ii = 0; ii < totalCols; ii++) {
                TextView tvField = (TextView) tableData.get(i).getChildAt(ii);
                dom.newCol(row, ii, String.valueOf(tvField.getText()));
            }
        }
    };


    /**
     * Creates a new TextView.
     * @param text Lable text
     * @param color Text color
     * @param backgroundColor background color
     * @return
     */
    @SuppressLint("NewApi")
    private TextView newTextView(String text, int row, int col) {
        final TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(ctx.getResources().getColor(R.color.ownDarkGrey));

        tv.setPadding(5, 5, 5, 5);
        tv.setMinWidth(100);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTag(row + "_" + col);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimension(R.dimen.textsize));

        if (row == 0 || col == 0) {
            tv.setBackground(ctx.getResources().getDrawable(R.drawable.borderhead));
        } else {
            tv.setBackground(ctx.getResources().getDrawable(R.drawable.border));
        }
        
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] rowCol = String.valueOf(tv.getTag()).split("_");
                pointer.setPosition(Integer.parseInt(rowCol[0]), Integer.parseInt(rowCol[1]));
                highlightField(pointer.getPosition());
            }
        });
        return tv;
    };


    public void setText(int[] pos, String txt) {
        TextView input = this.getTextView(pos);
        input.setText(txt);
        dom.setNodeText(pos, txt);
    };


    public TextView getTextView(int[] pos) {
        return (TextView) tableData.get(pos[0]).getChildAt(pos[1]);
    };

    /**
     * Highlights a textView field on a given position.
     * @param pos
     */
    @SuppressLint("NewApi")
    public void highlightField(int[] pos) {
        if (isStartFieldHiglighted) {
            higlightStartFields(0);
        }
        // fetch TextView
        TextView tv = (TextView) tableData.get(pos[0]).getChildAt(pos[1]);
        // Reset the before highlighted field
        if (lastEntry != null) {
            lastEntry.setBackground(ctx.getResources().getDrawable(R.drawable.border));
            lastEntry.setTextColor(ctx.getResources().getColor(R.color.ownDarkGrey));
            lastEntry.setTypeface(null, Typeface.NORMAL);
            lastRowHead.setBackground(ctx.getResources().getDrawable(R.drawable.borderhead));
            lastColHead.setBackground(ctx.getResources().getDrawable(R.drawable.borderhead));
        }
        lastEntry = tv;
        lastEntry.setBackgroundColor(ctx.getResources().getColor(R.color.ownAndroidDarkBlue));
        lastEntry.setTextColor(ctx.getResources().getColor(R.color.ownWhite_100));
        lastEntry.setTypeface(null, Typeface.BOLD);
        // Highlight headings
        lastRowHead = (TextView) tableData.get(0).getChildAt(pos[1]);
        lastRowHead.setBackgroundColor(ctx.getResources().getColor(R.color.ownAndroidBlue));
        lastColHead = (TextView) tableData.get(pos[0]).getChildAt(0);
        lastColHead.setBackgroundColor(ctx.getResources().getColor(R.color.ownAndroidBlue));

        if (speech != null) {
            speech.setText(tv.getText());
            speech.setSelection(speech.getText().length());
        }
        if (tvRow != null && tvCol != null) {
            tvCol.setText(Helper.removeLineBreaks(lastRowHead.getText().toString()));
            tvRow.setText(Helper.removeLineBreaks(lastColHead.getText().toString()));
            tvRowColSign.setText("/");
        }
    };


    /**
     * Removes the highlight on any field in table.
     */
    public void removeHighlight() {
        if (isStartFieldHiglighted) {
            higlightStartFields(0);
        }
        // Reset the before highlighted field
        if (lastEntry != null) {
            lastEntry.setBackground(ctx.getResources().getDrawable(R.drawable.border));
            lastRowHead.setBackground(ctx.getResources().getDrawable(R.drawable.borderhead));
            lastColHead.setBackground(ctx.getResources().getDrawable(R.drawable.borderhead));
        }
        lastEntry = null;
        if (speech != null) {
            speech.setText("");
        }
        if (tvRow != null && tvCol != null) {
            tvCol.setText("");
            tvRow.setText("");
            tvRowColSign.setText("/");
        }
    };


    /**
     * Highlights all possible voice input start fields.
     */
    public void higlightStartFields(int mode) {
        Drawable background;
        if (mode == 1) {
            background = ctx.getResources().getDrawable(R.drawable.higlightfield);
            isStartFieldHiglighted = true;
        } else {
            background = ctx.getResources().getDrawable(R.drawable.border);
            isStartFieldHiglighted = false;
        }
        int rows = tableData.size();
        int cols = tableData.get(0).getChildCount();
        TextView field;
        for (int i = 1; i < rows; i++) {
            for (int ii = 1; ii < cols; ii++) {
                field = (TextView) tableData.get(i).getChildAt(ii);
                field.setBackground(background);
            }
        }
    };


    /**
     * Creates a new TableRow.
     * @param backgroundColor
     * @return
     */
    private TableRow newTableRow() {
        TableRow tr = new TableRow(ctx);
        tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return tr;
    };


    /**
     * Returns the pointer
     * @return
     */
    public Pointer getPointer() {
        return pointer;
    };


    /**
     * Saves the table as file.
     */
    public void saveFile(String directory, String name) {
        fileHelper.writeFile(directory, Helper.getFileNameWithExtension(name, ".xml"), dom.toString());
    };


    /**
     * Set the EditText field to print the selected table field.
     * @param edtSpeech Output field.
     */
    public void setValueEditField(EditText edtSpeech) {
        speech = edtSpeech;
    }


    public void setCursorPositionField(TextView rowPosition, TextView colPosition, TextView rowColSign) {
        tvRow = rowPosition;
        tvCol = colPosition;
        tvRowColSign = rowColSign;
    };
};
