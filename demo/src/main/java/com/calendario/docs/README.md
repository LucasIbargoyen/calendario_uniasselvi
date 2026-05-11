## PROJETO CALENDÁRIO ##

**OBJETIVO:**
Permitir que alunos se organizem, marquem eventos e continuem informados sobre datas pertinentes ao curso e turma mesmo quando o dispositivo esta em estado off-line.

**Atores:** Usuario.

**Requisitos Funcionais**
- **RF01**: O aplicativo deve disponibilizar um formulario de integração onde o usuario preenchera os seguintes dados:
  - *RF01.1*: Sua turma, curso, semestre atual, data de inicio do semestre, data de encerramento do semestre;
  - *RF01.2*: Alem disso o formulario deve ser informado qual classes/disciplinas o usuario esta participando esse semestre e os dias da semana na qual ocorrem;
- **RF02**: A partir dos dados coletados no formulario de integração, o calendario deve marcar todas as aulas desde do inicio do semsetre ate o fim.
- **RF03**: O calendário mostrará datas e eventos marcados pertinentes apenas ao seu curso e turma, e esses dados devem estar sempre disponivel mesmo quando o dispositivo esta em estado off-line.
- **RF04**: O usuario deve ser capaz de definir outros eventos alem do curriculo gerado pelo formulario de integraçao, eventos personalizados devem ser capazes de armazenar as seguintes informaçoes:
  - *RF04.1*: Nome do evento, descriçao do evento, data de inicio do evento, data de encerramento do evento, Horario de inicio do evento, horario de encerramento do evento, alem de uma cor da lista predefinda para o evento;
  - *RF04.2*: O calendário deve mostrar eventos criados de tal forma com a cor definida em sua criaçao;
- **RF06**: O usuario poderá clicar sobre uma data marcada para ver a descrição e horário de início e horário final do evento marcado.
- **RF05**: O calendário mostrará eventos do inicio do semestre até o encerramento do semestre, a partir do qual ele reinicia e incita o usuario a preencher um novo formulario de integraçao.

**Requisitos Não Funcionais**
- **RNF01**: O calendário será responsivo.
- **RNF02**: O calendário será intuitivo e de fácil navegação.

**Requisitos Não Funcionais Gerais**
- **RNG01**: O sistema deve ter disponibilidar de 99% em produção.
- **RNG02**: O sistema deve ser compatível com a versões mais recentes dos principais mecanismos de busca.
- **RNG03**: O sistema deve atender ao nível AA da WCAG 2.1 nas principais telas de uso.
- **RNG04**: O sistema deve proteger dados em trânsito com HTTPS e registrar logs de auditoria das operações críticas

**Funcionalidades**
- **F01: Configuração Inicial do Calendário**  
  Descrição: Permite ao usuario preencher um formulário de integração contendo informações acadêmicas do semestre, incluindo turma, curso,      período letivo e disciplinas cursadas com seus respectivos dias da semana.  
  Requisitos Associados: **RF01, RF01.1, RF01.2.**
- **F02: Geração Automática de Aulas**  
  Descrição: Permite ao sistema gerar automaticamente no calendário todas as aulas do semestre com base nas informações fornecidas no           formulário de integração.  
  Requisitos Associados: **RF02.**
- **F03: Visualização de Eventos Acadêmicos Off-line**  
  Descrição: Permite ao usuario visualizar eventos e datas relacionadas ao seu curso e turma mesmo quando o dispositivo estiver sem acesso à    internet.  
  Requisitos Associados: **RF03.**
- **F04: Criação de Eventos Personalizados**
  Descrição: Permite ao usuario cadastrar eventos personalizados contendo nome, descrição, datas, horários e cor de identificação no            calendário.  
  Requisitos Associados: **RF04, RF04.1.**
- **F04: Categorizaçao Visual de Eventos**  
  Descrição: Permite ao calendário exibir eventos personalizados utilizando a cor definida durante sua criação.  
  Requisitos Associados: **RF04.2.**
- **F05: Consulta de Detalhes de Eventos**  
  Descrição: Permite ao usuario clicar sobre uma data marcada para visualizar descrição, horário de início e horário de encerramento do         evento.  
  Requisitos Associados: **RF06.**
- **F06: Reinicialização de Semestre**  
  Descrição: Permite ao sistema encerrar o calendário do semestre atual e solicitar um novo formulário de integração ao final do período        letivo.  
  Requisitos Associados: **RF05.**

**Acessibilidade** 

- **WCAG 2.1 AA:** padrão internacional de acessibilidade para conteúdo web.
- **Contraste:** textos e componentes visuais devem ter contraste suficiente para leitura.
- **Navegação por teclado/mouse:** todas as funções principais devem ser utilizáveis sem mouse.
- **Mensagens de erro:** devem explicar o problema e orientar correção.
- **Leitores de tela:** estrutura e elementos devem ser compatíveis com tecnologias assistivas.
- **Zoom e responsividade:** conteúdo deve permanecer legível e funcional em ampliação e telas menores


**Definition of Done (DoD)**
- O aluno poderá consultar os detalhes do evento ao clicar sobre a data marcada.






