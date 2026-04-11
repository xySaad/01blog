package com.z01.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.z01.blog.exception.AppError;
import com.z01.blog.model.BaseEntity;
import com.z01.blog.model.Audit.AuditAction;
import com.z01.blog.model.Audit.Deleteable;
import com.z01.blog.model.Audit.Hideable;
import com.z01.blog.model.DTO.AuditReportRequest;
import com.z01.blog.model.Report.MaterialRef;
import com.z01.blog.model.Report.ReportRepository;
import com.z01.blog.model.Report.ResolvedBy;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class AuditService {
    @Autowired
    EntityManager em;

    @Autowired
    ReportRepository reportRepo;

    @Autowired
    UserRepo userRepo;

    public void ignoreReport(long reportId, long userId) {
        var report = reportRepo.findById(reportId)
                .orElseThrow(() -> AppError.REPORT_NOT_FOUND.asException());
        if (report.resolvedBy != null)
            throw AppError.REPORT_ALREADY_RESOLVED.asException();

        report.actionTaken = "IGNORE_REPORT";
        report.resolvedBy = new ResolvedBy();
        report.resolvedBy.id = userId;
        reportRepo.save(report);
    }

    public void auditReport(AuditReportRequest request, long userId) {
        var report = reportRepo.findById(request.id())
                .orElseThrow(() -> AppError.REPORT_NOT_FOUND.asException());
        if (report.resolvedBy != null)
            throw AppError.REPORT_ALREADY_RESOLVED.asException();

        var action = request.action();
        auditMaterial(report.getMaterial(), action);

        report.actionTaken = action.name();
        report.resolvedBy = new ResolvedBy();
        report.resolvedBy.id = userId;
        reportRepo.save(report);
    }

    @Transactional
    public void auditMaterial(MaterialRef materialRef, AuditAction action) {
        var material = em.find(materialRef.type().entity, materialRef.id());
        if (material == null)
            throw AppError.MATERIAL_NOT_FOUND.asException();

        switch (action) {
            case DELETE -> deleteMaterial(material);
            case BAN_USER -> banMaterialOwner(material);
            case HIDE -> hideMaterial(material);
        }

    }

    private void hideMaterial(Object material) {
        if (material instanceof Hideable h)
            h.hide();
        else
            throw AppError.MATERIAL_NOT_HIDEABLE.asException();
    }

    private void deleteMaterial(Object material) {
        if (material instanceof Deleteable d)
            d.delete();
        else
            throw AppError.MATERIAL_NOT_DELETEABLE.asException();
    }

    private void banMaterialOwner(Object material) {
        if (material instanceof UserEntity u)
            u.banned = true;
        else if (material instanceof BaseEntity b)
            userRepo.findById(b.account).get().banned = true;
        else
            throw AppError.REPORT_NOT_USER_RELATED.asException();
    }
}