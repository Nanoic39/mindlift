package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.AIStarAO;
import cn.dcstd.web.mindlift3.entity.DiaryHistoryDO;
import cn.dcstd.web.mindlift3.service.AIService;
import cn.dcstd.web.mindlift3.service.DiaryService;
import cn.dcstd.web.mindlift3.utils.BertUtil;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;


/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    @jakarta.annotation.Resource
    private AIService aiService;

    @jakarta.annotation.Resource
    private DiaryService diaryService;

    @jakarta.annotation.Resource
    private BertUtil bertUtil;

    /**
     * 获取AI的回答
     * @param aiStarAO 请求体
     * @return
     */
    @PostMapping("/answer")
    public Result answer(@RequestBody AIStarAO aiStarAO) throws IOException {

        //获取最近一次心情日记(会有很申金的BUG，暂时搁置)
        /*
        String lastEmotion;
        String nowEmotion;
        try {
            DiaryHistoryDO lastDiaryDO = diaryService.getLastDiary(TokenUtil.getCurrentUser().getId());
            lastEmotion = bertUtil.getBertResponse(lastDiaryDO.getContent());
            nowEmotion = bertUtil.getBertResponse(aiStarAO.getContent());
        } catch (Exception e) {
            lastEmotion = "未知";
            nowEmotion = "未知";
        }*/

        /*String param = String.format("【最近的情绪可能是：%s;当前情绪可能是：%s】",lastEmotion, nowEmotion);
        System.out.println(param);*/
        String param = "";

        ResponseEntity responseEntity = aiService.getAnswer(aiStarAO, param);
        return Result.success(JSON.parseObject(Objects.requireNonNull(responseEntity.getBody()).toString()));
    }

    @PostMapping("/video2text")
    public Result video2text(@RequestPart MultipartFile file) throws IOException {
        System.out.println(file);

        System.out.println(file.getOriginalFilename());
        System.out.println(file.getName());

        try  {
            LibVosk.setLogLevel(LogLevel.DEBUG);
            // 使用 ClassPathResource 加载模型路径
            Resource resource = new ClassPathResource("vosk/vosk-model-small-cn-0.22");
            Path targetPath = resource.getFile().toPath();

            System.out.println(targetPath.toString());
            //该段是模型地址
            Model model = new Model(targetPath.toString());
            // File audioFile = new File("D:\\Others\\Logs\\d357c391-d387-4359-b57e-c0bf8c6853da.wav");
            InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(file.getInputStream()));
            //该段是要转的语言文件，仅支持wav
            Recognizer recognizer = new Recognizer(model, 16000);
            //该段中12000是语言频率，需要大于8000，可以自行调整

            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = ais.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    System.out.println(recognizer.getResult());
                } else {
                    System.out.println(recognizer.getPartialResult());
                }
            }

            return Result.success(recognizer.getFinalResult());
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }




    }






}
