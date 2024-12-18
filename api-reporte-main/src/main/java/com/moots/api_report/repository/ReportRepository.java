package com.moots.api_report.repository;

import com.moots.api_report.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {


    List<Report> findByPostIdOrderByDataCriacaoDesc(Long postId);

    List<Report> findAllByOrderByDataCriacaoDesc();

}
