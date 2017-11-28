package com.asura.restapi;

import base.TestSupport;
import com.alibaba.fastjson.JSONObject;
import com.asura.restapi.common.encrypt.AsuraEncrypt;
import com.asura.restapi.model.dto.TaskDto;
import com.asura.restapi.model.dto.TaxInfo;
import com.asura.restapi.service.TaskService;
import com.asura.restapi.service.TaxInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by lichuanshun on 2017/11/20.
 */
public class TaskServiceTest extends TestSupport{

    @Autowired
    TaskService taskService;
    @Autowired
    TaxInfoService taxInfoService;


    @Test
    public void queryTaxByUid(){
        String uid = "27786501-249b-4ad5-a725-0499e6e3a613";
        List<TaxInfo> list = taxInfoService.queryTaxInfoByUid(uid);
        System.out.println(JSONObject.toJSONString(list));
    }
    @Test
    public void testCreateTask(){
        TaskDto taskDto = new TaskDto();
        taskDto.setCity_code("310100");
        taskDto.setId_type("201");
        taskDto.setPwd(AsuraEncrypt.encryptStr("love1990"));
        taskDto.setUser_name(AsuraEncrypt.encryptStr("372926198903172511"));
        taskDto.setSource("test");
        int result = taskService.saveTask(taskDto);
        System.out.println("&&&&&&&&&&&result:" + result);
    }

    @Test
    public void testUpdateTask(){
        String taskId = "eb2ee05f-e3f2-4981-83e0-30c76dc50c04";
        int status = 3;
        taskService.updateTaskStatus(status, taskId,"222");

    }

    @Test
    public void testQueryTask(){
        String taskId = "eb2ee05f-e3f2-4981-83e0-30c76dc50c04";
        taskService.queryTaskByTaskId(taskId);
    }
}
