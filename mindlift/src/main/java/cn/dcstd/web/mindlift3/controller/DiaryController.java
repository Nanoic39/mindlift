package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.DiaryListVO;
import cn.dcstd.web.mindlift3.exception.GlobalException;
import cn.dcstd.web.mindlift3.service.DiaryService;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Resource
    private DiaryService diaryService;

    /**
     * 获取我的心情日记列表
     * @return
     */
    @GetMapping("/me")
    public Result getDiaryList() {
        int userId = TokenUtil.getCurrentUser().getId();
        List<DiaryListVO> diaryList = diaryService.getMyDiaryList(userId);
        if (diaryList == null) {
            return Result.fail(GlobalException.EMPTY.getCode(), GlobalException.EMPTY.getMsg(), "暂无心情日记数据");
        }
        return Result.success(diaryList);
    }

    /**
     * 添加心情日记
     * @param todayStatus 今日状态
     * @param title 标题
     * @param content 内容
     * @return
     */
    @PostMapping("/add")
    public Result addDiary(String todayStatus, String title, String content) {
        int userId = TokenUtil.getCurrentUser().getId();
        diaryService.addDiary(userId, todayStatus, title, content);
        return Result.success();
    }
}
