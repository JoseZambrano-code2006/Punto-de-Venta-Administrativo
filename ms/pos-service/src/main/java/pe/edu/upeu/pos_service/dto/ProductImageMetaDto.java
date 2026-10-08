package pe.edu.upeu.pos_service.dto;

import java.time.LocalDateTime;

public class ProductImageMetaDto {

    private Long id;
    private String name;
    private String contentType;
    private LocalDateTime creationDate;

    public ProductImageMetaDto() {
    }

    public ProductImageMetaDto(Long id, String name, String contentType, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
