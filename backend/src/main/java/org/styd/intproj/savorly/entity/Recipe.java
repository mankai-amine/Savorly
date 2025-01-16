package org.styd.intproj.savorly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recipes") // maybe public.recipes?
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Name is required")
    @Column(unique = true)
    @Size(min = 10, max = 300, message = "Recipes must contain between {min} and {max} characters")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ingredients;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "picture")
    private String picture;

    @Column(name = "authorid")
    private Long authorId;

//    @ManyToMany
//    @JoinTable(
//            name = "recipe_tags",
//            joinColumns = @JoinColumn(name = "recipeid"),
//            inverseJoinColumns = @JoinColumn(name = "tagid")
//    )
//    private Set<Tag> tags;
}
