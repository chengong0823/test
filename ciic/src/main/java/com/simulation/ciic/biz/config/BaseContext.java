package com.simulation.ciic.biz.config;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存功能
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-24 13:38
 **/
public class BaseContext {

    private static Map<String, Date> workflowMap = new ConcurrentHashMap<>();

    public static Map<String, Date> getWorkflowMap(){
        return workflowMap;
    }
}
