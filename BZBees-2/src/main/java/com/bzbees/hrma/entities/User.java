package com.bzbees.hrma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="user_accounts")
public class User implements UserDetails, Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
	@Column(name="user_id")
	private long userId;
	
	@Column(name="username")
	private String userName;
	
	private String email;
	
	private String password;
	
	@Column(name="account_non_expired")
    private boolean accountNonExpired = true;
	
	@Column(name="account_non_locked")
    private boolean accountNonLocked = true;
	
	@Column(name="credentials_non_expired")
    private boolean credentialsNonExpired = true;
	
	private boolean active;
	
	@ManyToMany
	@JoinTable(name="user_role",
			joinColumns=@JoinColumn(name="user_id"),
			inverseJoinColumns=@JoinColumn(name="role_id"))
	private List<UserRole> roles = new ArrayList<UserRole>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade= {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},optional = false)
	@JoinColumn(name = "person_id", referencedColumnName="person_id", nullable = false )
	private Person person;
	
	@OneToOne(mappedBy="user")
	private Agency agency;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Agency oneAgency;
	
	
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
	
	public User removeRole (UserRole role) {
		this.roles.remove(role);
		return this;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.toString())));
		return authorities;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return this.credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.active;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if( !(obj instanceof User)) return false;
		
		User user = (User) obj;
		
		if(!getUsername().equals(user.getUsername())) return false;
		// TODO Auto-generated method stub
		return userId != 0L && userId == user.getUserId();
	}

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	public Agency getOneAgency() {
		return oneAgency;
	}

	public void setOneAgency(Agency oneAgency) {
		this.oneAgency = oneAgency;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}
	
	
	
	
	
	
	

}
