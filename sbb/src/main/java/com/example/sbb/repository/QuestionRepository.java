package com.example.sbb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sbb.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer>{
	Question findBySubject(String subject);
}
