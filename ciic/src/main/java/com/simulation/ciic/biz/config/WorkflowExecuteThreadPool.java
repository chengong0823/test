package com.simulation.ciic.biz.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 流程启动缓存池
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-24 14:10
 **/
@Component
public class WorkflowExecuteThreadPool {
    /**
     * 定义缓存线程池
     */
    private static ExecutorService pool = Executors.newCachedThreadPool();

    /**
     * 线程池添加一个工作流任务线程
     */
    public void addNewWorkflow(String workflowCode, RestTemplate restTemplate, String url,Integer operationType) {
        pool.execute(new WorkflowExecuteThread(workflowCode, restTemplate, url,operationType));
    }
}
