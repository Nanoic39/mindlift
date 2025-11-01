package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public interface JudgeService {

    List<JudgeVO> getJudgeList();

    JudgeDetailVO getJudgeDetail(int id);

    List<JudgeQuestionLibVO> getJudgeQuestion(int id);

    void addHistory(int id);

    void updateHistory(int hid, String progress);

    List<JudgeHistoryVO> getJudgeHistory();

    JudgeResultVO submitJudgeAnswers(JudgeSubmitDTO submitDTO);

    JudgeResultVO getJudgeResult(int resultId);

    List<JudgeResultVO> getJudgeResultHistory(int judgeId);
}
