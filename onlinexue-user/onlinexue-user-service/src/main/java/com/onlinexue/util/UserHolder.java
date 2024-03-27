package com.onlinexue.util;

import com.onlinexue.model.dto.UserDtO;

public class UserHolder {
    private static final ThreadLocal<UserDtO> tl = new ThreadLocal<>();

    public static void saveUser(UserDtO user) {
        tl.set(user);
    }

    public static UserDtO getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
