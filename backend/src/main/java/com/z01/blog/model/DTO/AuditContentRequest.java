package com.z01.blog.model.DTO;

import com.z01.blog.model.Audit.AuditAction;
import com.z01.blog.model.Report.MaterialRef;

public class AuditContentRequest {
    public MaterialRef material;
    public AuditAction action;
}
