package com.example.otp.dao;

import com.example.otp.model.OtpCode;
import com.example.otp.model.OtpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class OtpDao {
    private final JdbcTemplate jdbc;
    public OtpDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public OtpCode create(long userId, String operationId, String code, LocalDateTime createdAt, LocalDateTime expiresAt) {
        return jdbc.queryForObject("""
                INSERT INTO otp_codes(user_id, operation_id, code, status, created_at, expires_at)
                VALUES (?, ?, ?, 'ACTIVE', ?, ?)
                RETURNING *
                """, (rs, rowNum) -> new OtpCode(rs.getLong("id"), rs.getLong("user_id"), rs.getString("operation_id"), rs.getString("code"),
                OtpStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("expires_at").toLocalDateTime()),
                userId, operationId, code, createdAt, expiresAt);
    }

    public Optional<OtpCode> findActive(long userId, String operationId, String code) {
        var list = jdbc.query("""
                SELECT * FROM otp_codes
                WHERE user_id = ? AND operation_id = ? AND code = ? AND status = 'ACTIVE'
                ORDER BY id DESC LIMIT 1
                """, (rs, rowNum) -> new OtpCode(rs.getLong("id"), rs.getLong("user_id"), rs.getString("operation_id"), rs.getString("code"),
                OtpStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("expires_at").toLocalDateTime()),
                userId, operationId, code);
        return list.stream().findFirst();
    }

    public void markUsed(long id) { jdbc.update("UPDATE otp_codes SET status = 'USED' WHERE id = ?", id); }

    public int expireOldCodes() {
        return jdbc.update("UPDATE otp_codes SET status = 'EXPIRED' WHERE status = 'ACTIVE' AND expires_at < now()");
    }
}
