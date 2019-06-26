package com.gadaldo.leisure.pass.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

@Data
@Builder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor(access = PACKAGE)
@Entity
@Table(name = "CUSTOMERS")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String homeCity;

}
