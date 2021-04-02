package com.zone.process.domain.service;

import com.google.common.collect.Lists;
import com.zone.commons.entity.LoginUser;
import com.zone.process.domain.agg.ProcessDefAgg;
import com.zone.process.domain.agg.ProcessInstAgg;
import com.zone.process.domain.valueobject.DefNodeVO;
import com.zone.process.domain.valueobject.InstDataVO;
import com.zone.process.domain.valueobject.InstOperationVO;
import com.zone.process.shared.enums.InstanceOperationTypeEnum;
import com.zone.process.shared.enums.TaskOperationTypeEnum;
import com.zone.process.shared.process.valueobject.TaskVO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: jianyong.zhu
 * @Date: 2021/4/1 3:55 下午
 * @Description:
 */
@Service
public class InstanceDataDomainService {


    /**
     * 发起流程时保存数据
     */
    public void saveStartFormData(ProcessDefAgg defAgg, ProcessInstAgg instAgg, Map<Long, Map<String, Object>> formDataMap, String comment, LoginUser loginUser) {
        DefNodeVO nodeVO = defAgg.getNodeByNodeId(defAgg.getStartBpmnNodeId());
        List<Long> formIdList = Arrays.asList(nodeVO.getInputFormIds().split(","))
                .stream().map(tmp -> Long.valueOf(tmp)).collect(Collectors.toList());

        instAgg.setDataVOList(InstDataVO.generateDataVOList(defAgg.getStartBpmnNodeId(), formDataMap, formIdList));
        instAgg.setOperationVOList(Lists.newArrayList(InstOperationVO.generateOperationVO(defAgg.getStartBpmnNodeId(), InstanceOperationTypeEnum.START.getCode(), comment, loginUser, loginUser.getAccountName() + "发起流程实例")));
    }

    /**
     * 操作任务时保存数据
     */
    public void saveUserTaskData(ProcessInstAgg instAgg, TaskVO taskVO, String operationType, String comment,
                                 Map<Long, Map<String, Object>> formDataMap, ProcessDefAgg defAgg, LoginUser loginUser) {
        DefNodeVO nodeVO = defAgg.getNodeByNodeId(taskVO.getCurNodeId());
        List<Long> formIdList = Arrays.asList(nodeVO.getInputFormIds().split(","))
                .stream().map(tmp -> Long.valueOf(tmp)).collect(Collectors.toList());

        // 只有完成任务能够保存表单数据
        if (TaskOperationTypeEnum.COMPLETE.getCode().equals(operationType)) {
            instAgg.getDataVOList().addAll(InstDataVO.generateDataVOList(taskVO.getCurNodeId(), formDataMap, formIdList));
        }
        String ext = TaskOperationTypeEnum.COMPLETE.getCode().equals(operationType) ? loginUser.getUserName() + "提交任务" : loginUser.getUserName() + "转派任务";
        instAgg.getOperationVOList().add(InstOperationVO.generateOperationVO(taskVO.getCurNodeId(), operationType, comment, loginUser, ext));
    }
}