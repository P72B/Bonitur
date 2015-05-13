package de.p72b.bonitur;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class ActivityMain extends ActionBarActivity implements InterfaceOnFileSelectedListener, ActionBar.TabListener{
    private String directory;
    FileHelper fileHelper = new FileHelper();

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);
        
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        setDirectory(mViewPager.getCurrentItem());
    };


    private void setDirectory(int item) {
        switch (item) {
        case 0:
            this.directory = FileHelper.FILES_DIR;
            break;
        case 1:
            this.directory = FileHelper.TEMPLATE_DIR;
            break;
        case 2:
            this.directory = FileHelper.EXPORT_DIR;
            break;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_list_activity, menu);
        return true;
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_new:
            // Open ActivityBonitur and create a new file
            Intent boniturIntent = new Intent(ActivityMain.this, ActivityBonitur.class);
            boniturIntent.putExtra("newFile", Helper.NEW_FILE_NAME);
            ActivityMain.this.startActivity(boniturIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        case R.id.action_about:
            Intent aboutIntent = new Intent(ActivityMain.this, ActivityAbout.class);
            ActivityMain.this.startActivity(aboutIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        default: 
            return super.onOptionsItemSelected(item);
        }
    };


    /**
     * Interface listener is called when file was clicked in ListViewFragment.
     */
    public void onFileSelected(File file) {
        Intent myIntent;
        switch (this.directory) {
        case FileHelper.FILES_DIR:
            myIntent = new Intent(ActivityMain.this, ActivityBonitur.class);
            myIntent.putExtra("fileName", file.getName());
            myIntent.putExtra("filePath", file.getAbsolutePath());
            ActivityMain.this.startActivity(myIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            break;
        case FileHelper.TEMPLATE_DIR:
            String newFileName = fileHelper.getUniqueFileName("new Table from " + file.getName());
            file = fileHelper.copyFile(fileHelper.getFilesDir(), file, newFileName);
            myIntent = new Intent(ActivityMain.this, ActivityBonitur.class);
            myIntent.putExtra("fileName", file.getName());
            myIntent.putExtra("filePath", file.getAbsolutePath());
            ActivityMain.this.startActivity(myIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            break;
        case FileHelper.EXPORT_DIR:
            break;
        }
    };


    @Override
    public void onTabReselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
    };


    @Override
    public void onTabSelected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
        setDirectory(arg0.getPosition());
        mViewPager.setCurrentItem(arg0.getPosition());
    };


    @Override
    public void onTabUnselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) {
    };
}