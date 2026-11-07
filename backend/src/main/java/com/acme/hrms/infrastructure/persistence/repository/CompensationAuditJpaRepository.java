package com.acme.hrms.infrastructure.persistence.repository;

import com.acme.hrms.infrastructure.persistence.entity.CompensationAuditJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompensationAuditJpaRepository extends JpaRepository<CompensationAuditJpaEntity, UUID> {
    List<CompensationAuditJpaEntity> findByEmployeeIdOrderByModifiedAtDesc(UUID employeeId);
    List<CompensationAuditJpaEntity> findTop50ByOrderByModifiedAtDesc();
}

