package org.styd.intproj.savorly.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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



//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "tag_id", referencedColumnName = "id", unique = true) // maintain the relation with Reicpe class
//    @JsonManagedReference

//    @OneToOne(mappedBy = "recipe", cascade = CascadeType.MERGE, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JsonManagedReference

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id", referencedColumnName = "id", unique = true) // maintain the relation with Reicpe class
    @JsonManagedReference
    //create is usable when using this annotation
    private Tag tag;
}