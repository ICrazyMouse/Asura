package com.asura.restapi.service;

import com.asura.restapi.mapper.TaskMapper;
import com.asura.restapi.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lichuanshun on 2017/11/20.
 */
@Service
public class TaskService {

    @Autowired
    TaskMapper taskMapper;


    /**
     * 初始化任务
     * @param taskDto
     * @return
     */


    public int saveTask(TaskDto taskDto){
        return taskMapper.createTask(taskDto);
    }

    /**
     *
     * @param status 状态  0: 错误 1：正常 2：正在登录 3:登录成功，正在解析 4：解析成功
     * @param taskId 任务ID
     * @return
     */
    public int updateTaskStatus(Integer status, String taskId,String desc){

        return taskMapper.updateTaskStatus(status,taskId,desc);
    }

    /**
     *
     * @param taskId
     * @return
     */
    public TaskDto queryTaskByTaskId(String taskId){
        return taskMapper.queryTaskByTaskId(taskId);
    }

}
