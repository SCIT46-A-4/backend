package com.scit.iLog.exception;

public class AnalysisResultNotFoundException extends RuntimeException {
    public AnalysisResultNotFoundException(Long analysisResultId) {
        super(analysisResultId.toString());
    }
}
