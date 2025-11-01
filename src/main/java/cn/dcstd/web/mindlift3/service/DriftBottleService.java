package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.DriftBottleReceivedVO;
import cn.dcstd.web.mindlift3.entity.DriftBottleVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DriftBottleService {

    List<DriftBottleVO> getRandomDriftBottle(int uid);

    List<DriftBottleVO> getMyDriftBottle(int id);

    void replyDriftBottle(int driftId, String content, int id);

    List<DriftBottleVO> getMyRepliedDriftBottle(int id);

    List<DriftBottleReceivedVO> getMyReceivedDriftBottle(int id, int uid);

    void sendDriftBottle(String content, int id);

    List<DriftBottleReceivedVO> getAllMyReceivedDriftBottle(int id);
}
