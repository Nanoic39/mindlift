package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.*;
import cn.dcstd.web.mindlift3.exception.CustomException;
import cn.dcstd.web.mindlift3.service.FileService;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;


/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/file")
@Transactional(rollbackFor = Exception.class)
public class FileController {

    @Resource
    private FileService fileService;
    @Resource
    private GlobalConfiguration globalConfiguration;

    /**
     * 文件上传
     * @param fileName 文件名
     * @param file 文件
     * @return Result
     * @throws IOException 异常
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("fileName") String fileName, @RequestPart MultipartFile file) throws IOException {
        // 文件上传
        fileInsert(fileName, file);
        int fileId = fileService.selectLastId();

        return Result.success("文件上传成功", "上传成功", fileId);
    }


    /**
     * 视频上传
     * @param fileName 文件名
     * @param videoTypeId 文件类型id
     * @param file 文件
     * @return 正常返回
     * @throws IOException 异常
     */
    @PostMapping("/video/upload")
    public Result videoUpload(@RequestParam("fileName") String fileName,
                              @RequestParam("videoTypeId") int videoTypeId,
                              @RequestParam("title") String title,
                              @RequestParam("intro") String intro,
                              @RequestPart MultipartFile file,
                              @RequestPart MultipartFile coverFile) throws IOException {
        // 文件上传
        fileInsert(fileName, file);
        int fileId = fileService.selectLastId();

        // 封面上传
        fileInsert("cover-"+fileName, coverFile);
        int coverId = fileService.selectLastId();

        // 插入视频信息
        fileService.insertVideoLib(fileId, videoTypeId, coverId, TokenUtil.getCurrentUser().getId(), title, intro);

        return Result.success();
    }


    /**
     * 文件下载(获取文件真实路径)
     * @param id 文件id
     * @return 文件真实路径
     */
    @GetMapping("/download")
    public Result download(@RequestParam("id") int id) {
        FileLibDTO fileLibDTO = fileService.selectFilePathById(id);
        FileLibVO fileLibVO = new FileLibVO();
        fileLibVO.setFileName(fileLibDTO.getFileName());
        String filePath = String.format("%s/%s/%s.%s", globalConfiguration.getBaseFileURL(), fileLibDTO.getFileType(), fileLibDTO.getFileName(), fileLibDTO.getFileExtension());
        fileLibVO.setFilePath(filePath);
        return Result.success(fileLibVO);
    }

    /**
     * 文件下载
     * @param response 响应
     * @param filePath
     */
    @GetMapping("/file/download")
    public void fileDownload(HttpServletResponse response, @RequestParam("filePath") String filePath) {
        filePath = globalConfiguration.getBaseFileURL() + filePath;
        System.out.println("filePath: " + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new CustomException("当前下载的文件不存在，请检查路径是否正确");
        }

        // 清空 response
        response.reset();
        response.setCharacterEncoding("UTF-8");

        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(String.valueOf(e));
        }
        response.setContentType("application/octet-stream");

        // 将文件读到输入流中
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {

            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

            byte[] buffer = new byte[1024];
            int len;

            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while((len = is.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }

            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 文件上传
     * @param fileName 文件名
     * @param file 文件
     * @throws IOException 异常
     */
    private void fileInsert(String fileName, MultipartFile file) throws IOException {
        String fileInfo = fileService.fileUpload(fileName, file);

        String filename = fileInfo.split("\\.")[0];
        String filetype = fileInfo.split("\\.")[1];
        String fileextension = fileInfo.split("\\.")[2];

        int uid = TokenUtil.getCurrentUser().getId();
        fileService.insertFileInfo(uid, filetype, filename, fileextension);
    }




}
