package com.asura.restapi.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * Created by User on 2017/9/28.
 * 基础Mapper
 */
@SuppressWarnings("unused")
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> ,InsertListMapper<T> {
}
