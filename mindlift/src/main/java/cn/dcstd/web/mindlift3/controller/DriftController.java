package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.DriftBottleReceivedVO;
import cn.dcstd.web.mindlift3.entity.DriftBottleVO;
import cn.dcstd.web.mindlift3.service.DriftBottleService;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/drift")
public class DriftController {

    @Resource
    private DriftBottleService driftBottleService;

    /**
     * 获取随机漂流瓶
     * @return 随机漂流瓶
     */
    @GetMapping("/random")
    public Result getRandomDriftBottle() {
        List<DriftBottleVO> driftBottleVOList = driftBottleService.getRandomDriftBottle(TokenUtil.getCurrentUser().getId());
        return Result.success(driftBottleVOList);
    }

    /**
     * 获取我发送的漂流瓶
     * @return
     */
    @GetMapping("/my")
    public Result getMyDriftBottle() {
        List<DriftBottleVO> driftBottleVOList = driftBottleService.getMyDriftBottle(TokenUtil.getCurrentUser().getId());
        return Result.success(driftBottleVOList);
    }

    /**
     * 对漂流瓶进行回复
     * @param drift_id 漂流瓶id
     * @param content 内容
     * @return
     */
    @PostMapping("/reply")
    public Result replyDriftBottle(@RequestParam("drift_id") int drift_id, @RequestParam("content") String content) {
        driftBottleService.replyDriftBottle(drift_id, content, TokenUtil.getCurrentUser().getId());
        return Result.success("回复成功");
    }

    /**
     * 获取我回复过的漂流瓶
     * @return
     */
    @GetMapping("/replied")
    public Result replied() {
        List<DriftBottleVO> driftBottleVOList = driftBottleService.getMyRepliedDriftBottle(TokenUtil.getCurrentUser().getId());
        return Result.success(driftBottleVOList);
    }

    /**
     * 获取我的漂流瓶接收的回复
     * @return
     */
    @GetMapping("/received")
    public Result received() {
        List<DriftBottleReceivedVO> driftBottleReceivedVO = driftBottleService.getMyReceivedDriftBottle(TokenUtil.getCurrentUser().getId());
        return Result.success(driftBottleReceivedVO);
    }



}
