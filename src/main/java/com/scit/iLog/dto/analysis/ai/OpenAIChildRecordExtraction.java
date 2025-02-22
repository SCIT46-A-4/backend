package com.scit.iLog.dto.analysis.ai;

import com.scit.iLog.dto.child.ChildRecordExtraction;

public record OpenAIChildRecordExtraction (
        double height,
        double weight,
        double leftEye,
        double rightEye,
        String diagnosis
) implements ChildRecordExtraction {
        @Override
        public double getHeight() {
                return this.height;
        }

        @Override
        public double getWeight() {
                return this.weight;
        }

        @Override
        public double getLeftEye() {
                return this.leftEye;
        }

        @Override
        public double getRightEye() {
                return this.rightEye;
        }

        @Override
        public String getDiagnosis() {
                return this.diagnosis;
        }
}
