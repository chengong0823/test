package com.simulation.ciic;

import com.simulation.ciic.biz.config.BaseContext;
import com.simulation.ciic.biz.config.WorkflowConfig;
import com.simulation.ciic.biz.config.WorkflowExecuteThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

/**
 * 启动时调用流程
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-24 13:51
 **/
@Component
@Slf4j
public class WorkflowConfigruationComponent implements CommandLineRunner {

    @Autowired
    private WorkflowConfig workflowConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WorkflowExecuteThreadPool workflowExecuteThreadPool;

    @Override
    public void run(String... args) throws Exception {
        log.info("--------初始化流程begin----------");
        workflowConfig.getWorkflowList().forEach(e->{
            try {
                log.info("workflow execute,code:{}",e.getCode());
                workflowExecuteThreadPool.addNewWorkflow(e.getCode(), restTemplate, workflowConfig.getRequestUrl(), 0);
                Calendar time = Calendar.getInstance();
                time.add(Calendar.MINUTE, e.getTime().intValue());
                BaseContext.getWorkflowMap().put(e.getCode(), time.getTime());
                workflowExecuteThreadPool.addNewWorkflow(e.getRefCode(), restTemplate, workflowConfig.getRequestUrl(), 1);
            }catch (Exception ex){
                log.info("workflow execute error");
            }
        });
        log.info("--------初始化流程end----------");
    }


   // public






}
