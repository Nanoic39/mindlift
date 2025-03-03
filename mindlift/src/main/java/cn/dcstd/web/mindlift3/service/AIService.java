package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.AIStarAO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AIService {

    ResponseEntity getAnswer(AIStarAO aiStarAO, String param);
}
