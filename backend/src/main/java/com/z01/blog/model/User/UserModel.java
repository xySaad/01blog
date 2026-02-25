package com.z01.blog.model.User;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@MappedSuperclass
public class UserModel {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long accountId;

    public String login;
    public String firstName;
    public String lastName;
    public boolean banned;
}