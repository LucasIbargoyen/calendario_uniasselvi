def gerar_calendario(ano, mes):
    import calendar
    cal = calendar.monthcalendar(ano, mes)
    matriz= []
    for semana in cal:
        nova_semana = semana[1:] + semana [:1]
        matriz.append(nova_semana)

    return matriz
def imprimir_calendario(matriz, mes, ano):
    nomes_meses = [
        "", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    ]

    print(f"\n{nomes_meses[mes]} {ano}".center(28))
    print("Seg Ter Qua Qui Sex Sáb Dom")

    for semana in matriz:
        linha = []
        for dia in semana:
            if dia == 0:
                linha.append(" ")
            else:
                linha.append(f"{dia:2}")
        print("  ".join(linha))

ano = 2026
mes = 2

matriz = gerar_calendario(ano, mes)
imprimir_calendario(matriz, mes, ano)
    






   
        
        
