package cn.dcstd.web.mindlift3.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

/**
 * 语音识别服务
 * 提供同步和异步的语音识别功能
 * 
 * @author NaNo1c
 */
@Service
public class VoiceRecognitionService {

    private static final Logger logger = LoggerFactory.getLogger(VoiceRecognitionService.class);

    @Resource
    private VoskModelService voskModelService;

    /**
     * 同步语音识别
     * 
     * @param file 音频文件
     * @return 识别结果JSON字符串
     * @throws IOException 处理异常
     */
    public String recognizeAudio(MultipartFile file) throws IOException {
        return recognizeAudio(file, voskModelService.getDefaultSampleRate());
    }

    /**
     * 同步语音识别（指定采样率）
     * 
     * @param file 音频文件
     * @param sampleRate 采样率
     * @return 识别结果JSON字符串
     * @throws IOException 处理异常
     */
    public String recognizeAudio(MultipartFile file, int sampleRate) throws IOException {
        File tempAudioFile = null;
        InputStream audioInputStream = null;
        Recognizer recognizer = null;

        try {
            // 创建临时文件
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            tempAudioFile = File.createTempFile("vosk_audio_", fileExtension);
            file.transferTo(tempAudioFile);

            logger.debug("处理音频文件: {}, 大小: {} bytes", originalFilename, file.getSize());

            // 读取音频流
            try {
                audioInputStream = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(Files.newInputStream(tempAudioFile.toPath()))
                );
            } catch (UnsupportedAudioFileException e) {
                throw new IOException("不支持的音频格式。请上传WAV格式的音频文件。", e);
            }

            // 创建识别器
            recognizer = voskModelService.createRecognizer(sampleRate);

            // 处理音频数据
            int nbytes;
            byte[] buffer = new byte[4096];
            
            while ((nbytes = audioInputStream.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, nbytes)) {
                    String result = recognizer.getResult();
                    logger.debug("中间识别结果: {}", result);
                }
            }

            // 获取最终结果
            String finalResult = recognizer.getFinalResult();
            logger.info("语音识别完成，结果: {}", finalResult);

            return finalResult;

        } finally {
            // 清理资源
            closeQuietly(recognizer);
            closeQuietly(audioInputStream);
            deleteQuietly(tempAudioFile);
        }
    }

    /**
     * 异步语音识别
     * 
     * @param file 音频文件
     * @return CompletableFuture包装的识别结果
     */
    @Async
    public CompletableFuture<String> recognizeAudioAsync(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return recognizeAudio(file);
            } catch (IOException e) {
                logger.error("异步语音识别失败: {}", e.getMessage(), e);
                throw new RuntimeException("语音识别失败: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 异步语音识别（指定采样率）
     * 
     * @param file 音频文件
     * @param sampleRate 采样率
     * @return CompletableFuture包装的识别结果
     */
    @Async
    public CompletableFuture<String> recognizeAudioAsync(MultipartFile file, int sampleRate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return recognizeAudio(file, sampleRate);
            } catch (IOException e) {
                logger.error("异步语音识别失败: {}", e.getMessage(), e);
                throw new RuntimeException("语音识别失败: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 提取识别文本（从JSON结果中）
     * 
     * @param jsonResult JSON格式的识别结果
     * @return 文本内容
     */
    public String extractText(String jsonResult) {
        try {
            JSONObject json = JSON.parseObject(jsonResult);
            return json.getString("text");
        } catch (Exception e) {
            logger.warn("解析识别结果失败: {}", e.getMessage());
            return jsonResult;
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".wav";
    }

    /**
     * 安静地关闭Recognizer
     */
    private void closeQuietly(Recognizer recognizer) {
        if (recognizer != null) {
            try {
                recognizer.close();
            } catch (Exception e) {
                logger.warn("关闭Recognizer时出错: {}", e.getMessage());
            }
        }
    }

    /**
     * 安静地关闭InputStream
     */
    private void closeQuietly(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.warn("关闭InputStream时出错: {}", e.getMessage());
            }
        }
    }

    /**
     * 安静地删除文件
     */
    private void deleteQuietly(File file) {
        if (file != null && file.exists()) {
            try {
                Files.delete(file.toPath());
                logger.debug("临时文件已删除: {}", file.getName());
            } catch (IOException e) {
                logger.warn("删除临时文件失败: {}", e.getMessage());
            }
        }
    }
}

