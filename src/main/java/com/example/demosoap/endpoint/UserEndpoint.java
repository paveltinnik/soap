package com.example.demosoap.endpoint;

import com.example.demosoap.entity.UserEntity;
import com.example.demosoap.gs_ws.*;
import com.example.demosoap.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class UserEndpoint {
	public static final String NAMESPACE_URI = "http://paveltinnik.com/demosoap";
	
	private UserEntityService userService;
	
	public UserEndpoint() {
	}
	
	@Autowired
	public UserEndpoint(UserEntityService userService) {
		this.userService = userService;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserByLoginRequest")
	@ResponsePayload
	public GetUserByLoginResponse getUserByLogin(@RequestPayload GetUserByLoginRequest request) {
		GetUserByLoginResponse response = new GetUserByLoginResponse();
		UserType userType = userService.getUserByLogin(request.getUserLogin());
		
		response.setUserType(userType);
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
	@ResponsePayload
	public GetAllUsersResponse getAllUsers(@RequestPayload GetAllUsersRequest request) {
		GetAllUsersResponse response = new GetAllUsersResponse();
		List<UserType> userTypeList = userService.getAllUsers();
		
		response.getUserType().addAll(userTypeList);
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
	@ResponsePayload
	public AddUserResponse addUser(@RequestPayload AddUserRequest request) {
		try {
			AddUserResponse response = new AddUserResponse();
			ServiceStatus serviceStatus = new ServiceStatus();
			UserType userType = request.getUserType();
			
			if (userService.getUserByLogin(userType.getLogin()) != null) {
				serviceStatus.setSuccess(false);
				serviceStatus.getErrors().add("User with login " + userType.getLogin() + " exists already");
				response.setServiceStatus(serviceStatus);
				return response;
			}
			
			if (isUserTypeInputsValid(userType) && !userType.getRoles().getName().isEmpty()
					&& userType.getRoles().getName().stream().noneMatch(roleName -> roleName.equals(""))
			) {
				UserEntity savedUserEntity = userService.addUser(userType);
				
				if (savedUserEntity == null) {
					serviceStatus.setSuccess(false);
					serviceStatus.getErrors().add("Exception while adding User");
				} else {
					serviceStatus.setSuccess(true);
				}
			} else {
				serviceStatus.setSuccess(false);
				serviceStatus.getErrors().add("Some fields are empty or password doesn't match");
			}
			
			response.setServiceStatus(serviceStatus);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
	@ResponsePayload
	public UpdateUserResponse updateUser(@RequestPayload UpdateUserRequest request) {
		UpdateUserResponse response = new UpdateUserResponse();
		ServiceStatus serviceStatus = new ServiceStatus();
		UserType userType = request.getUserType();
		
		if (isUserTypeInputsValid(userType)) {
			if (userService.getUserByLogin(userType.getLogin()) == null) {
				serviceStatus.setSuccess(false);
				serviceStatus.getErrors().add("User = " + request.getUserType().getLogin() + " not found");
			} else {
				// Update the user in database
				boolean flag = userService.updateUser(request.getUserType());
				
				if (flag == false) {
					serviceStatus.setSuccess(false);
					serviceStatus.getErrors().add("Exception while updating User = " + request.getUserType().getLogin());
				} else {
					serviceStatus.setSuccess(true);
				}
			}
		} else {
			serviceStatus.setSuccess(false);
			serviceStatus.getErrors().add("Some fields are empty or password doesn't match");
		}
		
		response.setServiceStatus(serviceStatus);
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
	@ResponsePayload
	public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request) {
		DeleteUserResponse response = new DeleteUserResponse();
		ServiceStatus serviceStatus = new ServiceStatus();
		
		boolean flag = userService.deleteEntityByLogin(request.getUserLogin());
		
		if (flag == false) {
			serviceStatus.setSuccess(false);
			serviceStatus.getErrors().add("Exception while deleting User with login " + request.getUserLogin());
		} else {
			serviceStatus.setSuccess(true);
		}
		
		response.setServiceStatus(serviceStatus);
		return response;
	}
	
	private boolean isUserTypeInputsValid(UserType userType) {
		String login = userType.getLogin();
		String name = userType.getName();
		String password = userType.getPassword();
		
		if (!login.isEmpty() && !name.isEmpty() && password.matches(".*(([A-Z].*[0-9])|([0-9].*[A-Z])).*")) {
			return true;
		} else {
			return false;
		}
	}
}
