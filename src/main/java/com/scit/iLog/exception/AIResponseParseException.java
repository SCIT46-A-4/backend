package com.scit.iLog.exception;

import lombok.Getter;

@Getter
public class AIResponseParseException extends RuntimeException {
    private Long analysisTargetId;

    public AIResponseParseException(String message) {
        super(message);
    }

    public AIResponseParseException(Long analysisTargetId, String message) {
        super(message);
        this.analysisTargetId = analysisTargetId;
    }
}
