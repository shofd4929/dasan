package com.example.springbatch.entity2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

// Sixth 배치 JdbcPagingItemReader에서 사용하는 매퍼
public class CustomBeforeRowMapper2 implements RowMapper<BeforeEntity2> {

    public static final String ID_COLUMN = "id";
    public static final String USERNAME_COLUMN = "username";

    @Override
    public BeforeEntity2 mapRow(ResultSet rs, int rowNum) throws SQLException {

        BeforeEntity2 beforeEntity = new BeforeEntity2();

        beforeEntity.setId(rs.getLong(ID_COLUMN));
        beforeEntity.setUsername(rs.getString(USERNAME_COLUMN));

        return beforeEntity;
    }
}
