package com.example.otp.dao;

import com.example.otp.model.OtpConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OtpConfigDao {
    private final JdbcTemplate jdbc;
    public OtpConfigDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public OtpConfig get() {
        return jdbc.queryForObject("SELECT code_length, ttl_seconds FROM otp_config WHERE id = 1",
                (rs, rowNum) -> new OtpConfig(rs.getInt("code_length"), rs.getInt("ttl_seconds")));
    }

    public void update(int codeLength, int ttlSeconds) {
        jdbc.update("UPDATE otp_config SET code_length = ?, ttl_seconds = ? WHERE id = 1", codeLength, ttlSeconds);
    }
}
