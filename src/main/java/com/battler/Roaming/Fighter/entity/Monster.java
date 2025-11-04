package com.battler.Roaming.Fighter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "monsters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9 ]*$")
    private String name;

    @Column(nullable = false)
    @Min(0)
    private Integer attack;

    @Column(nullable = false)
    @Min(0)
    private Integer defence;

    @Column(nullable = false)
    @Min(0)
    private Integer health;
}
