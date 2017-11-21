package com.asura.restapi.mapper;

import com.asura.restapi.model.dto.TaskDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by lichuanshun on 2017/11/20.
 */
@Mapper
public interface TaskMapper{

    /**
     * 创建新任务
     * @param taskDto
     * @return
     */
    int createTask(TaskDto taskDto);

    int updateTaskStatus(@Param("status") Integer status, @Param("task_id") String taskId);

    TaskDto queryTaskByTaskId(@Param("task_id") String taskId);
}
