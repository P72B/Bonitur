package de.p72b.bonitur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.annotation.SuppressLint;

@SuppressLint("SdCardPath")
public class FileHelper {
    public final static String HOME_DIR = "/sdcard/Bonitur/";
    public final static String FILES_DIR = "/sdcard/Bonitur/files/";
    public final static String EXPORT_DIR = "/sdcard/Bonitur/export/";
    public final static String TEMPLATE_DIR = "/sdcard/Bonitur/template/";

    public FileHelper() {
        // Create the home directory
        File appHomeDir = new File(HOME_DIR);
        appHomeDir.mkdirs();
        // Create the file directory
        File appFileDir = new File(FILES_DIR);
        appFileDir.mkdirs();
        // Create the export directory
        File appExportDir = new File(EXPORT_DIR);
        appExportDir.mkdirs();
        // Create the export directory
        File appTemplateDir = new File(TEMPLATE_DIR);
        appTemplateDir.mkdirs();
    };

    /**
     * Writes given data to a specified directory.
     * 
     * @param path
     *            Directory to place the file.
     * @param fileName
     *            Describes the name of the file to write.
     * @param dataToWrite
     *            Data.
     */
    public void writeFile(String path, String fileName, String dataToWrite) {
        File file = new File(path, fileName);
        FileOutputStream fos;
        byte[] data = new String(dataToWrite).getBytes();
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // handle exception
        } catch (IOException e) {
            // handle exception
        }
    };

    /**
     * Reads all files in given directory and gives them back.
     * 
     * @param path
     *            Directory path to read.
     */
    public File[] fetchFiles(String path) {
        File f = new File(path);
        File file[] = f.listFiles();
        Arrays.sort(file);
        return file;
    };

    /**
     * Checks if a file exists.
     * 
     * @param absolutPath
     *            File path
     * @return
     */
    public boolean existsFile(String absolutPath) {
        File file = new File(absolutPath);
        return file.exists();
    };

    /**
     * Opens a file by given directory path and filename.
     * 
     * @param absolutePath
     * @return
     */
    public File getFile(String absolutePath) {
        return new File(absolutePath);
    };

    /**
     * Retruns the applications home directory.
     * 
     * @return
     */
    public String getHomeDir() {
        return HOME_DIR;
    }

    /**
     * Retruns the applications export directory.
     * 
     * @return
     */
    public String getExportDir() {
        return EXPORT_DIR;
    }

    /**
     * Retruns the applications file directory.
     * 
     * @return
     */
    public String getFilesDir() {
        return FILES_DIR;
    }

    public String getTemplateDir() {
        return TEMPLATE_DIR;
    }

    /**
     * Renames a file.
     * 
     * @param file
     *            File to be renamed.
     * @param name
     *            New file name.
     */
    public void renameFile(String directory, File file, String newName) {
        File newNamedFile = new File(directory, newName);
        file.renameTo(newNamedFile);
    };

    /**
     * Delete a file.
     * 
     * @param file
     *            File to be renamed.
     * @param name
     *            New file name.
     */
    public void deleteFile(File file) {
        file.delete();
    };

    /**
     * Creates a unique file name with ".xml" extension.
     * 
     * @return
     */
    public String getUniqueFileName(String name) {
        String uniqueFileName = Helper.getFileNameWithoutExtension(name);
        String ending = "";
        int i = 0;
        boolean test = true;

        do {
            String fileNameToTest = getFilesDir() + uniqueFileName + ending
                    + ".xml";
            test = existsFile(fileNameToTest);
            i++;
            ending = "_" + i;
        } while (test);

        if (i > 1) {
            uniqueFileName = uniqueFileName + "_" + (i - 1);
        }
        return Helper.getFileNameWithExtension(uniqueFileName, ".xml");
    };

    /**
     * 
     * @param fileToSave
     * @return
     */
    public File copyFile(String dir, File fileToSave, String name) {
        File newNamedFile = new File(dir, name);
        try {
            InputStream in = new FileInputStream(fileToSave);
            OutputStream out;

            out = new FileOutputStream(newNamedFile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newNamedFile;
    }
}
