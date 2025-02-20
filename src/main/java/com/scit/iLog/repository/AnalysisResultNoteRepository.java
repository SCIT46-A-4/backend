package com.scit.iLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultNoteEntity;

public interface AnalysisResultNoteRepository extends JpaRepository<AnalysisResultNoteEntity, Long> {

}
