# Escopo da V1

## Objetivo

Evitar que potenciais clientes sejam esquecidos durante o processo de
atendimento e fornecer uma visão simples das oportunidades comerciais
da clínica.

A V1 deve garantir que todo lead ativo possua uma situação clara e,
quando necessário, uma próxima ação definida.

## Funcionalidades

### Landing Page

- Apresentação da clínica
- Principais serviços
- Diferenciais
- CTA para contato
- Formulário de interesse

### Gestão de Leads

Cada lead deverá possuir:

- nome
- WhatsApp
- serviço principal de interesse
- origem
- status
- responsável
- data de criação
- data do último contato
- próxima ação (`nextActionAt`)
- observação comercial

O lead representa uma oportunidade comercial e não necessariamente
um paciente novo.

### Origem do Lead

A origem deverá ser estruturada para permitir métricas futuras.

Origens iniciais:

- INSTAGRAM
- LANDING_PAGE
- GOOGLE
- REFERRAL
- WHATSAPP
- RETURNING_CLIENT
- OTHER

### Status iniciais

- NEW
- CONTACTED
- WAITING_CLIENT
- WAITING_SCHEDULE
- SCHEDULED
- RESCHEDULE_REQUIRED
- COMPLETED
- LOST

O status representa o estado atual da oportunidade comercial.

### Histórico de Interações

As interações realizadas com um lead deverão ser registradas
individualmente para preservar o histórico de acompanhamento.

Cada interação poderá possuir:

- tipo
- resultado
- observação
- data e hora
- responsável

Tipos iniciais:

- INITIAL_CONTACT
- FOLLOW_UP
- RESCHEDULE_ATTEMPT

Resultados iniciais:

- NO_RESPONSE
- SCHEDULED
- WANTS_LATER
- NO_AVAILABILITY
- WANTS_RESCHEDULE
- NOT_INTERESTED
- OTHER

O resultado de uma interação não deve ser confundido com o status atual
do lead.

Exemplo:

Lead:
- status: WAITING_CLIENT

Interação:
- tipo: FOLLOW_UP
- resultado: NO_RESPONSE

O lead poderá continuar em WAITING_CLIENT mesmo após uma tentativa
sem resposta.

### Motivos de Perda

Quando uma oportunidade for encerrada como LOST, deverá ser possível
registrar um motivo estruturado.

Motivos iniciais:

- PRICE
- NO_AVAILABILITY
- NO_LONGER_INTERESTED
- CHOSE_COMPETITOR
- NO_RESPONSE
- OTHER

Uma observação adicional poderá ser registrada quando necessário.

### Follow-up e Próximas Ações

Leads que ainda possuem potencial comercial não devem desaparecer
do processo.

O sistema deverá permitir identificar:

- leads sem retorno;
- clientes que não conseguiram horário;
- cancelamentos que ainda necessitam de remarcação;
- pessoas aguardando novo contato;
- próximas ações programadas;
- ações atrasadas.

### Regra: próxima ação obrigatória

Leads nos status:

- WAITING_CLIENT
- WAITING_SCHEDULE
- RESCHEDULE_REQUIRED

devem possuir uma próxima ação programada.

O sistema não deve permitir a alteração para esses estados sem que
uma data de acompanhamento (`nextActionAt`) seja definida.

Para WAITING_CLIENT, a interface poderá sugerir 5 dias como padrão,
permitindo alteração pelo usuário.

### Regra: resultado de follow-up

Sempre que um follow-up for realizado, seu resultado deverá ser registrado.

Exemplos:

NO_RESPONSE:
- mantém a oportunidade ativa;
- exige definição de uma nova próxima ação.

WANTS_LATER:
- status passa ou permanece como WAITING_CLIENT;
- exige uma nova próxima ação.

NO_AVAILABILITY:
- status passa para WAITING_SCHEDULE;
- exige uma nova próxima ação.

WANTS_RESCHEDULE:
- status passa para RESCHEDULE_REQUIRED;
- exige uma nova próxima ação.

SCHEDULED:
- status passa para SCHEDULED;
- não exige follow-up comercial imediato.

NOT_INTERESTED:
- status passa para LOST;
- próxima ação deixa de ser necessária;
- motivo de perda deverá ser registrado.

### Regra: tarefas atrasadas

Quando uma próxima ação ultrapassar sua data programada sem ser concluída,
ela deverá permanecer ativa e ser apresentada visualmente como atrasada.

Atraso não representa um novo status do lead.

Exemplo:

- status: WAITING_CLIENT
- nextActionAt: 12/09/2026
- data atual: 14/09/2026
- situação da ação: atrasada há 2 dias

A pendência não deve ser removida ou reagendada automaticamente.

O responsável deverá:

- registrar uma interação;
- concluir a ação;
- ou definir uma nova data.

Pendências atrasadas deverão possuir maior destaque no dashboard.

A aplicação poderá enviar notificações resumidas ao responsável,
evitando excesso de notificações.

### Dashboard Inicial

O dashboard deverá priorizar ações que necessitam de atenção.

Exemplos:

- novos leads ainda não atendidos;
- follow-ups de hoje;
- ações atrasadas;
- clientes aguardando horário;
- clientes que precisam remarcar;
- próximas ações programadas.

### Métricas iniciais

- quantidade total de leads;
- leads por origem;
- leads por serviço;
- leads por status;
- quantidade de agendamentos;
- conversão de lead para agendamento;
- quantidade de follow-ups realizados;
- agendamentos obtidos após follow-up;
- leads perdidos;
- motivos de perda;
- comparecimentos.

## Princípio da V1

Todo lead ativo deve possuir uma situação clara e, quando necessário,
uma próxima ação definida.

Nenhuma oportunidade comercial deve permanecer invisível ou depender
exclusivamente da memória da equipe.

## Fora da V1

- prontuário;
- dados médicos;
- financeiro completo;
- caixa completo;
- estoque;
- venda de produtos;
- agenda completa;
- grupo VIP;
- IA;
- automações de marketing complexas.

Esses itens poderão entrar em versões futuras.