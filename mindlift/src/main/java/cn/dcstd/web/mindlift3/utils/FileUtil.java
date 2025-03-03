package cn.dcstd.web.mindlift3.utils;

import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.FileLibDTO;
import cn.dcstd.web.mindlift3.entity.FileLibVO;
import cn.dcstd.web.mindlift3.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {

    @Resource
    private FileService fileService;

    @Resource
    private GlobalConfiguration globalConfiguration;

    public String getFilePath(int id) {
        FileLibDTO fileLibDTO = fileService.selectFilePathById(id);
        return String.format("%s/%s/%s.%s",
                globalConfiguration.getBaseFileURL(),
                fileLibDTO.getFileType(),
                fileLibDTO.getFileName(),
                fileLibDTO.getFileExtension());
    }
}
