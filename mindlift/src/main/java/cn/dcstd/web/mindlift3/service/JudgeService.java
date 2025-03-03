package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.JudgeDetailVO;
import cn.dcstd.web.mindlift3.entity.JudgeHistoryVO;
import cn.dcstd.web.mindlift3.entity.JudgeQuestionLibVO;
import cn.dcstd.web.mindlift3.entity.JudgeVO;
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
}
