package ca.chirp.messenger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Chat Fragment
                return new ListChatActivity();
            case 1:
                // User List Fragment
                return new ListUsersActivity();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}