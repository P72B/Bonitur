package de.p72b.bonitur;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    Fragment fragment;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            fragment = new FragmentFileList(FileHelper.FILES_DIR);
            Bundle args = new Bundle();
            args.putInt(Helper.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        case 1:
            fragment = new FragmentFileList(FileHelper.TEMPLATE_DIR);
            Bundle argsNewStop = new Bundle();
            argsNewStop.putInt(Helper.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(argsNewStop);
            return fragment;
        case 2:
            fragment = new FragmentFileList(FileHelper.EXPORT_DIR);
            Bundle argsExport = new Bundle();
            argsExport.putInt(Helper.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(argsExport);
            return fragment;
        }
        return null;
    };

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    };

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case 0:
            return "File";
        case 1:
            return "Template";
        case 2:
            return "Export";
        }
        return null;
    };
};
