package com.shikenstore.shikenstoreapp.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "original_price")
    private Integer originalPrice;

    private Integer discount;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 500)
    private String image;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean featured = false;

    private Double rating;

    private Integer reviews;

    @Column(name = "release_date", length = 20)
    private String releaseDate;

    @Column(length = 200)
    private String developer;

    @JsonIgnore
    @Column(length = 200)
    private String platform;

    @JsonIgnore
    @Column(length = 500)
    private String tags;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonGetter("platform")
    public List<String> getPlatformList() {
        if (platform == null || platform.isEmpty()) return Collections.emptyList();
        return Arrays.asList(platform.split(","));
    }

    @JsonSetter("platform")
    public void setPlatformFromJson(Object value) {
        if (value instanceof List<?> list) {
            this.platform = String.join(",", list.stream().map(Object::toString).toList());
        } else if (value instanceof String s) {
            this.platform = s;
        }
    }

    @JsonGetter("tags")
    public List<String> getTagsList() {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();
        return Arrays.asList(tags.split(","));
    }

    @JsonSetter("tags")
    public void setTagsFromJson(Object value) {
        if (value instanceof List<?> list) {
            this.tags = String.join(",", list.stream().map(Object::toString).toList());
        } else if (value instanceof String s) {
            this.tags = s;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
