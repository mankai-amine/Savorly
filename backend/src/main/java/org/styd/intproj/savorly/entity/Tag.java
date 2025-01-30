package org.styd.intproj.savorly.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //for postgresql
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Column(name = "ingredients")
    private String ingredients;

    @Column(name = "description")
    private String description;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536) // dimensions, ollama 768, openai-small 1536, openai-middle 3072
    @JsonSerialize
    @JsonDeserialize
    @JsonIgnore
    private float[] embedding;

    @OneToOne
    @JoinColumn(name = "recipe_id", unique = true)
    @JsonBackReference
    private Recipe recipe;

}