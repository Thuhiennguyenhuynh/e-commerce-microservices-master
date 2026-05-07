package com.rainbowforest.productcatalogservice.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Tên banner không được để trống")
    private String name;

    @Column(name = "image_url")
    @NotBlank(message = "URL hình ảnh không được để trống")
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "position")
    private int position;

    public Banner() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
