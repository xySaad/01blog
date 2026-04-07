package com.z01.blog.model.Report;

import org.hibernate.annotations.Formula;

import com.z01.blog.model.Audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_reports")
@PrimaryKeyJoinColumn(name = "id")
public class UserReport extends ReportModel {
    public long userId;

    @Formula("(SELECT u.login FROM users u WHERE u.account_id = user_id)")
    public String userLogin;

    @Override
    public MaterialRef getMaterial() {
        return new MaterialRef(Auditable.USER, userId);
    }

}
