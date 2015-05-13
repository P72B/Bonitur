package de.p72b.bonitur;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdvancedArrayAdapter extends ArrayAdapter<File>{
    private final Context context;
    String directory;
    HashMap<File, Integer> mIdMap = new HashMap<File, Integer>();
    File[] files;
    Date date;
    ImageView fileIcon;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public AdvancedArrayAdapter(Context context, File[] file, String dir) {
      super(context, R.layout.file_view, file);
      this.context = context;
      this.directory = dir;
      mIdMap = new HashMap<File, Integer>();
      this.files = file;
      for (int i = 0; i < file.length; ++i) {
          mIdMap.put(file[i], i);
      }
    };


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View rowView = inflater.inflate(R.layout.file_view, parent, false);
      TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
      TextView secoundLine = (TextView) rowView.findViewById(R.id.secondLine);
      TextView sizeLine = (TextView) rowView.findViewById(R.id.sizeLine);
      fileIcon = (ImageView) rowView.findViewById(R.id.icon);

      // File name
      //Object[] files = mIdMap.keySet().toArray();
      File current = files[position];
      firstLine.setText(current.getName());
      // File creation date
      date = new Date(current.lastModified());
      secoundLine.setText(df2.format(date));
      // File size on disk
      sizeLine.setText(Helper.bestSize(current.length()));
      
      setElementStyle();
      return rowView;
    };


    private void setElementStyle() {
        switch (this.directory) {
        case FileHelper.FILES_DIR:
            fileIcon.setImageResource(R.drawable.ic_file_bonitur);
            break;
        case FileHelper.TEMPLATE_DIR:
            fileIcon.setImageResource(R.drawable.ic_file_xml);
            break;
        case FileHelper.EXPORT_DIR:
            fileIcon.setImageResource(R.drawable.ic_file_txt);
            break;
        }
    };


    @Override
    public long getItemId(int position) {
      File item = getItem(position);
      return mIdMap.get(item);
    }
}
