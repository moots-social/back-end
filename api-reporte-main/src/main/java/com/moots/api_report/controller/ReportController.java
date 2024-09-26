package com.moots.api_report.controller;

import com.moots.api_report.event.ReportPostEvent;
import com.moots.api_report.model.Report;
import com.moots.api_report.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @KafkaListener(topics = "report-post-topic")
    public void saveReportPost(ReportPostEvent reportPost){
        System.out.println("Mensagem recebida " + reportPost);
        reportService.saveReportPost(reportPost);
        log.info("Mensagem salva com sucesso");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> reportById(@PathVariable String id){
        var report = reportService.findById(id);
        return ResponseEntity.ok().body(report);
    }

    @DeleteMapping("/deletar/{id}/post/{postId}")
    public ResponseEntity<Optional<Report>> deletarReport(@PathVariable String id, @PathVariable Long postId){
        var reportDeletado = reportService.deleteById(id, postId);
        return ResponseEntity.ok().body(reportDeletado);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Report>> encontrarReportPorPostId(@PathVariable Long postId){
        var reports = reportService.findByPostId(postId);
        return ResponseEntity.ok().body(reports);
    }
}
