package com.example.demosoap.entity;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@Column(name = "login", nullable = false, unique = true)
	private String login;
	
	private String name;
	
	private String password;
	
	@ManyToMany(
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "users_roles",
			joinColumns = @JoinColumn(name = "users_login"),
			inverseJoinColumns = @JoinColumn(name = "roles_id")
	)
	private Set<RoleEntity> roles = new HashSet<>();
	
	public void addRole(RoleEntity role) {
		this.roles.add(role);
		role.getUsers().add(this);
	}
	
	public void removeRole(RoleEntity role) {
		this.roles.remove(role);
		role.getUsers().remove(this);
	}
	
	public void removeRoles() {
		for (RoleEntity roleEntity : new HashSet<>(roles)) {
			removeRole(roleEntity);
		}
	}
	
	public UserEntity() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public Set<RoleEntity> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}
	
	@Override
	public String toString() {
		return "UserEntity{" +
				"login='" + login + '\'' +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", roles=" + roles +
				'}';
	}
}

