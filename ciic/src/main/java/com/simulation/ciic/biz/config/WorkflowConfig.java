package com.simulation.ciic.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * desc
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-23 14:50
 **/
@Data
@Component
@ConfigurationProperties(prefix = "workflow")
public class WorkflowConfig {
    private String requestUrl;
    private List<WorkflowEntity> workflowList;
}
