package com.moots.api_report.model;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report")
public class Report {

    @Id
    private String id;

    @NonNull
    private Long postId;

    @NonNull
    private String denuncia;

}
