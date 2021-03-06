package com.futao.springbootdemo.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;

/**
 * @author futao
 * Created on 2019-04-10.
 */
public interface TestDao {

    void incrementA(@Param("id") String id, @Param("amount") int amount);

    int incrementB(@Param("id") String id, @Param("amount") int amount);

    HashMap<String, Object> list();
}
