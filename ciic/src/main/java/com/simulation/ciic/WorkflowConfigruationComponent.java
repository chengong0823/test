package com.simulation.ciic;

import com.geekplus.optimus.common.util.string.StringUtil;
import com.simulation.ciic.biz.config.BaseContext;
import com.simulation.ciic.biz.config.WorkflowConfig;
import com.simulation.ciic.biz.config.WorkflowExecuteThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Objects;

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
                if(Objects.isNull(e.getPreCode())){
                    String operateTpye=e.getOperateType();
                    if(StringUtil.isEmpty(operateTpye)||Objects.isNull(operateTpye)){
                        operateTpye="0";
                    }else{
                        operateTpye="1";
                    }
                    log.info("workflow execute,code:{},operateType:{}",e.getCode(),operateTpye);
                    workflowExecuteThreadPool.addNewWorkflow(e.getCode(), restTemplate, workflowConfig.getRequestUrl(), Integer.valueOf(operateTpye));
                    Calendar time = Calendar.getInstance();
                    time.add(Calendar.MINUTE, e.getTime().intValue());
                    if(Objects.nonNull(e.getNextCode())&&StringUtil.isNotEmpty(e.getNextCode())){
                        BaseContext.getWorkflowMap().put(e.getNextCode(), time.getTime());
                    }
                    if(Objects.nonNull(e.getRefCode())&& StringUtil.isNotEmpty(e.getRefCode())){
                        log.info("workflow execute,refcode:{}",e.getRefCode());
                        workflowExecuteThreadPool.addNewWorkflow(e.getRefCode(), restTemplate, workflowConfig.getRequestUrl(), 1);
                    }
                }
            }catch (Exception ex){
                log.info("workflow execute error");
            }
        });
        log.info("--------初始化流程end----------");
    }


   // public






}
