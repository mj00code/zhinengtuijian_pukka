package com.ubo.iptv.logstash.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserPreferencesService {
    @Autowired
    private EsService esService;


    public void userPreferences(Set<Long> uids) {
        
    }

}
