package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.DiaryHistoryDO;
import cn.dcstd.web.mindlift3.entity.DiaryListVO;
import cn.dcstd.web.mindlift3.mapper.DiaryMapper;
import cn.dcstd.web.mindlift3.service.DiaryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public class DiaryServiceImp implements DiaryService {
    @Resource
    private DiaryMapper diaryMapper;

    @Override
    public List<DiaryListVO> getMyDiaryList(int userId) {
        List<DiaryHistoryDO> diaryHistoryDOList = diaryMapper.getMyDiaryList(userId);
        List<DiaryListVO> diaryListVOList = new ArrayList<>();
        for (DiaryHistoryDO diaryHistoryDO : diaryHistoryDOList) {
            DiaryListVO diaryListVO = new DiaryListVO();
            diaryListVO.setId(diaryHistoryDO.getId());
            diaryListVO.setTodayStatus(diaryHistoryDO.getTodayStatus());
            diaryListVO.setTitle(diaryHistoryDO.getTitle());
            diaryListVO.setContent(diaryHistoryDO.getContent());
            diaryListVO.setCreateTime(diaryHistoryDO.getCreateTime());
            diaryListVO.setUpdateTime(diaryHistoryDO.getUpdateTime());

            diaryListVOList.add(diaryListVO);
        }
        return diaryListVOList;
    }

    @Override
    public void addDiary(int userId, String todayStatus, String title, String content) {
        diaryMapper.addDiary(userId, todayStatus, title, content);
    }

    @Override
    public DiaryHistoryDO getLastDiary(int id) {
        DiaryHistoryDO diaryHistoryDO = diaryMapper.getLastDiary(id);
        return diaryHistoryDO;
    }
}
