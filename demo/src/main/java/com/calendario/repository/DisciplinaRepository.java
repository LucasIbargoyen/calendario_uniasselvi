package com.calendario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.calendario.model.Disciplina;

import java.util.List;
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    List<Disciplina> findBySemestreId(Long semestreId);
}
