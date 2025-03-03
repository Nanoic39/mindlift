package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.*;
import cn.dcstd.web.mindlift3.service.FileService;
import cn.dcstd.web.mindlift3.service.VideoService;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private VideoService videoService;

    @Resource
    private GlobalConfiguration globalConfiguration;

    @Resource
    private FileService fileService;

    /**
     * 根据类型获取视频列表
     * @param type 类型
     * @param page 页
     * @param num 每页数量
     * @return
     */
    @GetMapping("/type")
    public Result getVideoType(@RequestParam("type") int type,
                               @RequestParam("page") int page,
                               @RequestParam("num") int num) {

        List<VideoListDTO> videoTypeList = videoService.getVideoTypeList(type, page, num);

        // TODO: 将VideoListDTO转换为VideoListVO
        List<VideoListVO> videoTypeListVO = videoTypeList.stream().map(videoTypeListItem -> {
            VideoListVO videoTypeListVOItem = new VideoListVO();
            videoTypeListVOItem.setVideoFileName(videoTypeListItem.getFileName());
            videoTypeListVOItem.setVideoFilePath(videoTypeListItem.getFilePath());

            VideoSenderDTO videoSenderDTO = videoService.getVideoExtraInfoSenderByVideoId(videoTypeListItem.getSenderId());
            videoTypeListVOItem.setSenderName(videoSenderDTO.getSenderName());

            FileLibDTO senderAvatarFilePathDTO = fileService.selectFilePathById(videoSenderDTO.getSenderAvatarId());
            String senderAvatarFilePath = String.format("%s/%s/%s.%s", globalConfiguration.getBaseFileURL(), senderAvatarFilePathDTO.getFileType(), senderAvatarFilePathDTO.getFileName(), senderAvatarFilePathDTO.getFileExtension());

            videoTypeListVOItem.setSenderAvatar(senderAvatarFilePath);

            int likedCount = videoService.getVideoLikedCountByVideoId(videoTypeListItem.getId());
            videoTypeListVOItem.setLikedCount(likedCount);

            FileLibDTO coverFilePathDTO = fileService.selectFilePathById(videoTypeListItem.getCoverFileId());
            String coverFilePath = String.format("%s/%s/%s.%s", globalConfiguration.getBaseFileURL(), coverFilePathDTO.getFileType(), coverFilePathDTO.getFileName(), coverFilePathDTO.getFileExtension());
            videoTypeListVOItem.setCoverFilePath(coverFilePath);
            return videoTypeListVOItem;
        }).toList();
        return Result.success(videoTypeListVO);
    }

    /**
     * 获取随机视频
     * @param num 数量
     * @return
     */
    @GetMapping("/random")
    public Result getRandomVideo(@RequestParam("num") int num) {
        List<VideoListDTO> videoList = videoService.getRandomVideoList(num);

        // TODO: 将VideoListDTO转换为VideoListVO
        List<VideoListVO> videoListVO = videoList.stream().map(videoListDTO -> {
            VideoListVO videoListVOItem = new VideoListVO();
            videoListVOItem.setVideoFileName(videoListDTO.getFileName());
            videoListVOItem.setVideoFilePath(videoListDTO.getFilePath());

            VideoSenderDTO videoSenderDTO = videoService.getVideoExtraInfoSenderByVideoId(videoListDTO.getSenderId());
            videoListVOItem.setSenderName(videoSenderDTO.getSenderName());

            FileLibDTO senderAvatarFilePathDTO = fileService.selectFilePathById(videoSenderDTO.getSenderAvatarId());
            String senderAvatarFilePath = String.format("%s/%s/%s.%s", globalConfiguration.getBaseFileURL(), senderAvatarFilePathDTO.getFileType(), senderAvatarFilePathDTO.getFileName(), senderAvatarFilePathDTO.getFileExtension());

            videoListVOItem.setSenderAvatar(senderAvatarFilePath);

            int likedCount = videoService.getVideoLikedCountByVideoId(videoListDTO.getId());
            videoListVOItem.setLikedCount(likedCount);

            FileLibDTO coverFilePathDTO = fileService.selectFilePathById(videoListDTO.getCoverFileId());
            String coverFilePath = String.format("%s/%s/%s.%s", globalConfiguration.getBaseFileURL(), coverFilePathDTO.getFileType(), coverFilePathDTO.getFileName(), coverFilePathDTO.getFileExtension());
            videoListVOItem.setCoverFilePath(coverFilePath);
            return videoListVOItem;
        }).toList();

        return Result.success(videoListVO);
    }

    /**
     * 点赞视频
     * @param id 视频id
     * @return
     */
    @PostMapping("/like")
    public Result likeVideo(@RequestParam("id") int id) {
        videoService.likeVideo(id, TokenUtil.getCurrentUser().getId());
        return Result.success();
    }

}
