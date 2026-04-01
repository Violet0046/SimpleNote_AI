package com.simplenote.backend.service.impl;

import com.simplenote.backend.mapper.PostMapper;
import com.simplenote.backend.pojo.Post;
import com.simplenote.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Override
    public void add(Post post) {
        postMapper.add(post);
    }
}