package com.simulation.ciic;

import com.geekplus.optimus.common.util.ExceptionUtil;
import com.geekplus.optimus.common.util.collection.CollectionUtil;
import com.geekplus.optimus.common.util.string.StringUtil;
import com.simulation.ciic.biz.config.BaseContext;
import com.simulation.ciic.biz.config.WorkflowConfig;
import com.simulation.ciic.biz.config.WorkflowEntity;
import com.simulation.ciic.biz.config.WorkflowExecuteThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 流程判断
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-24 15:43
 **/
@Component
@Slf4j
public class WorkflowSchedule implements CommandLineRunner {

    @Autowired
    private WorkflowConfig workflowConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WorkflowExecuteThreadPool workflowExecuteThreadPool;

    private ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(1);

    @Override
    public void run(String... args) throws Exception {
        // 启动执行器，系统启动后10秒启动，每0.5秒执行一次
        taskExecutor.scheduleWithFixedDelay(circleTask, 10000, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * 轮询器任务，处理等待状态的任务
     */
    private Runnable circleTask = () -> {
        try {
            Set<String> workflowKeys = BaseContext.getWorkflowMap().keySet();
            if(CollectionUtil.isNotEmpty(workflowKeys)){
                workflowKeys.forEach(code->{
                    Date time = BaseContext.getWorkflowMap().get(code);
                    Calendar currentTime = Calendar.getInstance();
                    Calendar lastTime = Calendar.getInstance();
                    lastTime.setTime(time);
                    int i = currentTime.compareTo(lastTime);
                    //1，0表示当前时间大于或等于可以执行时间，触发流程
                    if(i==1||i==0){
                        List<WorkflowEntity> workflowList = workflowConfig.getWorkflowList();
                        List<WorkflowEntity> collect = workflowList.stream().filter(e -> e.getCode().equals(code)).collect(Collectors.toList());
                        if(CollectionUtil.isNotEmpty(collect)){

                            WorkflowEntity workflowEntity=collect.get(0);
                            try {
                                String operateTpye=workflowEntity.getOperateType();
                                if(StringUtil.isEmpty(operateTpye)||Objects.isNull(operateTpye)){
                                    operateTpye="0";
                                }else{
                                    operateTpye="1";
                                }
                                log.info("circle execute workflow,code:{},operateTpye:{}",code,operateTpye);
                                workflowExecuteThreadPool.addNewWorkflow(code, restTemplate, workflowConfig.getRequestUrl(), Integer.valueOf(operateTpye));
                            }catch(Exception ex){
                                log.error("circle execute workflow error,code:{}",code);
                            }
                            BaseContext.getWorkflowMap().remove(code);
                            if(Objects.nonNull(workflowEntity.getNextCode())&&StringUtil.isNotEmpty(workflowEntity.getNextCode())){
                                currentTime.add(Calendar.MINUTE, workflowEntity.getTime().intValue());
                                BaseContext.getWorkflowMap().put(workflowEntity.getNextCode(), currentTime.getTime());
                            }
                            if(Objects.nonNull(workflowEntity.getRefCode())&& StringUtil.isNotEmpty(workflowEntity.getRefCode())){
                                log.info("circle execute workflow,refcode:{}",workflowEntity.getRefCode());
                                try {
                                    workflowExecuteThreadPool.addNewWorkflow(workflowEntity.getRefCode(), restTemplate, workflowConfig.getRequestUrl(), 1);
                                }catch(Exception ex){
                                    log.error("circle execute workflow error,refcode:{}",workflowEntity.getRefCode());
                                }
                            }
                        }
                    }
                });

            }
        } catch (Exception e) {
            log.error("");
        }
    };

}
