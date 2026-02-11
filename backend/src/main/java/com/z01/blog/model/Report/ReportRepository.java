package com.z01.blog.model.Report;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<ReportModel, Long> {

    @Query(value = "SELECT unnest(enum_range(NULL::report_reason))", nativeQuery = true)
    public List<String> findReportReasons();
}
