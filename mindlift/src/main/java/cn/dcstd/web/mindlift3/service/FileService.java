package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.FileLibDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author NaNo1c
 */
@Service
public interface FileService {

    /**
     * 文件上传
     * @param fileName 文件名
     * @param file 文件
     */
    String fileUpload(String fileName, MultipartFile file) throws IOException;

    /**
     * 文件信息插入
     *
     * @param uid      用户id
     * @param filetype 文件类型
     * @param filename 文件名
     */
    void insertFileInfo(int uid, String filetype, String filename, String fileextension);

    /**
     * 根据id查询文件路径
     * @param id 文件id
     * @return FileLibDTO
     */
    FileLibDTO selectFilePathById(int id);

    /**
     * 查询最新文件id
     * @return int
     */
    int selectLastId();

    /**
     * 插入视频信息
     * @param id id
     * @param videoType 视频类型
     * @param coverId 封面id
     * @param title 视频标题
     * @param intro 视频简介
     */
    void insertVideoLib(int id, int videoType, int coverId, String title, String intro);
}
