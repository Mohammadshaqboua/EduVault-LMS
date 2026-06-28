package com.example.eduvaultlms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor@NoArgsConstructor
@Builder
public class QuizResponse {
    private UUID                   id;
    private String                 title;
    private UUID                   courseId;
    private int                    passMark;
    private int                    timeLimitMin;
    private List<QuestionResponse> questions;
}