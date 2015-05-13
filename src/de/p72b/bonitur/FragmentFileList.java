package de.p72b.bonitur;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentFileList extends Fragment {
    private ListView listView;
    private FileHelper fileHelper = new FileHelper();
    InterfaceOnFileSelectedListener mListener;
    AdvancedArrayAdapter objAdapter;
    File[] files;
    Context context;
    ImageView fileListBackground;
    private File selectedFile;
    private String directory = null;

    public FragmentFileList(String dir) {
        this.directory = dir;
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.file_list, container, false);

        // fetch all files in bonitur files dir
        this.files = fileHelper.fetchFiles(directory);
        listView = (ListView) rootView.findViewById(R.id.listViewFiles);
        
        this.context = this.getActivity();
        updateListView();

        setElementStyle(rootView);
        return rootView;
    };


    private void setElementStyle(View view) {
        fileListBackground = (ImageView) view.findViewById(R.id.imageViewFileListBackground);
        
        switch (this.directory) {
        case FileHelper.FILES_DIR:
            fileListBackground.setImageResource(R.drawable.tomato_plant);
            break;
        case FileHelper.TEMPLATE_DIR:
            fileListBackground.setImageResource(R.drawable.ic_new_black);
            break;
        case FileHelper.EXPORT_DIR:
            fileListBackground.setImageResource(R.drawable.ic_im_export);
            break;
        }
    };


    /**
     * Refresh the listView.
     */
    public void updateListView() {
        objAdapter = new AdvancedArrayAdapter(this.context, this.files, this.directory);
        listView.setAdapter(objAdapter);
        // Short (single) click on list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
              mListener.onFileSelected(files[position]);
            };
        });
        
        // Long click on list item
        registerForContextMenu(listView);
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (InterfaceOnFileSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    };


    /**
     * Is shown on file long click and has the options rename or delete.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        this.selectedFile = files[info.position];
        
        System.out.println("NAME=" + directory + files[info.position].getName());
        
        
        
        menu.setHeaderTitle(files[info.position].getName());
        //menu.setHeaderTitle(R.string.context_menu_file_list_title);

        String[] menuItems = getResources().getStringArray(R.array.context_menu_file_list);
        for (int i = 0; i < menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    };


    /**
     * Is called after selected item (rename, delete) from context menu on file long click.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      if (getUserVisibleHint()) {
          int menuItemIndex = item.getItemId();
          String[] menuItems = getResources().getStringArray(R.array.context_menu_file_list);
          String menuItemName = menuItems[menuItemIndex];
          AlertDialog alert = null;
          
          System.out.println("GET=");
          
          switch (menuItemIndex) {
          case 0:
              // Rename
              final EditText input = new EditText(context);
              input.setText(this.selectedFile.getName());
              input.setOnFocusChangeListener(new OnFocusChangeListener() {
                  @Override
                  public void onFocusChange(View v, boolean hasFocus) {
                      input.post(new Runnable() {
                          @Override
                          public void run() {
                              InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                              imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                          }
                      });
                  }
              });
              input.requestFocus();
              alert = getAlertDialog(menuItemIndex, menuItemName, getResources().getString(R.string.alertDialogInputRenameSub), input).create();
              alert.setView(input);
              alert.setIcon(R.drawable.ic_action_edit);
              break;
          case 1:
              // Delete
              alert = getAlertDialog(menuItemIndex, menuItemName, getResources().getString(R.string.alertDialogInputDeleteSub), null).create();
              alert.setIcon(R.drawable.ic_action_delete);
              break;
          };
          alert.show();

          objAdapter.notifyDataSetChanged();
          return true;
      } else {
          return false;
      }
    };


    /**
     * Creates a new AlertDialog.Builder from given input.
     * @param menuName
     * @param message
     * @param input
     * @return
     */
    private AlertDialog.Builder getAlertDialog(final int index, String menuName, String message, final EditText input) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(menuName);
        builder.setMessage(message)
                .setCancelable(true)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (index) {
                                case 0:
                                    // Rename was clicked
                                    clickRenameFile(input.getText().toString());
                                    break;
                                case 1:
                                    // Delete was clicked
                                    clickDeleteFile();
                                    break;
                                }
                                updateListView();
                                dialog.cancel();
                            }
                        });
        return builder;
    };


    /**
     * Rename the file.
     * @param userInput
     */
    private void clickRenameFile(String userInput) {
        String fileNameExtension = this.directory.equals(FileHelper.EXPORT_DIR) ? ".txt" : ".xml";
        String newName = Helper.getFileNameWithExtension(userInput, fileNameExtension);

        if (newName.length() > 4) {
            // filename has more characters than ".xml"
            fileHelper.renameFile(directory, selectedFile, newName);
            this.files = fileHelper.fetchFiles(directory);
        } else {
            Toast.makeText(context.getApplicationContext(), "Invalid file name", Toast.LENGTH_LONG).show();
        }
    };


    /**
     * Delete the file.
     */
    private void clickDeleteFile() {
        selectedFile.delete();
        this.files = fileHelper.fetchFiles(directory);
    };
};
