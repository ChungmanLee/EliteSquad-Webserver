package com.ues.adminportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.adminportal.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {

}
