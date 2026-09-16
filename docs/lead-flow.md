# Fluxo de Leads

## Conceito

Um Lead representa uma oportunidade comercial específica.

Um Lead não representa necessariamente uma pessoa nova ou um paciente novo.

Uma mesma pessoa poderá possuir múltiplos Leads ao longo do tempo,
representando interesses ou oportunidades comerciais diferentes.

## Status

- NEW
- CONTACTED
- WAITING_CLIENT
- WAITING_SCHEDULE
- SCHEDULED
- RESCHEDULE_REQUIRED
- COMPLETED
- LOST

## Transições permitidas

### NEW

Pode avançar para:

- CONTACTED
- SCHEDULED
- LOST

### CONTACTED

Pode avançar para:

- WAITING_CLIENT
- WAITING_SCHEDULE
- SCHEDULED
- LOST

### WAITING_CLIENT

Pode avançar para:

- CONTACTED
- WAITING_SCHEDULE
- SCHEDULED
- LOST

### WAITING_SCHEDULE

Pode avançar para:

- CONTACTED
- WAITING_CLIENT
- SCHEDULED
- LOST

### SCHEDULED

Pode avançar para:

- COMPLETED
- RESCHEDULE_REQUIRED
- LOST

### RESCHEDULE_REQUIRED

Pode avançar para:

- SCHEDULED
- LOST

## Estados terminais

Os estados:

- COMPLETED
- LOST

são considerados terminais.

Um Lead nesses estados não deve ser reaberto.

Caso a mesma pessoa demonstre um novo interesse posteriormente,
uma nova oportunidade deverá ser registrada através de um novo Lead.

## Exemplo

Uma cliente entra em contato em setembro interessada em Pilates.

Lead #1:

- serviço: Pilates
- status final: LOST

Em novembro, a mesma cliente entra novamente em contato.

O Lead #1 permanece encerrado.

Um novo Lead é criado:

Lead #2:

- serviço: Pilates
- status inicial: NEW

Essa abordagem preserva corretamente o histórico e as métricas
de oportunidades comerciais.

## Transição direta NEW -> SCHEDULED

A transição de NEW diretamente para SCHEDULED é permitida.

Isso cobre cenários em que o primeiro contato com o cliente já resulta
em um agendamento, evitando transições artificiais apenas para cumprir
o fluxo interno.

A interação inicial ainda deverá ser registrada no histórico.

## Regras relacionadas à próxima ação

Os seguintes status exigem uma próxima ação programada:

- WAITING_CLIENT
- WAITING_SCHEDULE
- RESCHEDULE_REQUIRED

Esses estados não podem ser atribuídos sem uma `nextActionAt`.

## Princípio

O fluxo deve representar o estado real da oportunidade comercial,
sem criar etapas artificiais apenas por conveniência técnica.

Nenhum Lead ativo que dependa de acompanhamento deve permanecer
sem uma próxima ação definida.


## Visão simplificada do fluxo

```text
NEW
|
v
CONTACTED
|
+------------------------------+
|                              |
v                              v
WAITING_CLIENT           WAITING_SCHEDULE
|                              |
|                              |
+-------------+----------------+ 
              |
              v
          SCHEDULED
              |
         +----+----+
         |         |
         v         v
     COMPLETED  RESCHEDULE_REQUIRED
                   |
                   v
               SCHEDULED