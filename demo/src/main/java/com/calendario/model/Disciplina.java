package com.calendario.model;

import jakarta.persistence.*;

@Entity
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cor;

    // dias da semana: 0=Dom,1=Seg,...,6=Sab — armazenados como "1,3,5"
    private String diasSemana;

    @ManyToOne
    @JoinColumn(name = "semestre_id")
    private Semestre semestre;

    public Long getId()               { return id; }
    public String getNome()           { return nome; }
    public void setNome(String n)     { this.nome = n; }
    public String getCor()            { return cor; }
    public void setCor(String c)      { this.cor = c; }
    public String getDiasSemana()     { return diasSemana; }
    public void setDiasSemana(String d){ this.diasSemana = d; }
    public Semestre getSemestre()     { return semestre; }
    public void setSemestre(Semestre s){ this.semestre = s; }
}