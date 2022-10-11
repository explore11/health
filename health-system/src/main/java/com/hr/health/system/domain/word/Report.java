package com.hr.health.system.domain.word;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Report {
    String userName;
    String sex;
    String nation;
    String totalScore;
    String selfJudge;
    String header;
    String image;
    List<Score> scoreList;
    Map<String, Picture> pictureMap;
    List<String> picList;
}