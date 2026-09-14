# Regras de Domínio

## Lead

Um Lead representa uma oportunidade comercial.

Um Lead não representa necessariamente um paciente novo.

## Próxima ação

Leads em:

- WAITING_CLIENT
- WAITING_SCHEDULE
- RESCHEDULE_REQUIRED

devem possuir uma próxima ação programada.

## Tarefas vencidas

Uma próxima ação vencida não altera automaticamente o status do lead.

O atraso deverá ser calculado a partir de `nextActionAt`.

## Interações

Toda tentativa relevante de contato deverá gerar uma interação.

Cada interação possui:

- tipo;
- resultado;
- observação opcional;
- responsável;
- data e hora.

## Status e resultado

Status representa o estado atual da oportunidade.

Outcome representa o resultado de uma interação específica.

São conceitos distintos.