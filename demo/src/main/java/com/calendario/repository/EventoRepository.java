package com.calendario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.calendario.model.Evento;

import java.time.LocalDate;
import java.util.List;
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findBySemestreIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
        Long semestreId, LocalDate data, LocalDate data2
    );
}
