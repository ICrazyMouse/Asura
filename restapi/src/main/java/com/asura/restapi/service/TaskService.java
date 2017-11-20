package com.asura.restapi.service;

import com.asura.restapi.mapper.TaskMapper;
import com.asura.restapi.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by lichuanshun on 2017/11/20.
 */
@Service
public class TaskService {

    @Autowired
    TaskMapper taskMapper;


    public int createTask(TaskDto taskDto){
        String taskId = UUID.randomUUID().toString();
        taskDto.setTask_id(taskId);
        return taskMapper.createTask(taskDto);

    }
}
