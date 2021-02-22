package com.fiedormichal.postrestapi.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiedormichal.postrestapi.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class JsonPostMapper {
    public List<Post> getMappedPostsList(String restURL) throws IOException {
        URL url = new URL(restURL);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Post> posts = Arrays.asList(objectMapper.readValue(url, Post[].class));
        return posts;
    }
}
