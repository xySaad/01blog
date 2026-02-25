package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.Auth;
import com.z01.blog.model.Report.ReportModel;
import com.z01.blog.model.Report.ReportReason;
import com.z01.blog.model.Report.ReportRepository;
import com.z01.blog.model.Report.ReportedBy;

import cn.hutool.core.util.IdUtil;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {
    @Autowired
    ReportRepository repo;

    @GetMapping
    List<String> getReasons() {
        return repo.findReportReasons();
    }

    static class Request {
        public String type;
        public long id;
        public ReportReason reason;
        public String description;
    }

    @PostMapping
    void reportEntity(@Auth.User long userId, @RequestBody Request body) {
        ReportModel report = generateReport(body.type, body.id);
        report.id = IdUtil.getSnowflake().nextId();
        report.reportedBy = new ReportedBy();
        report.reportedBy.id = userId;
        report.reason = body.reason;
        report.description = body.description;
        report.createdAt = LocalDateTime.now();
        repo.save(report);
    }

    ReportModel generateReport(String type, long id) {
        switch (type) {
            case "POST" -> {
                var pr = new ReportModel.Post();
                pr.postId = id;
                return pr;
            }
            case "COMMENT" -> {
                var cr = new ReportModel.Comment();
                cr.commentId = id;
                return cr;
            }
            case "USER" -> {
                var ur = new ReportModel.User();
                ur.userId = id;
                return ur;
            }
            default -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }
}
