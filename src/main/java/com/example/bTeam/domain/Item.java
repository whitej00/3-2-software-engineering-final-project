package com.example.bTeam.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Item {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    @Column(length = 2000)
    private String content;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<ImageFile> imageFiles;
}
