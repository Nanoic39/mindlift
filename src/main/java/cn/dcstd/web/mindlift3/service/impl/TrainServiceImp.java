package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.controller.FileController;
import cn.dcstd.web.mindlift3.entity.TrainHistoryDO;
import cn.dcstd.web.mindlift3.entity.TrainLibDO;
import cn.dcstd.web.mindlift3.entity.TrainListVO;
import cn.dcstd.web.mindlift3.entity.TrainLogicLibDO;
import cn.dcstd.web.mindlift3.mapper.TrainMapper;
import cn.dcstd.web.mindlift3.service.TrainService;
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
public class TrainServiceImp implements TrainService {

    @Resource
    private TrainMapper trainMapper;

    @Resource
    private FileUtil fileUtil;


    @Override
    public List<TrainListVO> getTrainList() {
        List<TrainLibDO> trainLibDOList = trainMapper.getTrainList();
        List<TrainListVO> trainListVOList = new ArrayList<>();
        for (TrainLibDO trainLibDO : trainLibDOList) {
            TrainListVO trainListVO = new TrainListVO();
            trainListVO.setId(trainLibDO.getId());
            trainListVO.setTitle(trainLibDO.getTitle());
            trainListVO.setCount(trainLibDO.getCount());
            trainListVO.setCover(fileUtil.getFilePath(trainLibDO.getCoverId()));
            trainListVOList.add(trainListVO);
        }

        return trainListVOList;
    }

    @Override
    public TrainLogicLibDO getTrainDetail(int id, int nextId) {
        TrainLogicLibDO trainLogicLibDO;
        if(nextId == -1){
            trainLogicLibDO = trainMapper.getFirstTrainDetail(id);
        } else {
            trainLogicLibDO = trainMapper.getTrainDetail(id, nextId);
        }

        return trainLogicLibDO;
    }

    @Override
    public void addCount(int id) {
        trainMapper.addCount(id);
        trainMapper.addHistory(TokenUtil.getCurrentUser().getId(), id);
    }

    @Override
    public void updateHistory(int id, int nextIdInt) {
        trainMapper.updateHistory(id, nextIdInt);
    }

    @Override
    public int getHistoryId(int uid, int tid) {
        return trainMapper.getHistoryId(uid, tid);
    }

    @Override
    public List<TrainHistoryDO> getTrainHistory(int uid) {
        return trainMapper.getTrainHistoryListByUid(uid);
    }
}
