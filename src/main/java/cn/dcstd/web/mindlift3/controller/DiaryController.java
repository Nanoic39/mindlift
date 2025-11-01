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

    //TODO:
    /**
     * 1，	用户输入漂流瓶文本
     * 2，	NLP情感分析：识别情绪类型、强度
     * 3，	匹配算法：
     * a)	需要鼓励 → 匹配“鼓励型倾听者”
     * b)	需要经验 → 匹配“相似经历用户”
     * 4，	安全审核：敏感内容过滤+隐私保护
     * 5，	用户收到回应，可继续匿名交流
     *
     * o	情绪智能分析：识别用户投递漂流瓶的情感倾向
     * 	文本情感分析（Sentime+
     * 	nt Analysis）：使用BERT+情绪分类模型，对用户输入的漂流瓶文本进行情绪分析，判断其情感类别（如悲伤、焦虑、愤怒、压力等）。
     * o	智能匹配倾听者：为用户匹配合适的倾听者，使匿名交流更有效
     * 	用户画像+情绪标签匹配：每个用户在匿名模式下仍会有一个情绪交互画像，包含：
     * 	历史互动记录（是否曾经安慰过他人）
     * 	相似情绪经历（是否表达过类似情绪）
     * 	互动偏好（更愿意安慰他人 or 更愿意获得倾听）
     * 	匹配策略
     * 	鼓励需求匹配（用户需要鼓励）：若用户表达负面情绪（如“我好难过”），系统会匹配过去曾提供积极回应的用户，如“鼓励系倾听者”（有过安慰行为）。
     * 	经验分享匹配（用户表达困惑）：若用户表达心理困惑（如“我该怎么克服社交恐惧？”），系统会匹配有类似经历的用户，提供共鸣式交流。
     * 	匹配方式
     * 	采用向量相似度计算（Cosine Similarity），基于用户历史漂流瓶+情绪标签，计算当前用户的情绪向量，与数据库中的倾听者匹配：
     *
     * 	优先匹配原则
     * 	① 优先匹配有积极安慰记录的用户
     * 	② 匹配拥有相似经历的用户
     * 	③ 匹配最近活跃的用户，避免长时间等待
     * o	安全性与隐私保护机制
     * 	完全匿名ID机制：系统为每个用户创建**“匿名小星”ID**，所有互动均以虚拟身份进行，不显示用户任何个人信息。
     * 	文本审核（敏感内容过滤）：采用xxxx方法，防止漂流瓶中含有攻击性、不适宜内容。
     * 	情绪紧急预警：若AI检测到用户有极端情绪（如自伤、自杀暗示），可触发紧急干预机制，建议用户获取专业心理支持。
     */

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
