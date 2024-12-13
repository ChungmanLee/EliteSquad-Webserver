package com.ues.adminportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.adminportal.entity.Site;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByUser_Id(int userId);
    boolean existsByDomain(String domain);
}
