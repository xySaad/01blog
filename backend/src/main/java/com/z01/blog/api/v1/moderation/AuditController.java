package com.z01.blog.api.v1.moderation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.model.DTO.AuditContentRequest;
import com.z01.blog.model.DTO.AuditReportRequest;
import com.z01.blog.services.AuditService;

@RestController
@RequestMapping("/api/v1/moderation/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/report")
    @RequiresPermission(scope = "v1:reports:audit", description = "audit reports by banning users, deleting posts and comments or ignoring the report")
    void auditReport(@RequestBody AuditReportRequest request, @Auth.User long userId) {
        auditService.auditReport(request, userId);
    }

    @PostMapping("/report/{reportId}/ignore")
    @RequiresPermission(scope = "v1:reports:audit", description = "audit reports by banning users, deleting posts and comments or ignoring the report")
    void auditReport(@PathVariable long reportId, @Auth.User long userId) {
        auditService.ignoreReport(reportId, userId);
    }

    @PostMapping("/content")
    @RequiresPermission(scope = "v1:content:audit", description = "audit content by banning users, deleting posts and comments")
    void auditContent(@RequestBody AuditContentRequest request, @Auth.User long userId) {
        auditService.auditMaterial(request.material, request.action);
    }
}