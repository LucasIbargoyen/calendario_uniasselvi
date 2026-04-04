package com.calendario.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.calendario.model.Semestre;
public interface SemestreRepository extends JpaRepository<Semestre, Long> {}