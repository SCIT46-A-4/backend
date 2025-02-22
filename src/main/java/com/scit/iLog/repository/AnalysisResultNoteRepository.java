package com.scit.iLog.repository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultNoteRepository extends JpaRepository<AnalysisResultNoteEntity, Long> {
}
