package pe.edu.upeu.pos_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la categoría (único)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // URL o nombre de la imagen (legacy)
    @Column(name = "image_url", length = 128)
    private String imageUrl;

    // Imagen almacenada en catálogo de categorías
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imagen_cat_id")
    @JsonIgnoreProperties({"data", "hibernateLazyInitializer", "handler"})
    private ImagenCat imagenCat;

    // Fecha de creación automática
    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    public Category() {}

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImagenCat getImagenCat() {
        return imagenCat;
    }

    public void setImagenCat(ImagenCat imagenCat) {
        this.imagenCat = imagenCat;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
