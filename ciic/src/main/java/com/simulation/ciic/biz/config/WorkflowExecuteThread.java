package com.simulation.ciic.biz.config;

import com.geekplus.optimus.common.util.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程线程
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-24 14:07
 **/
public class WorkflowExecuteThread implements Runnable{

    private RestTemplate restTemplate;

    private String workflowCode;

    private String url;

    /**
     * 0 送 1取
     */
    private Integer operationType;

    public WorkflowExecuteThread(String workflowCode,RestTemplate restTemplate,String url,Integer operationType) {
        this.workflowCode=workflowCode;
        this.restTemplate=restTemplate;
        this.url=url;
        this.operationType=operationType;
    }

    @Override
    public void run() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> params = new HashMap<>();
        params.put("workflowCode",workflowCode);
        params.put("operationType",operationType);
        HttpEntity<String> formEntity = new HttpEntity<>(JSONUtil.objToJson(params), headers);
        restTemplate.postForEntity(url, formEntity, Map.class).getBody();
    }
}
