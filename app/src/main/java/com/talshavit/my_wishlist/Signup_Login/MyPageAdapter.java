package com.talshavit.my_wishlist.Signup_Login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.talshavit.my_wishlist.Signup_Login.LoginTabFragment;
import com.talshavit.my_wishlist.Signup_Login.SignInFragment;

public class MyPageAdapter extends FragmentPagerAdapter {

    private final int numOfTabes;

    public MyPageAdapter(@NonNull FragmentManager fm, int numOfTabes) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabes = numOfTabes;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new LoginTabFragment();
            case 1:
                return new SignInFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabes;
    }
}
