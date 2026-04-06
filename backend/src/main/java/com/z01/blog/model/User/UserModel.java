package com.z01.blog.model.User;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public class UserModel {
    @Id
    public long accountId;

    public String login;
    public String firstName;
    public String lastName;
    public boolean banned;
    public boolean deleted;

    @Transient
    public Boolean followed;
}