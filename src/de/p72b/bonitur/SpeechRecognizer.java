package de.p72b.bonitur;

import java.util.ArrayList;

import android.content.Intent;
import android.speech.RecognizerIntent;

public class SpeechRecognizer {
    private ActivityBonitur mMainActivity;
    public String result = null;
    public int voiceCmdIndex;

    public SpeechRecognizer(ActivityBonitur act) {
        mMainActivity = act;
    }

    /**
     * Start listening for voice input.
     */
    public void startListening(String msg) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, msg);

        mMainActivity.startActivityForResult(intent, Helper.REQUEST_CODE);
    }

    /**
     * Reads a new user voice input.
     */
    public int newInput(ArrayList<String> input) {
      String val = fetchVoiceInput(input);
      int type = 0;
System.out.println(val);


      int cmdIndex = Helper.isVoiceCmd(val);
      if (cmdIndex > -1) {
          this.result = Helper.VOICE_CMD[cmdIndex];
          this.voiceCmdIndex = cmdIndex;
          type = 0;
      } else {
          String num = getNumber(val);
          if (Helper.isNumber(num)) {
              this.result = num;
              type = 1;
          } else {
              this.result = val;
              type = 2;
          }
      }

      return type;
    };
    
    
    /**
     * Analyzes the speech input.
     * @param {Array.<String>} input User speech input
     * @return {String|undefined}
     */
    private String getNumber(String input) {
        input = input.replace(",", ".");
        input = input.replace("komma", ".");
        input = input.replace("komme", ".");
        input = input.replace("kommen", ".");
        input = input.replace("punkt", ".");
        input = input.replace("Komma", ".");
        input = input.replace("Punkt", ".");
        input = input.replace("plus", "+");
        input = input.replace("Plus", "+");
        // repleace all space characters with "no" space
        input = input.replace(" ", "");

        return input;
    };
    
    
    /**
     * Picks the first element in array.
     */
    private String fetchVoiceInput(ArrayList<String> input) {
      return input.get(0);
    };
}
