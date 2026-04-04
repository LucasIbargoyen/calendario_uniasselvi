package com.calendario.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String cor;

    @ManyToOne
    @JoinColumn(name = "semestre_id")
    private Semestre semestre;

    public Long getId()                    { return id; }
    public String getNome()                { return nome; }
    public void setNome(String n)          { this.nome = n; }
    public String getDescricao()           { return descricao; }
    public void setDescricao(String d)     { this.descricao = d; }
    public LocalDate getDataInicio()       { return dataInicio; }
    public void setDataInicio(LocalDate d) { this.dataInicio = d; }
    public LocalDate getDataFim()          { return dataFim; }
    public void setDataFim(LocalDate d)    { this.dataFim = d; }
    public LocalTime getHorarioInicio()    { return horarioInicio; }
    public void setHorarioInicio(LocalTime h){ this.horarioInicio = h; }
    public LocalTime getHorarioFim()       { return horarioFim; }
    public void setHorarioFim(LocalTime h) { this.horarioFim = h; }
    public String getCor()                 { return cor; }
    public void setCor(String c)           { this.cor = c; }
    public Semestre getSemestre()          { return semestre; }
    public void setSemestre(Semestre s)    { this.semestre = s; }
}