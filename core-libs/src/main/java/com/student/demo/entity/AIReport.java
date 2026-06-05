package com.student.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_reports")
public class AIReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String bugs;

    @Column(columnDefinition = "TEXT")
    private String optimization;

    private String timeComplexity;

    @Column(columnDefinition = "TEXT")
    private String codeSmells;

    @Column(columnDefinition = "TEXT")
    private String refactoredCode;

    @Column(columnDefinition = "TEXT")
    private String unitTests;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "code_file_id")
    private CodeFile codeFile;

    public AIReport() {
    }

    public Long getId() {
        return id;
    }

    public String getBugs() {
        return bugs;
    }

    public void setBugs(String bugs) {
        this.bugs = bugs;
    }

    public String getOptimization() {
        return optimization;
    }

    public void setOptimization(String optimization) {
        this.optimization = optimization;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getCodeSmells() {
        return codeSmells;
    }

    public void setCodeSmells(String codeSmells) {
        this.codeSmells = codeSmells;
    }

    public String getRefactoredCode() {
        return refactoredCode;
    }

    public void setRefactoredCode(String refactoredCode) {
        this.refactoredCode = refactoredCode;
    }

    public String getUnitTests() {
        return unitTests;
    }

    public void setUnitTests(String unitTests) {
        this.unitTests = unitTests;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public CodeFile getCodeFile() {
        return codeFile;
    }

    public void setCodeFile(CodeFile codeFile) {
        this.codeFile = codeFile;
    }
}