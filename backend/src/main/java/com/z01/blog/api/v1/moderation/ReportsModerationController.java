package com.z01.blog.api.v1.moderation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("{reportId}/post")
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    Optional<ReportModel> getReportedPost(@PathVariable long reportId) {
        return reportRepo.findById(reportId);
    }

    @GetMapping("{reportId}/comments")
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    Optional<ReportModel> getReportedPostComments(@PathVariable long reportId) {
        return reportRepo.findById(reportId);
    }

    @GetMapping("{reportId}/user")
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    Optional<ReportModel> getReportedUser(@PathVariable long reportId) {
        return reportRepo.findById(reportId);
    }
}
