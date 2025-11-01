package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.FileLibDO;
import cn.dcstd.web.mindlift3.entity.LastId;
import org.apache.ibatis.annotations.*;

/**
 * @author NaNo1c
 */
@Mapper
public interface FileMapper {
    // 插入文件信息
    @Insert("insert into file_lib(uploaderId, fileType, fileName, fileExtension) values(#{uid}, #{filetype}, #{filename}, #{fileextension})")
    void insertFileInfo(@Param("uid") int uid,
                        @Param("filetype") String filetype,
                        @Param("filename") String filename,
                        @Param("fileextension") String fileextension);

    // 查询文件信息
    @Select("select * from file_lib where id = #{id}")
    FileLibDO selectFilePathById(@Param("id") int id);

    @Select("select id from file_lib order by id desc limit 1")
    int selectLastId();

    @Insert("insert into video_lib(videoFileId, typeId, coverFileId, senderId, title, intro) values(#{fileId}, #{videoTypeId}, #{coverId}, #{uid}, #{title}, #{intro})")
    void insertVideoLib(@Param("fileId") int fileId,
                        @Param("videoTypeId") int videoTypeId,
                        @Param("coverId") int coverId,
                        @Param("uid") int uid,
                        @Param("title") String title,
                        @Param("intro") String intro);
}
