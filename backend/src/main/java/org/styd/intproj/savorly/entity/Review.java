package org.styd.intproj.savorly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipeId", nullable = false)
    // When fetching Review data, each review is linked to a Recipe (because of the @ManyToOne relationship)
    // the backend would automatically send the entire Recipe object along with each Review, which is not needed
    // @JsonIgnore allows to avoid this
    @JsonIgnore
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    // @JsonProperty is used to map the User entity to "author" in JSON responses
    @JsonProperty("author")
    private User user;

    private String text;
}
