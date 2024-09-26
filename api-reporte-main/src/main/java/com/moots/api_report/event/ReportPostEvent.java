package com.moots.api_report.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long postId;
    private String denuncia;
}
