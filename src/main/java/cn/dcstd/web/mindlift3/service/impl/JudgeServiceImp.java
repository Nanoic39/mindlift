package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.*;
import cn.dcstd.web.mindlift3.mapper.JudgeMapper;
import cn.dcstd.web.mindlift3.service.JudgeService;
import cn.dcstd.web.mindlift3.utils.FileUtil;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author NaNo1c
 */
@Service
public class JudgeServiceImp implements JudgeService {
    @Resource
    private JudgeMapper judgeMapper;

    @Resource
    private FileUtil fileUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<JudgeVO> getJudgeList() {
        List<JudgeVO> judgeVOList = new ArrayList<>();
        List<JudgeLibDO> judgeLibDOList = judgeMapper.getJudgeList();
        for (JudgeLibDO judgeLibDO : judgeLibDOList) {
            System.out.println(judgeLibDO);
            JudgeVO judgeVO = new JudgeVO();
            judgeVO.setId(judgeLibDO.getId());
            judgeVO.setTitle(judgeLibDO.getTitle());
            judgeVO.setIntro(judgeLibDO.getIntro());
            // 处理封面图片，如果coverId为0或文件不存在则设置为null
            if (judgeLibDO.getCoverId() > 0) {
                judgeVO.setCover(fileUtil.getFilePath(judgeLibDO.getCoverId()));
            } else {
                judgeVO.setCover(null);
            }
            judgeVO.setCount(judgeLibDO.getCount());
            judgeVOList.add(judgeVO);
        }
        return judgeVOList;
    }

    @Override
    public JudgeDetailVO getJudgeDetail(int id) {
        JudgeLibDO judgeLibDO = judgeMapper.getJudgeDetail(id);
        if (judgeLibDO == null) {
            return null;
        }
        JudgeDetailVO judgeDetailVO = new JudgeDetailVO();
        judgeDetailVO.setId(judgeLibDO.getId());
        judgeDetailVO.setTitle(judgeLibDO.getTitle());
        judgeDetailVO.setContent(judgeLibDO.getContent());
        // 处理封面图片，如果coverId为0或文件不存在则设置为null
        if (judgeLibDO.getCoverId() > 0) {
            judgeDetailVO.setCover(fileUtil.getFilePath(judgeLibDO.getCoverId()));
        } else {
            judgeDetailVO.setCover(null);
        }
        judgeDetailVO.setCount(judgeLibDO.getCount());
        judgeDetailVO.setRule(judgeLibDO.getRule());
        judgeDetailVO.setSrc(judgeLibDO.getSrc());
        int num = judgeMapper.getJudgeQuestionNumById(id);
        judgeDetailVO.setNum(num);




        return judgeDetailVO;
    }

    @Override
    public List<JudgeQuestionLibVO> getJudgeQuestion(int id) {
        int userId = TokenUtil.getCurrentUser().getId();
        Integer hid = judgeMapper.getLatestJudgeHistoryId(userId, id);
        
        List<JudgeQuestionLibVO> judgeQuestionLibVOList = new ArrayList<>();
        List<JudgeQuestionLibDO> judgeQuestionLibDOList = judgeMapper.getJudgeQuestion(id);
        for (JudgeQuestionLibDO judgeQuestionLibDO : judgeQuestionLibDOList) {
            JudgeQuestionLibVO judgeQuestionLibVO = new JudgeQuestionLibVO();
            judgeQuestionLibVO.setId(judgeQuestionLibDO.getId());
            judgeQuestionLibVO.setMainId(judgeQuestionLibDO.getMainId());
            judgeQuestionLibVO.setQuestion(judgeQuestionLibDO.getQuestion());
            judgeQuestionLibVO.setDimension(judgeQuestionLibDO.getDimension());
            judgeQuestionLibVO.setOptions(judgeQuestionLibDO.getOptions());
            if (hid != null) {
                judgeQuestionLibVO.setHistoryId(hid);
            }
            judgeQuestionLibVOList.add(judgeQuestionLibVO);
        }
        return judgeQuestionLibVOList;
    }

    @Override
    public void addHistory(int id) {
        judgeMapper.addCount(id);
        judgeMapper.addHistory(TokenUtil.getCurrentUser().getId(), id);
    }

    @Override
    public void updateHistory(int hid, String progress) {
        judgeMapper.updateHistory(hid, progress);
    }

    @Override
    public List<JudgeHistoryVO> getJudgeHistory() {
        List<JudgeHistoryVO> judgeHistoryVOList = new ArrayList<>();
        List<JudgeHistoryDO> judgeHistoryDOList = judgeMapper.getJudgeHistory(TokenUtil.getCurrentUser().getId());
        for (JudgeHistoryDO judgeHistoryDO : judgeHistoryDOList) {
            JudgeHistoryVO judgeHistoryVO = new JudgeHistoryVO();
            judgeHistoryVO.setId(judgeHistoryDO.getId());
            judgeHistoryVO.setJudgeId(judgeHistoryDO.getJudgeId());
            judgeHistoryVO.setProgress(judgeHistoryDO.getProgress());
            JudgeLibDO judgeDetail = judgeMapper.getJudgeDetail(judgeHistoryDO.getJudgeId());
            if (judgeDetail == null) {
                // 如果测评记录已被删除，跳过此条历史记录
                continue;
            }
            judgeHistoryVO.setTitle(judgeDetail.getTitle());
            judgeHistoryVO.setIntro(judgeDetail.getIntro());
            // 处理封面图片，如果coverId为0或文件不存在则设置为null
            if (judgeDetail.getCoverId() > 0) {
                judgeHistoryVO.setCover(fileUtil.getFilePath(judgeDetail.getCoverId()));
            } else {
                judgeHistoryVO.setCover(null);
            }
            judgeHistoryVO.setNum(judgeMapper.getJudgeQuestionNumById(judgeHistoryDO.getJudgeId()));
            judgeHistoryVOList.add(judgeHistoryVO);


        }

        return judgeHistoryVOList;
    }

    @Override
    public JudgeResultVO submitJudgeAnswers(JudgeSubmitDTO submitDTO) {
        int userId = TokenUtil.getCurrentUser().getId();
        
        // 获取所有题目
        List<JudgeQuestionLibDO> questions = judgeMapper.getJudgeQuestion(submitDTO.getJudgeId());
        
        // 获取测评信息
        JudgeLibDO judgeLib = judgeMapper.getJudgeDetail(submitDTO.getJudgeId());
        String judgeTitle = judgeLib != null ? judgeLib.getTitle() : "心理测评";
        
        // 计算总分和各维度分数（前端answers的key是顺序索引，从1开始）
        int totalScore = 0;
        int totalMaxScore = 0;
        Map<String, Integer> dimensionScores = new HashMap<>();
        Map<String, Integer> dimensionMaxScores = new HashMap<>();
        
        for (int i = 0; i < questions.size(); i++) {
            JudgeQuestionLibDO question = questions.get(i);
            // 前端传递的是顺序索引（1, 2, 3...），而不是数据库ID
            Integer answer = submitDTO.getAnswers().get(i + 1);
            
            if (answer != null) {
                totalScore += answer;
                
                // 按维度累计分数
                String dimension = question.getDimension();
                dimensionScores.put(dimension, dimensionScores.getOrDefault(dimension, 0) + answer);
                
                // 计算该题最大分值
                try {
                    List<Map<String, Object>> options = objectMapper.readValue(
                        question.getOptions(), 
                        new TypeReference<List<Map<String, Object>>>() {}
                    );
                    int maxValue = options.stream()
                        .mapToInt(opt -> (Integer) opt.get("value"))
                        .max()
                        .orElse(2);
                    totalMaxScore += maxValue;
                    dimensionMaxScores.put(dimension, dimensionMaxScores.getOrDefault(dimension, 0) + maxValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        // 计算整体水平
        double overallPercentage = totalMaxScore > 0 ? (double) totalScore / totalMaxScore : 0;
        String overallLevel;
        String overallDescription;
        
        if (overallPercentage < 0.33) {
            overallLevel = "良好";
            overallDescription = "在面对各种生活情境和压力时，能够保持较为稳定的心理状态，表现出良好的适应能力";
        } else if (overallPercentage < 0.67) {
            overallLevel = "一般";
            overallDescription = "在面对压力和挑战时，大多数情况下能够应对自如，但在某些特定情境下可能会感到不适";
        } else {
            overallLevel = "需要关注";
            overallDescription = "在日常生活中可能经常感到压力或情绪困扰，在某些方面需要更多的自我调节和关注";
        }
        
        // 生成完整分析报告
        StringBuilder analysisBuilder = new StringBuilder();
        
        // 1. 整体分析
        analysisBuilder.append("【测评整体分析】\n");
        analysisBuilder.append(String.format("经过对测验题目的综合分析与计分，该受测者的%s处于%s水平。",
            getJudgeMainDimension(judgeTitle), overallLevel));
        analysisBuilder.append(String.format("在满分%d分的情况下，得分%d分。", totalMaxScore, totalScore));
        analysisBuilder.append(String.format("这表明受测者%s，", overallDescription));
        analysisBuilder.append("但仍存在部分需要关注和改善的情况。\n\n");

        String totalJudgeResult = String.valueOf(analysisBuilder).replaceAll("【测评整体分析】", "").replaceAll("\n", "");
        
        // 2. 各维度分析（确保所有7个维度都有解释）
        analysisBuilder.append("【各维度详细分析】\n\n");
        Map<String, JudgeResultVO.DimensionScore> dimensionAnalysis = new HashMap<>();
        
        String[] allDimensions = {"冲动性", "罪恶感", "疑心病", "焦虑", "抑郁", "自主", "自卑"};
        for (String dimension : allDimensions) {

            System.out.println(dimension);
            JudgeResultVO.DimensionScore dimScore = new JudgeResultVO.DimensionScore();
            
            if (dimensionScores.containsKey(dimension)) {

                // 该维度在本次测评中有题目
                int score = dimensionScores.get(dimension);
                int maxScore = dimensionMaxScores.getOrDefault(dimension, 1);
                dimScore.setScore(score);
                dimScore.setMaxScore(maxScore);
                
                double percentage = (double) score / maxScore;
                String level;
                
                if (percentage < 0.33) {
                    level = "低";
                } else if (percentage < 0.67) {
                    level = "中";
                } else {
                    level = "高";
                }
                
                dimScore.setLevel(level);
                String description = getDimensionDescription(dimension, level, score, maxScore);
                dimScore.setDescription(description);
                
                analysisBuilder.append(String.format("【%s】：%s水平（得分：%d/%d）\n", 
                    dimension, level, score, maxScore));
                analysisBuilder.append(description).append("\n\n");
            } else {
                // 该维度在本次测评中未涉及，但仍需说明
                dimScore.setScore(0);
                dimScore.setMaxScore(0);
                dimScore.setLevel("未测评");
                dimScore.setDescription("本次测评未包含此维度的题目。");
                
                analysisBuilder.append(String.format("【%s】：未测评\n", dimension));
                analysisBuilder.append("本次测评未包含此维度的题目，如需了解该维度情况，建议参加其他相关测评。\n\n");
            }
            
            dimensionAnalysis.put(dimension, dimScore);
        }

        // 总体评价

        // 3. 综合建议
        analysisBuilder.append("【综合建议】\n");
        analysisBuilder.append(getComprehensiveAdvice(dimensionScores, dimensionMaxScores, overallLevel));

        String judgeResultSuggest = getComprehensiveAdvice(dimensionScores, dimensionMaxScores, overallLevel);

        // 保存结果到数据库
        JudgeResultDO resultDO = new JudgeResultDO();
        resultDO.setUserId(userId);
        resultDO.setJudgeId(submitDTO.getJudgeId());
        resultDO.setTotalScore(totalScore);
        
        try {
            resultDO.setDimensionScores(objectMapper.writeValueAsString(dimensionScores));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        resultDO.setAnalysis(analysisBuilder.toString());
        judgeMapper.insertJudgeResult(resultDO);
        
        // 更新测评历史
        try {
            String answersJson = objectMapper.writeValueAsString(submitDTO.getAnswers());
            judgeMapper.updateJudgeHistoryWithResult(submitDTO.getHistoryId(), answersJson, resultDO.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 构造返回对象
        return buildJudgeResultVO(resultDO, dimensionAnalysis, totalJudgeResult, judgeResultSuggest);
    }

    @Override
    public JudgeResultVO getJudgeResult(int resultId) {
        JudgeResultDO resultDO = judgeMapper.getJudgeResultById(resultId);
        if (resultDO == null) {
            return null;
        }

        System.out.println();

        // 解析维度分数
        Map<String, JudgeResultVO.DimensionScore> dimensionAnalysis = new HashMap<>();

        String totalJudgeResult = resultDO.getAnalysis().split("【各维度详细分析】")[0].replaceAll("【测评整体分析】", "").replaceAll("\n", "");
        String judgeResultSuggest = "【综合建议】" + resultDO.getAnalysis().split("【综合建议】")[1].replaceAll("\n", "");

        try {
            Map<String, Integer> dimensionScores = objectMapper.readValue(
                resultDO.getDimensionScores(), 
                new TypeReference<Map<String, Integer>>() {}
            );
            
            for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
                JudgeResultVO.DimensionScore dimScore = new JudgeResultVO.DimensionScore();
                dimScore.setScore(entry.getValue());
                // 注意：这里无法获取maxScore，需要重新计算或存储
                dimensionAnalysis.put(entry.getKey(), dimScore);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return buildJudgeResultVO(resultDO, dimensionAnalysis, totalJudgeResult, judgeResultSuggest);
    }

    @Override
    public List<JudgeResultVO> getJudgeResultHistory(int judgeId) {
        int userId = TokenUtil.getCurrentUser().getId();
        List<JudgeResultDO> resultDOList = judgeMapper.getJudgeResultsByUserAndJudge(userId, judgeId);
        
        List<JudgeResultVO> resultVOList = new ArrayList<>();
        for (JudgeResultDO resultDO : resultDOList) {
            Map<String, JudgeResultVO.DimensionScore> dimensionAnalysis = new HashMap<>();
            String totalJudgeResult = resultDO.getAnalysis().split("【各维度详细分析】")[0].replaceAll("【测评整体分析】", "").replaceAll("\n", "");
            String judgeResultSuggest = "【综合建议】" + resultDO.getAnalysis().split("【综合建议】")[1].replaceAll("\n", "");
            try {
                Map<String, Integer> dimensionScores = objectMapper.readValue(
                    resultDO.getDimensionScores(), 
                    new TypeReference<Map<String, Integer>>() {}
                );
                
                for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
                    JudgeResultVO.DimensionScore dimScore = new JudgeResultVO.DimensionScore();
                    dimScore.setScore(entry.getValue());
                    dimensionAnalysis.put(entry.getKey(), dimScore);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            resultVOList.add(buildJudgeResultVO(resultDO, dimensionAnalysis, totalJudgeResult, judgeResultSuggest));
        }
        
        return resultVOList;
    }

    private JudgeResultVO buildJudgeResultVO(JudgeResultDO resultDO, Map<String, JudgeResultVO.DimensionScore> dimensionAnalysis, String totalJudgeResult, String judgeResultSuggest) {
        JudgeResultVO resultVO = new JudgeResultVO();
        resultVO.setId(resultDO.getId());
        resultVO.setJudgeId(resultDO.getJudgeId());
        
        JudgeLibDO judgeLib = judgeMapper.getJudgeDetail(resultDO.getJudgeId());
        if (judgeLib != null) {
            resultVO.setJudgeTitle(judgeLib.getTitle());
        } else {
            // 如果测评记录已被删除，使用默认标题
            resultVO.setJudgeTitle("测评已删除 (ID: " + resultDO.getJudgeId() + ")");
        }
        
        resultVO.setTotalScore(resultDO.getTotalScore());
        resultVO.setDimensionScores(dimensionAnalysis);
        resultVO.setAnalysis(resultDO.getAnalysis());

        resultVO.setTotalJudgeResult(totalJudgeResult);
        resultVO.setJudgeResultSuggest(judgeResultSuggest);
        
        if (resultDO.getCreateTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resultVO.setCreateTime(sdf.format(resultDO.getCreateTime()));
        }
        
        return resultVO;
    }

    /**
     * 根据测评标题获取主要评估的心理维度名称
     */
    private String getJudgeMainDimension(String judgeTitle) {
        if (judgeTitle.contains("情绪稳定")) {
            return "情绪稳定性";
        } else if (judgeTitle.contains("抑郁")) {
            return "抑郁程度";
        } else if (judgeTitle.contains("焦虑")) {
            return "焦虑水平";
        } else if (judgeTitle.contains("自尊")) {
            return "自尊水平";
        } else if (judgeTitle.contains("冲动")) {
            return "冲动控制";
        } else if (judgeTitle.contains("罪恶感")) {
            return "罪恶感水平";
        } else if (judgeTitle.contains("疑心")) {
            return "多疑倾向";
        } else if (judgeTitle.contains("自主")) {
            return "自主性水平";
        } else if (judgeTitle.contains("心理健康")) {
            return "心理健康状况";
        } else if (judgeTitle.contains("情绪调节")) {
            return "情绪调节能力";
        } else {
            return "心理状态";
        }
    }
    
    /**
     * 根据各维度得分生成综合建议
     */
    private String getComprehensiveAdvice(Map<String, Integer> dimensionScores, 
                                         Map<String, Integer> dimensionMaxScores,
                                         String overallLevel) {
        StringBuilder advice = new StringBuilder();
        
        // 统计高分维度
        List<String> highDimensions = new ArrayList<>();
        List<String> mediumDimensions = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
            String dimension = entry.getKey();
            int score = entry.getValue();
            int maxScore = dimensionMaxScores.getOrDefault(dimension, 1);
            double percentage = (double) score / maxScore;
            
            if (percentage >= 0.67) {
                highDimensions.add(dimension);
            } else if (percentage >= 0.33) {
                mediumDimensions.add(dimension);
            }
        }
        
        // 根据整体水平给出总体建议
        if ("良好".equals(overallLevel)) {
            advice.append("您的整体心理状态良好，表现出较强的心理韧性和适应能力。建议您：\n");
            advice.append("1. 继续保持良好的生活习惯和积极的心态；\n");
            advice.append("2. 在日常生活中注意劳逸结合，保持身心健康；\n");
            advice.append("3. 可以尝试帮助身边有需要的朋友，分享您的积极经验。\n\n");
        } else if ("一般".equals(overallLevel)) {
            advice.append("您的心理状态整体尚可，但在某些方面还有改善空间。建议您：\n");
            advice.append("1. 关注自己的情绪变化，学习适当的压力管理技巧；\n");
            advice.append("2. 保持规律作息，适度运动，这些有助于改善心理状态；\n");
            advice.append("3. 培养一些兴趣爱好，丰富生活内容，提升生活满意度。\n\n");
        } else {
            advice.append("您的心理状态需要更多的关注和调整。建议您：\n");
            advice.append("1. 重视自己的心理健康，必要时寻求专业心理咨询帮助；\n");
            advice.append("2. 建立良好的社会支持系统，多与信任的朋友家人交流；\n");
            advice.append("3. 学习情绪管理和压力应对技巧，如冥想、深呼吸等放松方法。\n\n");
        }
        
        // 针对高分维度的具体建议
        if (!highDimensions.isEmpty()) {
            advice.append("特别需要关注的方面：\n");
            for (String dimension : highDimensions) {
                switch (dimension) {
                    case "冲动性":
                        advice.append("• 冲动性较高：建议在做重要决定前给自己设定'冷静期'，培养三思而后行的习惯。\n");
                        break;
                    case "罪恶感":
                        advice.append("• 罪恶感较强：学习自我接纳和宽恕，理解犯错是成长的一部分，不必过度自责。\n");
                        break;
                    case "疑心病":
                        advice.append("• 多疑倾向明显：尝试以更开放的心态与人交往，给予他人适当的信任，改善人际关系。\n");
                        break;
                    case "焦虑":
                        advice.append("• 焦虑水平较高：学习放松技巧（如渐进式肌肉放松、正念冥想），必要时寻求专业帮助。\n");
                        break;
                    case "抑郁":
                        advice.append("• 抑郁倾向明显：增加户外活动和社交互动，保持规律作息。若持续低落，请及时寻求专业帮助。\n");
                        break;
                    case "自卑":
                        advice.append("• 自信心不足：关注并记录自己的优点和成就，多给自己积极的心理暗示，逐步建立自信。\n");
                        break;
                    case "自主":
                        advice.append("• 自主性较强：这是优势，但也要注意在团队合作时适当倾听他人意见，保持开放心态。\n");
                        break;
                }
            }
            advice.append("\n");
        }
        
        // 日常维护建议
        advice.append("日常心理健康维护建议：\n");
        advice.append("• 保持规律的作息时间，确保充足睡眠；\n");
        advice.append("• 每周进行3-5次30分钟以上的有氧运动；\n");
        advice.append("• 培养至少一项兴趣爱好，让生活更加充实；\n");
        advice.append("• 定期与朋友家人保持联系，维护良好的社会支持网络；\n");
        advice.append("• 学习并实践压力管理技巧，如冥想、正念、深呼吸等；\n");
        advice.append("• 如果心理困扰持续影响生活，请及时寻求专业心理咨询服务。\n");
        
        return advice.toString();
    }
    
    private String getDimensionDescription(String dimension, String level, int score, int maxScore) {
        Map<String, Map<String, String>> descriptions = new HashMap<>();
        
        // 冲动性
        Map<String, String> impulsivity = new HashMap<>();
        impulsivity.put("低", "您的冲动控制能力较好，能够在行动前进行理性思考。在面对诱惑或压力时，您通常能够保持冷静，做出深思熟虑的决定。这种特质有助于您避免因一时冲动而做出后悔的决定。");
        impulsivity.put("中", "您在冲动控制方面表现正常，大多数情况下能够理性行事，但偶尔也会有冲动的时候。建议在重要决策前给自己留出思考时间，避免在情绪激动时做决定。");
        impulsivity.put("高", "您可能容易出现冲动行为，在面对诱惑或压力时，往往会先行动后思考。建议培养延迟满足的能力，在做重要决定前先冷静思考，可以尝试深呼吸或数到十的方法来缓解冲动。");
        descriptions.put("冲动性", impulsivity);
        
        // 罪恶感
        Map<String, String> guilt = new HashMap<>();
        guilt.put("低", "您较少体验到罪恶感，对自己的行为通常持接纳态度。这可能意味着您有较强的自我接纳能力，但也需要注意是否在应该反思的时候缺乏自我审视。");
        guilt.put("中", "您的罪恶感水平适中，能够在做错事时感到内疚，但不会过度自责。这是一个健康的状态，既有道德感又不会被过度的罪恶感所困扰。");
        guilt.put("高", "您可能经常体验到强烈的罪恶感，即使在一些小事上也容易自责。这可能会影响您的心理健康和自信心。建议学习自我接纳，理解每个人都会犯错，重要的是从中学习而不是过度自责。");
        descriptions.put("罪恶感", guilt);
        
        // 疑心病
        Map<String, String> suspicion = new HashMap<>();
        suspicion.put("低", "您对他人保持基本的信任，不会过度怀疑别人的动机。这种开放的态度有助于建立良好的人际关系，但也要注意在必要时保持适度的警惕。");
        suspicion.put("中", "您在信任他人方面表现适中，既不会盲目相信也不会过度怀疑。这是一个比较平衡的状态，能够根据实际情况灵活调整对他人的信任程度。");
        suspicion.put("高", "您可能对他人持有较强的怀疑态度，容易认为别人有不良动机。这可能会影响您的人际关系和社交生活。建议尝试以更开放的心态看待他人，给予适当的信任，同时也要学会辨别真正需要警惕的情况。");
        descriptions.put("疑心病", suspicion);
        
        // 焦虑
        Map<String, String> anxiety = new HashMap<>();
        anxiety.put("低", "您的焦虑水平较低，面对压力和不确定性时能够保持相对平静。这种特质有助于您在困难情况下保持清晰的思维和良好的表现。");
        anxiety.put("中", "您的焦虑水平在正常范围内，在面对压力时会感到紧张，但不会严重影响日常生活。适度的焦虑其实是一种保护机制，能够提醒您注意潜在的危险。");
        anxiety.put("高", "您可能经常感到焦虑和紧张，这可能会影响您的日常生活和工作表现。建议学习一些放松技巧，如深呼吸、冥想或渐进性肌肉放松。必要时，可以寻求专业心理咨询的帮助。");
        descriptions.put("焦虑", anxiety);
        
        // 抑郁
        Map<String, String> depression = new HashMap<>();
        depression.put("低", "您的情绪状态良好，较少体验到抑郁情绪。您能够享受生活中的乐趣，对未来保持积极的态度。继续保持这种积极的心态。");
        depression.put("中", "您偶尔会感到情绪低落，但整体情绪状态基本稳定。这是正常的情绪波动，建议保持规律的作息、适度运动和良好的社交活动，这些都有助于维持良好的情绪状态。");
        depression.put("高", "您可能经常感到情绪低落、缺乏兴趣或动力。这种状态可能会影响您的生活质量。建议增加户外活动、保持社交联系，必要时寻求专业心理咨询的帮助。如果症状持续或加重，请及时就医。");
        descriptions.put("抑郁", depression);
        
        // 自主
        Map<String, String> autonomy = new HashMap<>();
        autonomy.put("低", "您在决策和行动时可能较多依赖他人的意见和支持。建议培养独立思考的能力，尝试在一些小事上自己做决定，逐步建立自信。");
        autonomy.put("中", "您在自主性方面表现良好，既能够独立做决定，也会在必要时寻求他人的建议。这是一个平衡的状态，既有独立性又不会孤立自己。");
        autonomy.put("高", "您有很强的自主性，喜欢独立思考和做决定。这种特质有助于您发挥个人潜力，但也要注意在需要团队合作时适当考虑他人的意见和建议。");
        descriptions.put("自主", autonomy);
        
        // 自卑
        Map<String, String> inferiority = new HashMap<>();
        inferiority.put("低", "您对自己有较为积极的评价，自信心较强。这种健康的自我认知有助于您在生活和工作中取得更好的表现。继续保持这种积极的自我形象。");
        inferiority.put("中", "您的自我评价比较现实，既能看到自己的优点也能接受自己的不足。这是一个健康的状态，有助于持续的自我成长和发展。");
        inferiority.put("高", "您可能经常对自己持负面评价，缺乏自信。这种自卑感可能会限制您的潜力发挥。建议多关注自己的优点和成就，学会自我肯定。可以尝试写下每天的小成就，逐步建立积极的自我形象。");
        descriptions.put("自卑", inferiority);
        
        Map<String, String> dimensionDesc = descriptions.getOrDefault(dimension, new HashMap<>());
        return dimensionDesc.getOrDefault(level, 
            String.format("您在%s维度的得分为%d/%d，处于%s水平。", dimension, score, maxScore, level));
    }
}
