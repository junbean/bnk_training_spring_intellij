package com.example.book_manage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bid;

    @Column(name="title", nullable = false, length = 200)
    private String title;

    @Column(name = "writer", length = 100)
    private String writer;

    @Column(name = "publisher", length = 100)
    private String publisher;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name="info", columnDefinition = "TEXT")
    private String info;
}
