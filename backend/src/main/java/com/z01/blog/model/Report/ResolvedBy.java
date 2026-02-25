package com.z01.blog.model.Report;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ResolvedBy {
    @Column(name = "resolved_by")
    public Long id;
    @Formula("(SELECT u.login FROM users u WHERE u.account_id = resolved_by)")
    public String login;
}
