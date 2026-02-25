package com.z01.blog.api.v1.moderation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.model.Report.ReportModel;
import com.z01.blog.model.Report.ReportRepository;

@RestController
@RequestMapping("/api/v1/moderation/reports")
public class ReportsModerationController {
    @Autowired
    ReportRepository reportRepo;

    @GetMapping
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    List<ReportModel> getAllReports() {
        return reportRepo.findAll();
    }
}
