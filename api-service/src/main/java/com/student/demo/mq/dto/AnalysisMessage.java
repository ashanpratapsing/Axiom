package com.student.demo.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisMessage {

    private Long fileId;

    private Long userId;

    private String language;
}