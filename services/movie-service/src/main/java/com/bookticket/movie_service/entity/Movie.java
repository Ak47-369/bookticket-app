package com.bookticket.movie_service.entity;

import com.bookticket.movie_service.enums.Certificate;
import com.bookticket.movie_service.enums.Genre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Duration;
import java.util.List;

@Document(collection = "movies")
@Getter
@Setter
@NoArgsConstructor
public class Movie {
    @Id
    @Indexed
    private String id;
    @Indexed(unique = true)
    private String name;
    private String description;
    private String image;
    private String trailer;
    private List<String> cast;
    @Indexed
    private List<Genre> genre;
    @Indexed
    private List<String> language;
    @Indexed
    private List<Certificate> certificate;
    private Duration duration;

    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
}
