package com.z01.blog.model.Report;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Table(name = "reports")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ReportModel {
    @Id
    public long id;
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::report_reason")
    public ReportReason reason;
    public String description;
    public LocalDateTime createdAt;
    @Embedded
    public ReportedBy reportedBy;
    @Embedded
    public ResolvedBy resolvedBy;
    public String actionTaken;

    public abstract MaterialRef getMaterial();

}
