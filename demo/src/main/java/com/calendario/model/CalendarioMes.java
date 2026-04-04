package com.calendario.model;

import java.util.List;

public class CalendarioMes {

    private int ano;
    private int mes;
    private String nomeMes;
    private List<List<Integer>> semanas;

    public CalendarioMes(int ano, int mes, String nomeMes, List<List<Integer>> semanas) {
        this.ano = ano;
        this.mes = mes;
        this.nomeMes = nomeMes;
        this.semanas = semanas;
    }

    // Getters — obrigatórios para o Jackson serializar em JSON
    public int getAno()                       { return ano; }
    public int getMes()                       { return mes; }
    public String getNomeMes()                { return nomeMes; }
    public List<List<Integer>> getSemanas()   { return semanas; }
}