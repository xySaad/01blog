package com.z01.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.z01.blog.exception.AppError;
import com.z01.blog.model.Report.PostRelatedReport;
import com.z01.blog.model.Report.ReportModel;
import com.z01.blog.model.Report.ReportRepository;
import com.z01.blog.model.Report.UserReport;
import com.z01.blog.model.User.UserExtra;
import com.z01.blog.model.User.UserRepo;

@Service
public class ModerationService {
    @Autowired
    ReportRepository reportRepo;
    @Autowired
    UserRepo userRepo;

    public ReportModel getReport(long reportId) {
        return reportRepo.findById(reportId).orElseThrow(() -> AppError.ENTITY_NOT_FOUND.asException());
    }

    public long getPostIdByReportId(long reportId) {
        var report = this.getReport(reportId);
        if (report instanceof PostRelatedReport pr)
            return pr.getPostId();
        else
            throw AppError.MATERIAL_NOT_DELETEABLE.asException();
    }

    public UserExtra getUserByReportId(long reportId) {
        var report = this.getReport(reportId);

        if (report instanceof UserReport u) {
            var user = userRepo.findExtraById(u.userId).orElseThrow(
                    () -> AppError.USER_PROFILE_NOT_FOUND.asException());
            return user;
        } else
            throw AppError.REPORT_NOT_USER_RELATED.asException();
    }
}
