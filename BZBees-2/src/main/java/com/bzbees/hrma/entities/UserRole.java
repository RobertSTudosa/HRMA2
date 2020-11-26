package com.bzbees.hrma.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="userrole")
public class UserRole {
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userrole_generator")
	@SequenceGenerator(name = "userrole_generator", sequenceName = "userrole_seq", allocationSize = 1)
	@Column(name="role_id")
	private long roleId;
	
	private String permission;
	
	@ManyToMany
	@JoinTable(name="user_role",
			joinColumns=@JoinColumn(name="role_id"),
			inverseJoinColumns=@JoinColumn(name="user_id"))
	private List<User> users;
	
	
	public UserRole () {
		
	}


	public UserRole(String permission) {
		super();
		this.permission = permission;
//		this.users = users;
	}


	public String getPermission() {
		return permission;
	}


	public void setPermission(String permission) {
		this.permission = permission;
	}


	public List<User> getUsers() {
		return users;
	}


	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	
	
	

}
