package com.rdc.mymap.model;

import java.util.Comparator;

/**
 * Created by wsoyz on 2017/4/24.
 */

public class FriendsListSortComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        FriendsListItem a = (FriendsListItem) lhs;
        FriendsListItem b = (FriendsListItem) rhs;

        return (a.getFc().compareTo(b.getFc()));
    }
}
