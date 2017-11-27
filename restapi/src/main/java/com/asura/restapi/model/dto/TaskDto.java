package com.asura.restapi.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lichuanshun on 2017/11/20.
 *
 * 任务
 */
@Data
@Getter
@Setter
public class TaskDto {

    /**
     * create table tb_tax_task
     (
     id int auto_increment comment 'id'
     primary key,
     task_id varchar(64) not null comment '任务ID',
     user_name varchar(64) not null comment '登录用户名',
     city_code varchar(6) not null comment '城市ID',
     id_type varchar(12) null comment '登录证件类型',
     status int default '2' not null comment '状态 0: 错误 1：正常 2：正在登录 ',
     source varchar(64) not null comment '用户所属渠道',
     add_time datetime default CURRENT_TIMESTAMP null comment '添加时间',
     update_time datetime default CURRENT_TIMESTAMP null comment '修改时间',
     constraint tb_tax_task_id_uindex
     unique (id)
     )
     comment '个税用户表'
     ;
     */

    private String id;
    private String task_id;
    private String user_name;
    private String pwd;
    private String city_code;
    private String id_type;
    private String status;
    private String source;
    private String idnum;
    private String uid;

}
