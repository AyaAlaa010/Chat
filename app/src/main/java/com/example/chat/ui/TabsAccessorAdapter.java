package com.example.chat.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chat.tabpager.chats.ChatsUsersFragment;
import com.example.chat.tabpager.contacts.ContactsFragment;
import com.example.chat.tabpager.groups.GroupsFragment;
import com.example.chat.tabpager.requests.RequestsFragment;

public class TabsAccessorAdapter extends FragmentStateAdapter {
    public TabsAccessorAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ChatsUsersFragment();
            case 1:
                return  new GroupsFragment();
            case 2:
                return new ContactsFragment();
            case 3:
                return new RequestsFragment();
            default:
                return null;


        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
