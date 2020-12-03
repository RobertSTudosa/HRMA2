package com.bzbees.hrma.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="user_accounts")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
	@Column(name="user_id")
	private long userId;
	
	@Column(name="username")
	private String userName;
	
	private String email;
	
	private String password;
	
	private boolean active;
	
	@ManyToMany
	@JoinTable(name="user_role",
			joinColumns=@JoinColumn(name="user_id"),
			inverseJoinColumns=@JoinColumn(name="role_id"))
	private List<UserRole> roles = new ArrayList<UserRole>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade= {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},optional = false)
	@JoinColumn(name = "person_id", referencedColumnName="person_id", nullable = false )
	private Person person;
	
	
	

	public User() {
		
	}

	public User(String userName, String email, String password, boolean active, List<UserRole> roles) {
		super();
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.active = active;
		this.roles = roles;
	}
	
//	public User(String userId, boolean active, String email, String password)
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	

	public long getUserId() {
		return userId;
	}

//	public void setUserId(long userId) {
//		this.userId = userId;
//	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	
	public User addRole(UserRole role) {
		this.roles.add(role);
		return this;
	}
	
	
	
	

}
