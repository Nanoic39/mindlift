package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.*;
import cn.dcstd.web.mindlift3.mapper.JudgeMapper;
import cn.dcstd.web.mindlift3.service.JudgeService;
import cn.dcstd.web.mindlift3.utils.FileUtil;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public class JudgeServiceImp implements JudgeService {
    @Resource
    private JudgeMapper judgeMapper;

    @Resource
    private FileUtil fileUtil;

    @Override
    public List<JudgeVO> getJudgeList() {
        List<JudgeVO> judgeVOList = new ArrayList<>();
        List<JudgeLibDO> judgeLibDOList = judgeMapper.getJudgeList();
        for (JudgeLibDO judgeLibDO : judgeLibDOList) {
            JudgeVO judgeVO = new JudgeVO();
            judgeVO.setId(judgeLibDO.getId());
            judgeVO.setTitle(judgeLibDO.getTitle());
            judgeVO.setIntro(judgeLibDO.getIntro());
            judgeVO.setCover(fileUtil.getFilePath(judgeLibDO.getCoverId()));
            judgeVO.setCount(judgeLibDO.getCount());
            judgeVOList.add(judgeVO);
        }
        return judgeVOList;
    }

    @Override
    public JudgeDetailVO getJudgeDetail(int id) {
        JudgeLibDO judgeLibDO = judgeMapper.getJudgeDetail(id);
        JudgeDetailVO judgeDetailVO = new JudgeDetailVO();
        judgeDetailVO.setId(judgeLibDO.getId());
        judgeDetailVO.setTitle(judgeLibDO.getTitle());
        judgeDetailVO.setContent(judgeLibDO.getContent());
        judgeDetailVO.setCover(fileUtil.getFilePath(judgeLibDO.getCoverId()));
        judgeDetailVO.setCount(judgeLibDO.getCount());
        judgeDetailVO.setRule(judgeLibDO.getRule());
        judgeDetailVO.setSrc(judgeLibDO.getSrc());
        int num = judgeMapper.getJudgeQuestionNumById(id);
        judgeDetailVO.setNum(num);




        return judgeDetailVO;
    }

    @Override
    public List<JudgeQuestionLibVO> getJudgeQuestion(int id) {
        int hid = judgeMapper.getJudgeHistoryIdByUid(TokenUtil.getCurrentUser().getId());
        List<JudgeQuestionLibVO> judgeQuestionLibVOList = new ArrayList<>();
        List<JudgeQuestionLibDO> judgeQuestionLibDOList = judgeMapper.getJudgeQuestion(id);
        for (JudgeQuestionLibDO judgeQuestionLibDO : judgeQuestionLibDOList) {
            JudgeQuestionLibVO judgeQuestionLibVO = new JudgeQuestionLibVO();
            judgeQuestionLibVO.setId(judgeQuestionLibDO.getId());
            judgeQuestionLibVO.setMainId(judgeQuestionLibDO.getMainId());
            judgeQuestionLibVO.setQuestion(judgeQuestionLibDO.getQuestion());
            judgeQuestionLibVO.setDimension(judgeQuestionLibDO.getDimension());
            judgeQuestionLibVO.setOptions(judgeQuestionLibDO.getOptions());
            judgeQuestionLibVOList.add(judgeQuestionLibVO);
            judgeQuestionLibVO.setHistoryId(hid);
        }
        return judgeQuestionLibVOList;
    }

    @Override
    public void addHistory(int id) {
        judgeMapper.addCount(id);
        judgeMapper.addHistory(TokenUtil.getCurrentUser().getId(), id);
    }

    @Override
    public void updateHistory(int hid, String progress) {
        judgeMapper.updateHistory(hid, progress);
    }

    @Override
    public List<JudgeHistoryVO> getJudgeHistory() {
        List<JudgeHistoryVO> judgeHistoryVOList = new ArrayList<>();
        List<JudgeHistoryDO> judgeHistoryDOList = judgeMapper.getJudgeHistory(TokenUtil.getCurrentUser().getId());
        for (JudgeHistoryDO judgeHistoryDO : judgeHistoryDOList) {
            JudgeHistoryVO judgeHistoryVO = new JudgeHistoryVO();
            judgeHistoryVO.setId(judgeHistoryDO.getId());
            judgeHistoryVO.setJudgeId(judgeHistoryDO.getJudgeId());
            judgeHistoryVO.setProgress(judgeHistoryDO.getProgress());
            judgeHistoryVO.setTitle(judgeMapper.getJudgeDetail(judgeHistoryDO.getJudgeId()).getTitle());
            judgeHistoryVO.setIntro(judgeMapper.getJudgeDetail(judgeHistoryDO.getJudgeId()).getIntro());
            judgeHistoryVO.setCover(fileUtil.getFilePath(judgeMapper.getJudgeDetail(judgeHistoryDO.getJudgeId()).getCoverId()));
            judgeHistoryVO.setNum(judgeMapper.getJudgeQuestionNumById(judgeHistoryDO.getJudgeId()));
            judgeHistoryVOList.add(judgeHistoryVO);


        }

        return judgeHistoryVOList;
    }
}
