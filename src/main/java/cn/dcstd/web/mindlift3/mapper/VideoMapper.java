package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.VideoLibDO;
import cn.dcstd.web.mindlift3.entity.VideoListDTO;
import cn.dcstd.web.mindlift3.entity.VideoSenderDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NaNo1c
 */
@Mapper
public interface VideoMapper {
    @Select("SELECT * FROM video_lib WHERE id >= (SELECT FLOOR( MAX(id) * RAND()) FROM video_lib ) ORDER BY id LIMIT #{num}; ")
    List<VideoLibDO> getRandomVideoList(@Param("num") int num);

    @Select("SELECT name as senderName, avatarId as senderAvatarId FROM video_creator WHERE id = #{id}")
    VideoSenderDTO getExtraInfoSenderByVideoId(@Param("id") int id);

    @Select("SELECT count(*) FROM video_liked WHERE videoId = #{id}")
    int getVideoLikedCountByVideoId(int id);

    @Insert("insert into video_liked(videoId, likerId) values(#{id}, #{uid})")
    void likeVideo(@Param("id") int id,@Param("uid") int uid);

    @Select("SELECT * FROM video_lib WHERE typeId = #{type} LIMIT #{page}, #{num}")
    List<VideoLibDO> getVideoTypeList(int type, int page, int num);
}
