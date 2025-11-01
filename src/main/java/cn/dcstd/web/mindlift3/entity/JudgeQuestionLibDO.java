package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

@Data
public class JudgeQuestionLibDO {
    private int id;
    private int mainId;
    private String question;
    private String dimension;
    private String options;
}
