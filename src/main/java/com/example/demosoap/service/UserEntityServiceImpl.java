package com.example.demosoap.service;

import com.example.demosoap.entity.RoleEntity;
import com.example.demosoap.entity.UserEntity;
import com.example.demosoap.gs_ws.RolesType;
import com.example.demosoap.gs_ws.UserType;
import com.example.demosoap.repository.RoleEntityRepository;
import com.example.demosoap.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {
	private UserEntityRepository userEntityRepository;
	private RoleEntityRepository roleEntityRepository;
	
	public UserEntityServiceImpl() {}
	
	@Autowired
	public UserEntityServiceImpl(UserEntityRepository userEntityRepository, RoleEntityRepository roleEntityRepository) {
		this.userEntityRepository = userEntityRepository;
		this.roleEntityRepository = roleEntityRepository;
	}
	
	@Transactional
	@Override
	public UserType getUserByLogin(String login) {
		UserEntity userEntity = userEntityRepository.findByLogin(login);
		
		if (userEntity != null) {
			return mapUserEntityToUserType(userEntity);
		} else {
			return null;
		}
	}
	
	@Override
	public List<UserType> getAllUsers() {
		List<UserType> userTypesList = new ArrayList<>();
		List<UserEntity> userEntitiesList = userEntityRepository.findAll();
		
		userEntitiesList.forEach(userEntity -> {
			UserType userType = mapUserEntityToUserType(userEntity);
			userType.setRoles(null);
			userTypesList.add(userType);
		});
		
		return userTypesList;
	}
	
	@Override
	public UserEntity addUser(UserType userType) {
		try {
			UserEntity userEntity = new UserEntity();
			mapUserTypeToUserEntity(userType, userEntity);
			
			return this.userEntityRepository.save(userEntity);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	@Override
	public boolean updateUser(UserType userType) {
		try {
			UserEntity userEntity = userEntityRepository.findByLogin(userType.getLogin());
			userEntity.getRoles().clear();
			mapUserTypeToUserEntity(userType, userEntity);
			
			userEntityRepository.save(userEntity);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean deleteEntityByLogin(String login) {
		try {
			UserEntity userEntity = userEntityRepository.findByLogin(login);
			
			if (userEntity != null) {
				userEntity.removeRoles();
				userEntityRepository.deleteByLogin(userEntity.getLogin());
			}
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void mapUserTypeToUserEntity(UserType userType, UserEntity userEntity) {
		try {
			userEntity.setLogin(userType.getLogin());
			userEntity.setName(userType.getName());
			userEntity.setPassword(userType.getPassword());
			
			if (userEntity.getRoles() == null) {
				userEntity.setRoles(new HashSet<>());
			}
			
			if (!userType.getRoles().getName().isEmpty()) {
				userType.getRoles().getName().stream().forEach(roleName -> {
					RoleEntity roleEntity = roleEntityRepository.findByName(roleName);
					
					if (roleEntity == null) {
						roleEntity = new RoleEntity();
						roleEntity.setUsers(new HashSet<>());
					}
					
					roleEntity.setName(roleName);
					userEntity.addRole(roleEntity);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private UserType mapUserEntityToUserType(UserEntity userEntity) {
		try {
			UserType userType = new UserType();
			userType.setLogin(userEntity.getLogin());
			userType.setName(userEntity.getName());
			userType.setPassword(userEntity.getPassword());
			
			RolesType rolesType = new RolesType();
			Set<RoleEntity> roleEntities = userEntity.getRoles();
			roleEntities.forEach(r -> rolesType.getName().add(r.getName()));
			userType.setRoles(rolesType);
			
			return userType;
		} catch (Exception e) {
			e.printStackTrace();
			return  null;
		}
	}
}
