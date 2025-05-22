package com.example.mapper;

import org.apache.ibatis.annotations.Select;

import com.example.entity.TestEntity;

public interface TestMapper {
    @Select("SELECT COUNT(*) FROM testentity")
    int countEntities();
    void insertTest(TestEntity entity);
    int countAll();

}
