package de.schwarz.libraryapp.customer.domain.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer", schema = "library")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "library.customer_id_seq")
    @SequenceGenerator(name = "library.customer_id_seq", sequenceName = "library.customer_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "email", length = 30, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;


    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}