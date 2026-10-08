package pe.edu.upeu.pos_service.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Categoría a la que pertenece el producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;

    // Nombre del producto
    @Column(nullable = false, length = 128)
    private String name;

    // Descripción del producto
    @Column(columnDefinition = "TEXT")
    private String description;

    // Precio del producto
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Imagen del producto (legacy texto)
    @Column(name = "image_url", length = 128)
    private String imageUrl;

    // Imagen almacenada en catálogo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonIgnoreProperties({"data", "hibernateLazyInitializer", "handler"})
    private ProductImage image;

    // Fecha de creación automática
    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ProductImage getImage() {
        return image;
    }

    public void setImage(ProductImage image) {
        this.image = image;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}