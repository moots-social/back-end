package com.moots.api_report.service;

import com.moots.api_report.event.ReportPostEvent;
import com.moots.api_report.model.Report;
import com.moots.api_report.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Report saveReportPost(ReportPostEvent reportPostEvent){
        var report = new Report();
        report.setDenuncia(reportPostEvent.getDenuncia());
        report.setPostId(reportPostEvent.getPostId());

        return reportRepository.save(report);
    }

    @Cacheable(value = "report", key = "#id")
    public Report findById(String id){
        return reportRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Caching(evict = {
            @CacheEvict(value = "report-postId", key = "#postId"),
            @CacheEvict(value = "report", key = "#id") })
    public Optional<Report> deleteById(String id, Long postId){
        var reportDeletado = reportRepository.findById(id);
        reportRepository.deleteById(id);
        return reportDeletado;
    }

    @Cacheable(value = "report-postId", key = "#postId")
    public List<Report> findByPostId(Long postId){
        return reportRepository.findByPostId(postId);
    }



}
