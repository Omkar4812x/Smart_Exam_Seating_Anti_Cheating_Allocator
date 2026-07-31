package com.examseating.dao;

import com.examseating.model.AdminUser;

public interface AdminDAO {
    AdminUser authenticate(String username, String hashedPassword);
}
