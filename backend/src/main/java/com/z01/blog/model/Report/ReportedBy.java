package com.z01.blog.model.Report;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReportedBy {
    @Column(name = "reported_by")
    public long id;
    @Formula("(SELECT u.login FROM users u WHERE u.account_id = reported_by)")
    public String login;
}