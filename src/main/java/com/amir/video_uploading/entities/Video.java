package com.amir.video_uploading.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_seq")
    @SequenceGenerator(name = "video_seq", sequenceName = "video_seq", allocationSize = 1)
    private Long id;
    @NotBlank (message = "title is required")
    @NotNull(message = "title can not be empty")
    private String title;
    private String description;
    private double size;
    private String contentType;
    @NotBlank(message = "video url is required")
    @NotNull(message = "video url can not be empty")
    private String videoUrl;
    private Date created;
    private Date modified;
}
