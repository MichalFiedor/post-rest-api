package com.fiedormichal.postrestapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
public class Post {
    @Id
    @Column(nullable = false)
    @Positive(message = "Id must be positive digit.")
    private long id;
    private long userId;
    @Column(nullable = false)
    @Size(min = 5, max = 500, message = "Title must contain from 5 to 500 characters.")
    private String title;
    @Size(min = 5, max = 500, message = "Body must contain from 5 to 3000 characters.")
    private String body;
    private boolean isUpdated = false;
}
