package com.example.demosoap.repository;

import com.example.demosoap.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
	public RoleEntity findByName(String name);
}
