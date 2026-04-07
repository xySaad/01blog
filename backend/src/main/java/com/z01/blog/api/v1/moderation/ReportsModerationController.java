package com.z01.blog.api.v1.moderation;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.Comment.CommentExtra;
import com.z01.blog.model.Comment.CommentRepo;
import com.z01.blog.model.Post.PostExtra;
import com.z01.blog.model.Post.PostRepo;
import com.z01.blog.model.Report.CommentReport;
import com.z01.blog.model.Report.ReportModel;
import com.z01.blog.model.Report.ReportRepository;
import com.z01.blog.model.Report.UserReport;
import com.z01.blog.model.User.UserExtra;
import com.z01.blog.services.ModerationService;

@RestController
@RequestMapping("/api/v1/moderation/reports")
public class ReportsModerationController {
    @Autowired
    ReportRepository reportRepo;
    @Autowired
    ModerationService moderationService;

    @Autowired
    PostRepo postRepo;
    @Autowired
    CommentRepo commentRepo;

    @GetMapping
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    List<ReportModel> getAllReports() {
        return reportRepo.findAll();
    }

    @GetMapping("{reportId}/post")
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    PostExtra getReportedPost(@PathVariable long reportId) {
        long postId = moderationService.getPostIdByReportId(reportId);
        return postRepo.findExtraById(postId);
    }

    @GetMapping("{reportId}/comments")
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    List<CommentExtra> getReportedCommentList(@PathVariable long reportId) {
        long postId = moderationService.getPostIdByReportId(reportId);
        return commentRepo.findAllByPost(postId);
    }

    @GetMapping("{reportId}/comment")
    @RequiresPermission(scope = "v1:reports:read", description = "Read reports")
    CommentExtra getReportedComment(@PathVariable long reportId) {
        var report = moderationService.getReport(reportId);

        if (report instanceof CommentReport c)
            return commentRepo.findExtraById(c.commentId);
        else
            throw AppError.MATERIAL_NOT_DELETEABLE.asException();
    }

    @GetMapping("{reportId}/user")
    public UserExtra getReportedUser(@PathVariable long reportId) {
        return moderationService.getUserByReportId(reportId);
    }

    @GetMapping("{reportId}/user/posts")
    public List<PostExtra> getReportedUserPosts(@Auth.User long userId, @PathVariable long reportId) {
        var report = moderationService.getReport(reportId);
        if (report instanceof UserReport u) {
            var posts = postRepo.findAllByAccount(u.userId);

            List<Long> postIds = posts.stream().map(p -> p.id).toList();
            Set<Long> likedIds = postRepo.findLikedPostIds(userId, postIds);
            posts.forEach(post -> post.liked = likedIds.contains(post.id));

            return posts;
        } else
            throw AppError.REPORT_NOT_USER_RELATED.asException();

    }
}
