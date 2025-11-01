package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.AIStarAO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface AIService {

    ResponseEntity getAnswer(AIStarAO aiStarAO, String param);

    String getFaceStatus(MultipartFile file) throws IOException;
}
