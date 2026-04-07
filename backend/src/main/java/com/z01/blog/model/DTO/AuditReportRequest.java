package com.z01.blog.model.DTO;

import com.z01.blog.model.Audit.AuditAction;

public record AuditReportRequest(long id, AuditAction action) {
}