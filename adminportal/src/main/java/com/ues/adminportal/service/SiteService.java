package com.ues.adminportal.service;

import com.ues.adminportal.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    public boolean isDomainExists(String domain) {
        return siteRepository.existsByDomain(domain);
    }
}