package cn.dcstd.web.mindlift3.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.io.IOException;

/**
 * Vosk语音识别模型服务
 * 负责模型的预加载、单例管理和Recognizer创建
 * 
 * @author NaNo1c
 */
@Service
public class VoskModelService {

    private static final Logger logger = LoggerFactory.getLogger(VoskModelService.class);

    @Value("${vosk.model.path:}")
    private String externalModelPath;

    @Value("${vosk.model.internal.path:vosk/vosk-model-small-cn-0.22}")
    private String internalModelPath;

    @Value("${vosk.model.sample.rate:16000}")
    private int sampleRate;

    @Value("${vosk.log.level:WARNINGS}")
    private String logLevel;

    private Model model;
    private volatile boolean modelLoaded = false;
    private final Object modelLock = new Object();

    /**
     * 应用启动时自动加载模型
     */
    @PostConstruct
    public void init() {
        logger.info("正在初始化Vosk模型服务...");
        try {
            // 设置Vosk日志级别
            setVoskLogLevel();
            
            // 预加载模型
            loadModel();
            
            logger.info("Vosk模型服务初始化成功");
        } catch (Exception e) {
            logger.error("Vosk模型服务初始化失败: {}", e.getMessage(), e);
            logger.warn("语音识别功能可能不可用，请检查模型配置");
        }
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void destroy() {
        logger.info("正在关闭Vosk模型服务...");
        if (model != null) {
            try {
                // Vosk模型的清理（如果有的话）
                model = null;
                modelLoaded = false;
                logger.info("Vosk模型已释放");
            } catch (Exception e) {
                logger.error("释放Vosk模型时出错: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 设置Vosk日志级别
     */
    private void setVoskLogLevel() {
        try {
            LogLevel level;
            switch (logLevel.toUpperCase()) {
                case "DEBUG":
                    level = LogLevel.DEBUG;
                    break;
                case "INFO":
                    level = LogLevel.INFO;
                    break;
                case "WARNINGS":
                    level = LogLevel.WARNINGS;
                    break;
                default:
                    level = LogLevel.WARNINGS;
            }
            LibVosk.setLogLevel(level);
            logger.info("Vosk日志级别设置为: {}", logLevel);
        } catch (Exception e) {
            logger.warn("设置Vosk日志级别失败: {}", e.getMessage());
        }
    }

    /**
     * 加载Vosk模型（单例模式）
     */
    private void loadModel() throws IOException {
        if (modelLoaded) {
            logger.debug("模型已加载，跳过重复加载");
            return;
        }

        synchronized (modelLock) {
            if (modelLoaded) {
                return;
            }

            String modelPath = getModelPath();
            logger.info("使用模型路径: {}", modelPath);
            
            long startTime = System.currentTimeMillis();
            model = new Model(modelPath);
            long loadTime = System.currentTimeMillis() - startTime;
            
            modelLoaded = true;
            logger.info("模型加载成功，耗时: {}ms", loadTime);
        }
    }

    /**
     * 获取模型路径（优先使用外部路径）
     */
    private String getModelPath() throws IOException {
        // 方案1: 从配置文件或JVM参数指定的外部路径加载
        if (externalModelPath != null && !externalModelPath.trim().isEmpty()) {
            File externalFile = new File(externalModelPath);
            if (externalFile.exists() && externalFile.isDirectory()) {
                logger.info("使用配置的外部模型路径: {}", externalModelPath);
                return externalFile.getAbsolutePath();
            } else {
                logger.warn("配置的外部模型路径不存在或不是目录: {}", externalModelPath);
            }
        }

        // 方案2: 从JVM系统属性加载
        String systemModelPath = System.getProperty("vosk.model.path");
        if (systemModelPath != null && !systemModelPath.trim().isEmpty()) {
            File systemFile = new File(systemModelPath);
            if (systemFile.exists() && systemFile.isDirectory()) {
                logger.info("使用JVM参数指定的模型路径: {}", systemModelPath);
                return systemFile.getAbsolutePath();
            } else {
                logger.warn("JVM参数指定的模型路径不存在或不是目录: {}", systemModelPath);
            }
        }

        // 方案3: 从resources目录加载（开发环境）
        try {
            org.springframework.core.io.Resource resource = new ClassPathResource(internalModelPath);
            
            if (resource.exists()) {
                if (resource.isFile()) {
                    // 开发环境，直接使用文件路径
                    String path = resource.getFile().getAbsolutePath();
                    logger.info("使用内部模型路径（开发环境）: {}", path);
                    return path;
                } else {
                    // JAR包环境
                    logger.error("检测到JAR包环境，无法直接访问内部模型文件");
                    throw new IOException("JAR包环境下必须使用外部模型路径。请通过以下方式之一指定模型路径:\n" +
                            "1. 在application.yaml中配置: vosk.model.path=/path/to/model\n" +
                            "2. 使用JVM参数: -Dvosk.model.path=/path/to/model");
                }
            }
        } catch (IOException e) {
            logger.error("无法访问内部模型资源: {}", e.getMessage());
        }

        throw new IOException("无法找到Vosk模型文件。请配置模型路径。");
    }

    /**
     * 创建识别器实例
     * 
     * @return Recognizer实例
     * @throws IOException 如果模型未加载
     */
    public Recognizer createRecognizer() throws IOException {
        if (!modelLoaded || model == null) {
            logger.warn("模型未加载，尝试重新加载...");
            loadModel();
        }

        return new Recognizer(model, sampleRate);
    }

    /**
     * 创建识别器实例（指定采样率）
     * 
     * @param sampleRate 采样率
     * @return Recognizer实例
     * @throws IOException 如果模型未加载
     */
    public Recognizer createRecognizer(int sampleRate) throws IOException {
        if (!modelLoaded || model == null) {
            logger.warn("模型未加载，尝试重新加载...");
            loadModel();
        }

        return new Recognizer(model, sampleRate);
    }

    /**
     * 检查模型是否已加载
     * 
     * @return true如果模型已加载
     */
    public boolean isModelLoaded() {
        return modelLoaded;
    }

    /**
     * 获取默认采样率
     * 
     * @return 采样率
     */
    public int getDefaultSampleRate() {
        return sampleRate;
    }

    /**
     * 手动重新加载模型
     * 
     * @throws IOException 如果加载失败
     */
    public void reloadModel() throws IOException {
        logger.info("手动触发模型重新加载...");
        synchronized (modelLock) {
            modelLoaded = false;
            model = null;
            loadModel();
        }
    }
}

