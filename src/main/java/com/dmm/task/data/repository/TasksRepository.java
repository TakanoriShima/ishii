package com.dmm.task.data.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmm.task.data.entity.Tasks;

public interface TasksRepository extends JpaRepository<LocalDate,Tasks> {

}