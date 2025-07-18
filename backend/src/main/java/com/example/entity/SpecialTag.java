package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Entity
@RegisterForReflection
public class SpecialTag extends PanacheEntity {
    @Column(name="name", unique = true)
    private String name;
    private String type;
    private String color;
    private String effect;
    private Boolean round;
}
