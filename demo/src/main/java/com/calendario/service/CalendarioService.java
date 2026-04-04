package com.calendario.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CalendarioService {

    private static final String[] NOMES_MESES = {
        "", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    // Mesma lógica do seu gerarCalendario(), mas retorna List em vez de int[][]
    public List<List<Integer>> gerarCalendario(int ano, int mes) {
        Calendar cal = Calendar.getInstance();
        cal.set(ano, mes - 1, 1);

        int totalDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int primeiroDia = cal.get(Calendar.DAY_OF_WEEK);
        int offsetSeg = (primeiroDia == Calendar.SUNDAY) ? 6 : primeiroDia - 2;

        List<List<Integer>> matriz = new ArrayList<>();
        List<Integer> semana = new ArrayList<>(Collections.nCopies(7, 0));
        int coluna = offsetSeg;

        for (int dia = 1; dia <= totalDias; dia++) {
            semana.set(coluna, dia);
            coluna++;
            if (coluna == 7) {
                matriz.add(new ArrayList<>(semana));
                semana = new ArrayList<>(Collections.nCopies(7, 0));
                coluna = 0;
            }
        }

        if (coluna > 0) {
            matriz.add(new ArrayList<>(semana));
        }

        return matriz;
    }

    public String getNomeMes(int mes) {
        if (mes < 1 || mes > 12) throw new IllegalArgumentException("Mês inválido: " + mes);
        return NOMES_MESES[mes];
    }
}