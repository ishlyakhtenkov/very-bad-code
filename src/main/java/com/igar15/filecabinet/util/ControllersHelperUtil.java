package com.igar15.filecabinet.util;

import com.igar15.filecabinet.entity.AbstractNamedEntity;
import com.igar15.filecabinet.service.DeveloperService;
import com.igar15.filecabinet.service.DeveloperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControllersHelperUtil {

    @Autowired
    DeveloperService developerService;

    public String[] getDeveloperNames() {
        return developerService.findAll().stream()
                .map(AbstractNamedEntity::getName)
                .toArray(String[]::new);
    }
}