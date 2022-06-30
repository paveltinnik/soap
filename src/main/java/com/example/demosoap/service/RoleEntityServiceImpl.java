package com.example.demosoap.service;

import com.example.demosoap.entity.RoleEntity;
import com.example.demosoap.repository.RoleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RoleEntityServiceImpl implements RoleEntityService {
	private RoleEntityRepository repository;
	
	public RoleEntityServiceImpl() {}
	
	@Autowired
	public RoleEntityServiceImpl(RoleEntityRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public RoleEntity getRoleEntityByName(String name) {
		return this.repository.findByName(name);
	}
}
