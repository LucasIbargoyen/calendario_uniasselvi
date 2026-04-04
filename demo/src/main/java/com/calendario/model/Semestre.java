package com.calendario.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String turma;
    private String curso;
    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disciplina> disciplinas;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos;

    // Getters e Setters
    public Long getId()                      { return id; }
    public String getTurma()                 { return turma; }
    public void setTurma(String t)           { this.turma = t; }
    public String getCurso()                 { return curso; }
    public void setCurso(String c)           { this.curso = c; }
    public String getSemestre()              { return semestre; }
    public void setSemestre(String s)        { this.semestre = s; }
    public LocalDate getDataInicio()         { return dataInicio; }
    public void setDataInicio(LocalDate d)   { this.dataInicio = d; }
    public LocalDate getDataFim()            { return dataFim; }
    public void setDataFim(LocalDate d)      { this.dataFim = d; }
    public List<Disciplina> getDisciplinas() { return disciplinas; }
    public List<Evento> getEventos()         { return eventos; }
}