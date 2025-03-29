package com.vision_back.vision_back.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vision_back.vision_back.repository.UserTagRepository;

@Service
public class TagService {

    private final UserTagRepository UserTagRepository;

    public TagService(UserTagRepository userTagRepository) {
        this.UserTagRepository = userTagRepository;
    }

    public Map<Long, Integer> getCardCountByTag() {
        List<Object[]> results = UserTagRepository.countCardsByTag();
        Map<Long, Integer> tagCounts = new HashMap<>();
        
        for (Object[] result : results) {
            Long tagId = (Long) result[0];
            Integer count = ((Number) result[1]).intValue();
            tagCounts.put(tagId, count);
        }
        
        return tagCounts;
    }
}
