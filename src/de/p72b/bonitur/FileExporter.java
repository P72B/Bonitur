package de.p72b.bonitur;

import java.io.File;

public class FileExporter {
    DOM dom = new DOM();
    FileHelper fileHelper = new FileHelper();
    String fileName = null;

    public FileExporter(File file, DOM paramDom, String name) {
        if (file != null) {
            // Create DOM from file
            this.dom.readFile(file);
            this.fileName = Helper.getFileNameWithoutExtension(file.getName()) + ".txt";
        }
        if (dom != null) {
            this.dom = paramDom;
            this.fileName = Helper.getFileNameWithoutExtension(name) + ".txt";
        }
    };


    /**
     * Converts the DOM data to string with ";" separator.
     */
    public void convert() {
        int lenRows = this.dom.getRowsLength();
        int lenCols = this.dom.getColsLength();
        String txt = "";

        for (int i = 0; i < lenRows; i++) {
            for (int ii = 0; ii < lenCols; ii++) {
                // Ensure that no line breaks will be exported
                String content = Helper.removeLineBreaks(this.dom.getTextContent(i, ii));
                txt = txt + content + ";";
            }
            txt = txt + System.getProperty("line.separator");
        }
        saveAsTextFile(txt);
    };


    /**
     * Saves the given data in exports directory.
     * @param data Content to write
     */
    private void saveAsTextFile(String data) {
        fileHelper.writeFile(fileHelper.getExportDir(), this.fileName, data);
    };
};
