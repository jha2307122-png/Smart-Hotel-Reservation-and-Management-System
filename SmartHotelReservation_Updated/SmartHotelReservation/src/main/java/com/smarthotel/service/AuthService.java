package com.smarthotel.service;

import com.smarthotel.dao.UserDao;
import com.smarthotel.model.User;
import com.smarthotel.util.PasswordUtil;

public class AuthService {
    private final UserDao dao = new UserDao();

    public User login(String username, String password) throws Exception {
        User user = dao.find(username);
        return user != null && PasswordUtil.matches(password, user.passwordHash()) ? user : null;
    }

    public User login(String username, String password, String requiredRole) throws Exception {
        User user = login(username, password);
        if (user == null) return null;
        if (requiredRole == null || requiredRole.equals(user.role())) return user;
        if ("ADMIN".equals(requiredRole) && "STAFF".equals(user.role())) return user;
        return null;
    }

    public int register(String username, String password, String role) throws Exception {
        return dao.create(username, PasswordUtil.hash(password), role);
    }
}
