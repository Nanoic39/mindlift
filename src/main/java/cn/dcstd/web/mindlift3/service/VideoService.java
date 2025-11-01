package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.VideoListDTO;
import cn.dcstd.web.mindlift3.entity.VideoSenderDTO;
import cn.dcstd.web.mindlift3.entity.VideoTypeDO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public interface VideoService {
    List<VideoListDTO> getRandomVideoList(int num);

    VideoSenderDTO getVideoExtraInfoSenderByVideoId(int id);

    int getVideoLikedCountByVideoId(int id);

    void likeVideo(int id, int uid);

    List<VideoListDTO> getVideoTypeList(int type, int page, int num);
}
