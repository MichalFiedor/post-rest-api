package com.fiedormichal.postrestapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;

@Getter
@Setter
@Builder
public class PostDto {
    @Id
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String title;
    private String body;
}
