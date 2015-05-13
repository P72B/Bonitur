package de.p72b.bonitur;


public class Helper {
    public static final int REQUEST_CODE = 1234;
    public static final String INPUT_ERR = "invalid input";
    public static final String NEW_FILE_NAME = "New table.xml";
    public static final int MAX_ROW_COL = 1000;
    public static final int MIN_ROW_COL = 1;
    public static final String ARG_SECTION_NUMBER = "section_number";
    
    public static String VOICE_CMD[] = {"neue Pflanze", "neue pflanze", "neue pflanzen", "neue transfers", "neue Pflanzen",
        "zurück", "Zurück"};
    
    
    /**
     * Checks if a value is a valid number.
     * @param {String} n Value to test
     * @return {Boolean} true if it is a number otherwise false
     */
    public static Boolean isNumber(String n) {
        try {
            Double.parseDouble(n); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    };
    
    
    /**
     * Checks if voice input is a command.
     */
    public static int isVoiceCmd(String cmd) {
      int index = -1;
      // iterate over all known commands
      for (int i = 0; i < VOICE_CMD.length; i++) {
          // compare input with command
          if (VOICE_CMD[i].equals(cmd)) {
              index = i;
          }
      }
      return index;
    };
    
    
    /**
     * Calculates the best visual Size from given file size in byte. Example: 600Byte returns 0.6kB.
     * @param byteValue
     * @return
     */
    public static String bestSize(long byteValue) {
        String[] präfix = {"B", "kB", "MB", "GB"};
        double value = byteValue;
        int index = 0;
        for (int i = 0; value > 499 && i < präfix.length; i++) {
            value = value / 1024;
            index = i + 1;
        }
        return ((double) Math.round(value * 10) / 10) + präfix[index];
    };


    /**
     * Ensures that a filename always ends with the file name extension
     * @param name
     * @return
     */
    public static String getFileNameWithExtension(String name, String fileNameExtension) {
        int len = fileNameExtension.length();
        String substr = name.substring(Math.max(0, name.length() - len));
        if (!substr.equals(fileNameExtension)) {
            name = name + fileNameExtension;
        }
        return name;
    };


    /**
     * Ensures that a filename never ends with the file name extension ".xml"
     * @param name
     * @return
     */
    public static String getFileNameWithoutExtension(String name) {
        if (name.endsWith(".xml") || name.endsWith(".txt")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    };


    public static String removeLineBreaks(String str) {
        return str.replace(System.getProperty("line.separator") , " ");
    }
};
