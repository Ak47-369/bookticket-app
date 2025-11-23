package com.bookticket.theater_service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "theater")
@Getter
@Setter
@NoArgsConstructor
public class Theater extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String landmark;
}
