package com.z01.blog.model.Report;

import com.z01.blog.model.Audit.Auditable;

public record MaterialRef(Auditable type, long id) {

}
