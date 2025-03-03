package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.TrainHistoryDO;
import cn.dcstd.web.mindlift3.entity.TrainListVO;
import cn.dcstd.web.mindlift3.entity.TrainLogicLibDO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public interface TrainService {

    List<TrainListVO> getTrainList();

    TrainLogicLibDO getTrainDetail(int id, int nextId);

    void addCount(int id);

    void updateHistory(int id, int nextIdInt);

    int getHistoryId(int uid, int tid);

    List<TrainHistoryDO> getTrainHistory(int id);
}
