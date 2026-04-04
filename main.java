import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class main {

    static int[][] gerarCalendario(int ano, int mes) {
        Calendar cal = Calendar.getInstance();
        cal.set(ano, mes - 1, 1);

        int totalDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int primeiroDiaSemana = cal.get(Calendar.DAY_OF_WEEK); // 1=Dom, 2=Seg, ..., 7=Sab

        // Converte para índice 0=Seg, ..., 5=Sab, 6=Dom
        int offsetSeg = (primeiroDiaSemana == Calendar.SUNDAY) ? 6 : primeiroDiaSemana - 2;

        List<int[]> matriz = new ArrayList<>();
        int[] semana = new int[7];
        int coluna = offsetSeg;

        for (int dia = 1; dia <= totalDias; dia++) {
            semana[coluna] = dia;
            coluna++;
            if (coluna == 7) {
                matriz.add(semana.clone());
                semana = new int[7];
                coluna = 0;
            }
        }

        // Adiciona a última semana incompleta (se houver dias restantes)
        if (coluna > 0) {
            matriz.add(semana.clone());
        }

        return matriz.toArray(new int[0][]);
    }

    static void imprimirCalendario(int[][] matriz, int mes, int ano) {
        String[] nomesMeses = {
            "", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };

        String cabecalho = nomesMeses[mes] + " " + ano;
        System.out.printf("%n%-28s%n", centerText(cabecalho, 28));
        System.out.println("Seg Ter Qua Qui Sex Sáb Dom");

        for (int[] semana : matriz) {
            List<String> linha = new ArrayList<>();
            for (int dia : semana) {
                if (dia == 0) {
                    linha.add("  ");
                } else {
                    linha.add(String.format("%2d", dia));
                }
            }
            System.out.println(String.join("  ", linha));
        }
    }

    static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }

    public static void main(String[] args) {
        int ano = 2026;
        int mes = 2;

        int[][] matriz = gerarCalendario(ano, mes);
        imprimirCalendario(matriz, mes, ano);
    }
}