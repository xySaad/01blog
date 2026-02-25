package com.z01.blog.api.v1.moderation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.model.Audit.AuditData;
import com.z01.blog.services.AuditService;

@RestController
@RequestMapping("/api/v1/moderation")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/audit")
    @RequiresPermission(scope = "v1:audit:write", description = "audit reports by banning users, deleting posts and comments or ignoring the report")
    void audit(@RequestBody AuditData request, @Auth.User long userId) {
        auditService.audit(request, userId);
    }
}