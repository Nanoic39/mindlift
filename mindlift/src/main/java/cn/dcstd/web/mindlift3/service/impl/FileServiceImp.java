package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.FileLibDO;
import cn.dcstd.web.mindlift3.entity.FileLibDTO;
import cn.dcstd.web.mindlift3.exception.CustomException;
import cn.dcstd.web.mindlift3.exception.GlobalException;
import cn.dcstd.web.mindlift3.mapper.FileMapper;
import cn.dcstd.web.mindlift3.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

/**
 * @author NaNo1c
 */
@Service
public class FileServiceImp implements FileService {

    @Resource
    private FileMapper fileMapper;

    /**
     * 文件上传
     * @param fileName 文件名
     * @param file 文件
     * @return 文件名.文件类型
     * @throws IOException
     */
    @Override
    public String fileUpload(String fileName, MultipartFile file) throws IOException {
        // 获取文件类型
        String originalFilename = file.getOriginalFilename();
        String fileType = getFileType(originalFilename);

        // 生成目标路径
        String uploadDir = "file/" + fileType + "/";
        Path targetPath = Paths.get(uploadDir);
        // 确保路径存在
        Files.createDirectories(targetPath);

        // 生成目标文件名
        String timestamp = String.valueOf(new Date().getTime());
        String baseName = null;
        if (originalFilename != null) {
            baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        }
        String targetFilename = timestamp + "-" + fileName + "-" + baseName;
        Path targetFile = null;
        if (originalFilename != null) {
            targetFile = targetPath.resolve(targetFilename + "." + getFileExtension(originalFilename));
        }

        // 保存文件
        try (InputStream inputStream = file.getInputStream()) {
            if (targetFile != null) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return targetFilename + "." + fileType + "." + getFileExtension(originalFilename);
    }

    /**
     * 插入文件信息
     *
     * @param uid      用户id
     * @param filetype 文件类型
     * @param filename 文件名
     */
    @Override
    public void insertFileInfo(int uid, String filetype, String filename, String fileextension) {
        fileMapper.insertFileInfo(uid, filetype, filename, fileextension);
    }

    /**
     * 根据id查询文件路径
     * @param id 文件id
     * @return
     */
    @Override
    public FileLibDTO selectFilePathById(int id) {
        FileLibDO fileLibDO = fileMapper.selectFilePathById(id);
        if (fileLibDO == null) {
            throw new CustomException(GlobalException.EMPTY);
        }

        FileLibDTO fileLibDTO = new FileLibDTO();
        fileLibDTO.setId(fileLibDO.getId());
        fileLibDTO.setFileType(fileLibDO.getFileType());
        fileLibDTO.setFileName(fileLibDO.getFileName());
        fileLibDTO.setFileExtension(fileLibDO.getFileExtension());

        return fileLibDTO;
    }

    /**
     * 查询最后一个文件id
     * @return 文件id
     */
    @Override
    public int selectLastId() {
        return fileMapper.selectLastId();
    }

    /**
     * 插入视频信息
     * @param fileId 文件id
     * @param videoTypeId 视频类型id
     * @param coverId 封面id
     * @param title 标题
     * @param intro 简介
     */
    @Override
    public void insertVideoLib(int fileId, int videoTypeId, int coverId, String title, String intro) {
        fileMapper.insertVideoLib(fileId, videoTypeId, coverId, title, intro);
    }

    /**
     * 根据原始文件名{文件名.后缀}获取文件类型
     * @param filename 文件名
     * @return "image" | "video" | "audio" | "other"
     */
    private String getFileType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif" -> "image";
            case "mp4", "avi", "mkv" -> "video";
            case "mp3", "wav" -> "audio";
            default -> "other";
        };
    }

    /**
     * 根据原始文件名获取文件后缀
     * @param filename 文件名
     * @return 文件后缀
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

}
