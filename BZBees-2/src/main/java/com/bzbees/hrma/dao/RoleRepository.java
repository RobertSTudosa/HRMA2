package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Skill;
import com.bzbees.hrma.entities.UserRole;

@Repository
public interface RoleRepository extends CrudRepository <UserRole, Long> {
	
	@Override
	public List<UserRole> findAll();
	
	@Query(nativeQuery= true, value="select userrole.role_id , permission "
			+ "FROM userrole "
			+ "left outer join user_role ON userrole.role_id = user_role.user_id " 
			+ " WHERE user_role.user_id = ?1 ;")
	public List<UserRole> findRolesForTheUser (long userId);

}
