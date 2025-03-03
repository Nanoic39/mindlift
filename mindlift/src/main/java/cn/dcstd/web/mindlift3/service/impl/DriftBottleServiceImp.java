package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.DriftBottleLibDO;
import cn.dcstd.web.mindlift3.entity.DriftBottleReceivedVO;
import cn.dcstd.web.mindlift3.entity.DriftBottleRepDO;
import cn.dcstd.web.mindlift3.entity.DriftBottleVO;
import cn.dcstd.web.mindlift3.mapper.AuthMapper;
import cn.dcstd.web.mindlift3.mapper.DriftBottleMapper;
import cn.dcstd.web.mindlift3.service.DriftBottleService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author NaNo1c
 */
@Service
public class DriftBottleServiceImp implements DriftBottleService {

    @Resource
    private DriftBottleMapper driftBottleMapper;

    @Resource
    private AuthMapper authMapper;

    @Override
    public List<DriftBottleVO> getRandomDriftBottle(int uid) {
        List<DriftBottleLibDO> driftBottleLibDOList = driftBottleMapper.getRandomDriftBottleList();
        return getDriftBottleVOS(driftBottleLibDOList);
    }

    /**
     * 获取我的漂流瓶
     * @param id
     * @return 列表
     */
    @Override
    public List<DriftBottleVO> getMyDriftBottle(int id) {
        List<DriftBottleLibDO> driftBottleLibDOList = driftBottleMapper.getMyDriftBottleList(id);
        return getDriftBottleVOS(driftBottleLibDOList);
    }

    /**
     * 回复漂流瓶
     * @param driftId
     * @param content
     * @param id
     */
    @Override
    public void replyDriftBottle(int driftId, String content, int id) {
        driftBottleMapper.replyDriftBottle(driftId, content, id);
    }

    /**
     * 获取我回复的漂流瓶
     * @param id
     * @return
     */
    @Override
    public List<DriftBottleVO> getMyRepliedDriftBottle(int id) {
        List<DriftBottleVO> driftBottleVOList = new ArrayList<>();
        List<DriftBottleRepDO> driftBottleRepDOList = driftBottleMapper.getMyReplyDriftBottle(id);
        for (DriftBottleRepDO driftBottleRepDO : driftBottleRepDOList) {
            DriftBottleVO driftBottleVOItem = new DriftBottleVO();
            DriftBottleLibDO driftBottleLibDO = driftBottleMapper.getDriftBottleById(driftBottleRepDO.getDriftBottleId());
            driftBottleVOItem.setId(driftBottleLibDO.getId());
            driftBottleVOItem.setSenderId(driftBottleLibDO.getUserId());
            driftBottleVOItem.setContent(driftBottleRepDO.getContent());
            driftBottleVOItem.setTags(driftBottleLibDO.getTags());
            driftBottleVOList.add(driftBottleVOItem);
        }
        return driftBottleVOList;
    }

    @Override
    public List<DriftBottleReceivedVO> getMyReceivedDriftBottle(int id) {
        List<DriftBottleReceivedVO> driftBottleReceivedVOList = new ArrayList<>();
        // 获取我发布的漂流瓶列表
        List<DriftBottleLibDO> myDriftBottleList = driftBottleMapper.getMyDriftBottleList(id);

        // 遍历id
        for (DriftBottleLibDO myDriftBottle : myDriftBottleList) {
            // 获取回复我的漂流瓶列表
            List<DriftBottleRepDO> replyDriftBottleList = driftBottleMapper.getReplyMyDriftBottle(myDriftBottle.getId());
            // 数据格式化
            for (DriftBottleRepDO replyDriftBottle : replyDriftBottleList) {
                DriftBottleReceivedVO driftBottleReceivedVOItem = new DriftBottleReceivedVO();
                driftBottleReceivedVOItem.setId(replyDriftBottle.getId());
                driftBottleReceivedVOItem.setDriftBottleId(replyDriftBottle.getDriftBottleId());
                driftBottleReceivedVOItem.setNickName(String.format("匿名用户%s", (replyDriftBottle.getUserId() + 114)));
                driftBottleReceivedVOItem.setContent(replyDriftBottle.getContent());
                driftBottleReceivedVOItem.setOriContent(myDriftBottle.getContent());
                driftBottleReceivedVOItem.setCreateTime(replyDriftBottle.getCreateTime());

                driftBottleReceivedVOList.add(driftBottleReceivedVOItem);
            }

        }

        return driftBottleReceivedVOList;
    }

    private List<DriftBottleVO> getDriftBottleVOS(List<DriftBottleLibDO> driftBottleLibDOList) {
        List<DriftBottleVO> driftBottleVOList = new ArrayList<>();
        for (DriftBottleLibDO driftBottleLibDO : driftBottleLibDOList) {
            DriftBottleVO driftBottleVOItem = new DriftBottleVO();
            driftBottleVOItem.setId(driftBottleLibDO.getId());
            driftBottleVOItem.setSenderId(driftBottleLibDO.getUserId());
            driftBottleVOItem.setContent(driftBottleLibDO.getContent());
            driftBottleVOItem.setTags(driftBottleLibDO.getTags());
            driftBottleVOList.add(driftBottleVOItem);
        }
        return driftBottleVOList;
    }
}
