package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.FileLibDO;
import cn.dcstd.web.mindlift3.entity.VideoLibDO;
import cn.dcstd.web.mindlift3.entity.VideoListDTO;
import cn.dcstd.web.mindlift3.entity.VideoSenderDTO;
import cn.dcstd.web.mindlift3.mapper.FileMapper;
import cn.dcstd.web.mindlift3.mapper.VideoMapper;
import cn.dcstd.web.mindlift3.service.VideoService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NaNo1c
 */
@Service
public class VideoServiceImp implements VideoService {
    @Resource
    private VideoMapper videoMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private GlobalConfiguration globalConfiguration;

    @Override
    public List<VideoListDTO> getRandomVideoList(int num) {
        List<VideoLibDO> videoLibDOList = videoMapper.getRandomVideoList(num);
        return getVideoListDTOS(videoLibDOList);
    }


    /**
     * 获取视频的发送者相关信息(名称和头像)
     * @param id 视频id
     * @return 发送者相关信息
     */
    @Override
    public VideoSenderDTO getVideoExtraInfoSenderByVideoId(int id) {
        return videoMapper.getExtraInfoSenderByVideoId(id);
    }

    /**
     * 获取视频点赞数
     * @param id 视频id
     * @return 点赞数
     */
    @Override
    public int getVideoLikedCountByVideoId(int id) {
        return videoMapper.getVideoLikedCountByVideoId(id);
    }


    @Override
    public void likeVideo(int id, int uid) {
        videoMapper.likeVideo(id, uid);
    }

    @Override
    public List<VideoListDTO> getVideoTypeList(int type, int page, int num) {
        List<VideoLibDO> videoLibDOList = videoMapper.getVideoTypeList(type, page, num);
        return getVideoListDTOS(videoLibDOList);
    }

    private List<VideoListDTO> getVideoListDTOS(List<VideoLibDO> videoLibDOList) {
        return videoLibDOList.stream().map(videoLibDO -> {
            VideoListDTO videoListDTO = new VideoListDTO();
            videoListDTO.setId(videoLibDO.getId());
            videoListDTO.setFileId(videoLibDO.getVideoFileId());
            videoListDTO.setTypeId(videoLibDO.getTypeId());
            FileLibDO fileLibDO = fileMapper.selectFilePathById(videoLibDO.getVideoFileId());
            videoListDTO.setFileName(fileLibDO.getFileName());
            String filePath = String.format("%s/%s/%s.%s", globalConfiguration.getBaseFileURL(), fileLibDO.getFileType(), fileLibDO.getFileName(), fileLibDO.getFileExtension());
            videoListDTO.setFilePath(filePath);
            videoListDTO.setSenderId(videoLibDO.getSenderId());
            videoListDTO.setCoverFileId(videoLibDO.getCoverFileId());
            return videoListDTO;
        }).toList();
    }

}
