package com.scit.iLog.exception;

public class AnalysisResultNotFoundException extends RuntimeException {
    private final Long childId;
    private final Long analysisResultId;

    public AnalysisResultNotFoundException(Long childId, Long analysisResultId) {
        super(String.format("childId: %d, analysisResultId: %d", childId, analysisResultId));
        this.childId = childId;
        this.analysisResultId = analysisResultId;
    }

    public Long childId() {
        return childId;
    }
}
