package com.z01.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.model.Audit.AuditData;
import com.z01.blog.model.Comment.CommentRepo;
import com.z01.blog.model.Post.PostRepo;
import com.z01.blog.model.Report.ReportModel;
import com.z01.blog.model.Report.ReportRepository;
import com.z01.blog.model.Report.ResolvedBy;
import com.z01.blog.model.User.UserRepo;

@Service
public class AuditService {
    @Autowired
    ReportRepository reportRepo;

    @Autowired
    CommentRepo commentRepo;
    @Autowired
    PostRepo postRepo;
    @Autowired
    UserRepo userRepo;

    public void audit(AuditData request, long userId) {
        var report = reportRepo.findById(request.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (report.resolvedBy != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Report already resolved");

        switch (request.action()) {
            case BAN_USER -> banUser(report);
            case DELETE_CONTENT -> deleteContent(report);
            case IGNORE -> {
            }
        }

        report.resolvedBy = new ResolvedBy();
        report.resolvedBy.id = userId;
        reportRepo.save(report);
    }

    private void deleteContent(ReportModel report) {
        switch (report) {
            case ReportModel.Comment rc -> {
                var comment = commentRepo.findById(rc.commentId).get();
                comment.deleted = true;
                commentRepo.save(comment);
            }
            case ReportModel.Post rp -> {
                var post = postRepo.findById(rp.postId).get();
                post.deleted = true;
                postRepo.save(post);
            }
            default -> throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "material doesn't support delete action");
        }
    }

    private void banUser(ReportModel report) {
        switch (report) {
            case ReportModel.Comment rc -> {
                var comment = commentRepo.findById(rc.commentId).get();
                var user = userRepo.findById(comment.account).get();
                user.banned = true;
                userRepo.save(user);
            }
            case ReportModel.Post rp -> {
                var post = postRepo.findById(rp.postId).get();
                var user = userRepo.findById(post.account).get();
                user.banned = true;
                userRepo.save(user);
            }
            case ReportModel.User up -> {
                var user = userRepo.findById(up.userId).get();
                user.banned = true;
                userRepo.save(user);
            }
            default -> throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "material doesn't support ban action");
        }
    }

}