package com.example.otp.dao;

import com.example.otp.model.Role;
import com.example.otp.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {
    private final JdbcTemplate jdbc;

    public UserDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public User create(String login, String passwordHash, Role role) {
        return jdbc.queryForObject(
                "INSERT INTO users(login, password_hash, role) VALUES (?, ?, ?) RETURNING id, login, password_hash, role",
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("login"), rs.getString("password_hash"), Role.valueOf(rs.getString("role"))),
                login, passwordHash, role.name());
    }

    public Optional<User> findByLogin(String login) {
        var list = jdbc.query("SELECT * FROM users WHERE login = ?",
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("login"), rs.getString("password_hash"), Role.valueOf(rs.getString("role"))), login);
        return list.stream().findFirst();
    }

    public boolean adminExists() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'", Integer.class);
        return count != null && count > 0;
    }

    public List<User> findRegularUsers() {
        return jdbc.query("SELECT * FROM users WHERE role = 'USER' ORDER BY id",
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("login"), rs.getString("password_hash"), Role.valueOf(rs.getString("role"))));
    }

    public void deleteUser(long id) {
        jdbc.update("DELETE FROM users WHERE id = ? AND role = 'USER'", id);
    }
}
