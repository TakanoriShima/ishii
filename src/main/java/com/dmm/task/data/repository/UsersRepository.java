package com.dmm.task.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmm.task.data.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
	  List<Users> findByName(String name);
}
