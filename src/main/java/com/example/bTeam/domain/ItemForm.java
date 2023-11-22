package com.example.bTeam.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemForm {
    private Long itemId;
    private String itemTitle;
    private String itemContent;
    private List<MultipartFile> imageFiles;
}
