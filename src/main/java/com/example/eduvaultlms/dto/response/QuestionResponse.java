package com.example.eduvaultlms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionResponse {
    private UUID         id;
    private String       text;
    private List<String> options;
    private int          points;
}
