package com.simulation.ciic.biz.config;

import lombok.Data;

/**
 * 流程配置
 *
 * @author chengong
 * @company GeekPlus
 * @create 2019-08-24 9:41
 **/
@Data
public class WorkflowEntity {
    private String code;
    private String refCode;
    private String nextCode;
    private String preCode;

    /**
     * 0 送 1取
     */
    private String operateType;

    private Double time;

}
