package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.TrainHistoryDO;
import cn.dcstd.web.mindlift3.entity.TrainListVO;
import cn.dcstd.web.mindlift3.entity.TrainLogicLibDO;
import cn.dcstd.web.mindlift3.service.TrainService;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;

    /**
     * 获取训练列表
     * @return
     */
    @GetMapping("/list")
    public Result getTrainList() {
        List<TrainListVO> trainList = trainService.getTrainList();
        return Result.success(trainList);
    }

    /**
     * 获取训练详情
     * @param id 训练id
     * @param nextId 下一个对话的id
     * @return
     */
    @GetMapping("/detail")
    public Result getTrainDetail(@RequestParam("id") int id, @RequestParam("nextId") String nextId, @RequestParam("historyId") int historyId) {
        int nextIdInt;
        TrainLogicLibDO trainLogicLibDO;
        if(nextId == null || nextId.isEmpty() || "-1".equals(nextId)) {
            nextId = "-1";
            trainService.addCount(id);
            historyId = trainService.getHistoryId(TokenUtil.getCurrentUser().getId(), id);
        }
        try {
            nextIdInt = Integer.parseInt(nextId);
            trainLogicLibDO = trainService.getTrainDetail(id, nextIdInt);
            if(historyId != -1) {
                //更新进度
                trainService.updateHistory(id, nextIdInt);
            }

        } catch (NumberFormatException e) {
            trainLogicLibDO = trainService.getTrainDetail(id, -1);
        }
        trainLogicLibDO.setHistoryId(historyId);

        return Result.success(trainLogicLibDO);

    }

    /**
     * 获取历史训练记录
     * @return
     */
    @GetMapping("/history")
    public Result getTrainHistory() {
        List<TrainHistoryDO> trainLogicLibDO = trainService.getTrainHistory(TokenUtil.getCurrentUser().getId());
        return Result.success(trainLogicLibDO);
    }

}
