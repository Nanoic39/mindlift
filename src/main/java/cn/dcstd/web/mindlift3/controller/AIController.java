package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.AIStarAO;
import cn.dcstd.web.mindlift3.service.AIService;
import cn.dcstd.web.mindlift3.service.DiaryService;
import cn.dcstd.web.mindlift3.service.RedisService;
import cn.dcstd.web.mindlift3.service.VoiceRecognitionService;
import cn.dcstd.web.mindlift3.utils.BertUtil;
import cn.dcstd.web.mindlift3.utils.RESUtil;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    @Resource
    private AIService aiService;

    @Resource
    private DiaryService diaryService;

    @Resource
    private BertUtil bertUtil;

    @Resource
    private RESUtil resUtil;

    @Resource
    private RedisService redisService;

    @Resource
    private VoiceRecognitionService voiceRecognitionService;

    /**
     * 获取AI的回答
     * @param aiStarAO 请求体
     * @return
     */
    @PostMapping("/answer")
    public Result answer(@RequestBody AIStarAO aiStarAO) throws IOException {
        //敏感词检测

        //获取最近一次心情日记(会有很申金的BUG，暂时搁置)

        String nowEmotion;

        // 懒得修了，全注释了
        try {
            // 根据文本推断心情(记得开BERT模型)
            // String _textEmotion = bertUtil.getBertResponse(aiStarAO.getContent());
            // 获取图像识别到的心情(记得开RES模型)
            //String _imageEmotion = resUtil.getRESResponse("uid-" + TokenUtil.getCurrentUser().getId());

            //System.out.println(String.format("%s , %s", _textEmotion, _imageEmotion));

            // 混合权重计算可能的心情 ： 0.5 | 0.5
            // 模型没给数据，这里就假装计算了
            //nowEmotion = _textEmotion;
        } catch (Exception e) {
            System.out.println(e);
            nowEmotion = "未知";
        }

        String param = String.format("(如果括号外的内容表示我当前心情较差则给出合适的安慰，如果括号外的内容表示我当前心情较好则给出合适的夸奖，如果括号外的内容表示我当前困惑则给出合适的建议，如果括号外的内容表示我当前愤怒则安抚我的情绪，如果括号外的内容表示我当前害怕则给我鼓励。输出内容时请不要提到任何该括号内的信息。)", "");

        ResponseEntity responseEntity = aiService.getAnswer(aiStarAO, param);
        return Result.success(JSON.parseObject(Objects.requireNonNull(responseEntity.getBody()).toString()));
    }

    @PostMapping("/get-face")
    public Result getFace(@RequestPart MultipartFile file) throws IOException {
        String uid = String.valueOf(TokenUtil.getCurrentUser().getId());
        String faceStatus = aiService.getFaceStatus(file);
        System.out.println("TEST:" + faceStatus);
        redisService.set("uid-" + uid, faceStatus, 60 * 60);
        return Result.success();
    }

    /**
     * 语音转文本接口（同步版本）
     * 使用预加载的Vosk模型，性能更好
     * 
     * @param file WAV格式音频文件
     * @return 识别结果
     */
    @PostMapping("/video2text")
    public Result video2text(@RequestPart MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("接收到空文件");
            return Result.fail("文件不能为空");
        }

        logger.info("接收到语音识别请求 - 文件: {}, 大小: {} bytes", 
                    file.getOriginalFilename(), file.getSize());

        try {
            // 使用优化后的语音识别服务（模型已预加载）
            String result = voiceRecognitionService.recognizeAudio(file);
            
            logger.info("语音识别成功完成");
            return Result.success(result);
            
        } catch (IOException e) {
            logger.error("语音识别失败: {}", e.getMessage(), e);
            return Result.fail("语音识别失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("语音识别过程中发生未知错误: {}", e.getMessage(), e);
            return Result.fail("服务器内部错误: " + e.getMessage());
        }
    }

    /**
     * 语音转文本接口（异步版本）
     * 适用于处理大文件，不阻塞主线程
     * 
     * @param file WAV格式音频文件
     * @return 任务提交成功消息
     */
    @PostMapping("/video2text/async")
    public Result video2textAsync(@RequestPart MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("接收到空文件");
            return Result.fail("文件不能为空");
        }

        logger.info("接收到异步语音识别请求 - 文件: {}, 大小: {} bytes", 
                    file.getOriginalFilename(), file.getSize());

        try {
            // 提交异步任务
            voiceRecognitionService.recognizeAudioAsync(file)
                .thenAccept(result -> {
                    logger.info("异步语音识别完成: {}", result);
                    // 这里可以通过WebSocket或其他方式通知客户端结果
                })
                .exceptionally(ex -> {
                    logger.error("异步语音识别失败: {}", ex.getMessage(), ex);
                    return null;
                });
            
            return Result.success("语音识别任务已提交，正在处理中...");
            
        } catch (Exception e) {
            logger.error("提交异步语音识别任务失败: {}", e.getMessage(), e);
            return Result.fail("任务提交失败: " + e.getMessage());
        }
    }






}
