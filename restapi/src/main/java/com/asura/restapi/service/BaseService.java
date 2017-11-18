package com.asura.restapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by User on 2017/9/28.
 * 基础Service
 */
@Transactional
@SuppressWarnings("unused")
public abstract class BaseService<T> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Mapper<T> mapper;

    public List<T> queryAll() {
        return mapper.selectAll();
    }

    public T selectByPrimaryKey(T e) {
        return mapper.selectByPrimaryKey(e);
    }

    public List<T> queryListByExample(Object example) {
        return mapper.selectByExample(example);
    }

    //为null的字段将使用数据库默认值
    public int insertByEntitySelective(T e) {
        return mapper.insertSelective(e);
    }

    //为null的字段将会被设置为null
    public int insertByEntity(T e) {
        return mapper.insert(e);
    }

    //为null的字段将使用数据库默认值
    public int updateByEntitySelective(T e) {
        return mapper.updateByPrimaryKeySelective(e);
    }

    //为null的字段将会被设置为null
    public int updateByPrimaryKey(T e) {
        return mapper.updateByPrimaryKey(e);
    }

    public int deleteByPrimaryKey(T e) {
        return mapper.deleteByPrimaryKey(e);
    }

    public int selectCount(T e) {
        return mapper.selectCount(e);
    }

    public int deleteByExample(Object example) {
        return mapper.deleteByExample(example);
    }
}
