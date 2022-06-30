package com.example.demosoap.service;

import com.example.demosoap.entity.UserEntity;
import com.example.demosoap.gs_ws.UserType;

import java.util.List;

public interface UserEntityService {
	public UserType getUserByLogin(String login);
	public List<UserType> getAllUsers();
	public UserEntity addUser(UserType userType);
	public boolean updateUser(UserType userType);
	public boolean deleteEntityByLogin(String login);
}
