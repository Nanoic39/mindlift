package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.JudgeDetailVO;
import cn.dcstd.web.mindlift3.entity.JudgeHistoryVO;
import cn.dcstd.web.mindlift3.entity.JudgeQuestionLibVO;
import cn.dcstd.web.mindlift3.entity.JudgeVO;
import cn.dcstd.web.mindlift3.service.JudgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/judge")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    /**
     * 获取测评列表
     * @return
     */
    @GetMapping("/list")
    public Result getJudgeList() {
        List<JudgeVO> judgeList = judgeService.getJudgeList();
        return Result.success(judgeList);
    }

    /**
     * 获取测评详情
     * @param id 测评id
     * @return
     */
    @GetMapping("/detail")
    public Result getJudgeDetail(@RequestParam("id") int id) {
        JudgeDetailVO judgeDetailVO = judgeService.getJudgeDetail(id);
        return Result.success(judgeDetailVO);
    }

    /**
     * 获取测评题目
     * @param id 测评id
     * @return
     */
    @GetMapping("/question")
    public Result getJudgeQuestion(@RequestParam("id") int id) {
        judgeService.addHistory(id);
        List<JudgeQuestionLibVO> judgeQuestionLibVOList = judgeService.getJudgeQuestion(id);
        return Result.success(judgeQuestionLibVOList);
    }

    /**
     * 更新测评历史的进度
     * @param hid 测评历史id
     * @param progress 进度
     * @return
     */
    @PostMapping("/update/history")
    public Result updateJudgeHistory(@RequestBody int hid, @RequestBody String progress) {
        judgeService.updateHistory(hid, progress);
        return Result.success();
    }

    /**
     * 获取测评历史
     * @return
     */
    @GetMapping("/history")
    public Result getJudgeHistory() {
        List<JudgeHistoryVO> judgeHistoryVOList = judgeService.getJudgeHistory();
        return Result.success(judgeHistoryVOList);
    }



}
