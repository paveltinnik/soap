package com.example.demosoap.repository;

import com.example.demosoap.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
	public UserEntity findByLogin(String login);
	public int deleteByLogin(String login);
}
