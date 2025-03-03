package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.DiaryHistoryDO;
import cn.dcstd.web.mindlift3.entity.DiaryListVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public interface DiaryService {

    List<DiaryListVO> getMyDiaryList(int userId);

    void addDiary(int userId, String todayStatus, String title, String content);

    DiaryHistoryDO getLastDiary(int id);
}
