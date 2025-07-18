package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Favorite extends PanacheEntity {
    @Column(name="path", unique = true)
    private String path;
    private String showName;
    
    public Favorite(){}
    public Favorite(String path, String name){
        this.path = path;
        this.showName = name;
    }
}
