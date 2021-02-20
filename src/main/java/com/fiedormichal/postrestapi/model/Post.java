package com.fiedormichal.postrestapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Post {
    @Id
    @Column(nullable = false)
    private long id;
    private long userId;
    @Column(nullable = false)
    private String title;
    private String body;
    private boolean isUpdated=false;
}
