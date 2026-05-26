#!/usr/bin/env bash
#
# Roda a collection do Postman via Newman em container.
# Fluxo esperado da collection (em ordem):
#   1. Health        - valida /actuator/health dos 3 servicos
#   2. Auth          - faz login dos 3 perfis e popula token_medico, token_enfermeiro,
#                      token_paciente como collection variables (tambem cobre cadastro
#                      de novo usuario e credenciais invalidas)
#   3. Scheduling    - usa os tokens em Authorization: Bearer
#   4. History GraphQL - usa os tokens em Authorization: Bearer
#   5. Negative Tests  - valida 401/403
#
# Variaveis de ambiente que sao injetadas no Newman (sobrescrevem o environment do Postman):
#   BASE_URL_SCHEDULING, BASE_URL_SCHEDULING_ROOT, BASE_URL_HISTORY, BASE_URL_NOTIFICATION
#   MEDICO_USERNAME, MEDICO_PASSWORD
#   ENFERMEIRO_USERNAME, ENFERMEIRO_PASSWORD
#   PACIENTE_USERNAME, PACIENTE_PASSWORD
#   MEDICO_ID, PACIENTE_ID, OUTRO_PACIENTE_ID
#
# Esses valores sao lidos automaticamente do .env na raiz do projeto.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COLLECTION="$ROOT_DIR/postman/tech-challenge-fase-3.postman_collection.json"
ENVIRONMENT="$ROOT_DIR/postman/tech-challenge-fase-3.local.postman_environment.json"
EXTRA_ARGS=()
NETWORK_ARG=()

if [[ -f "$ROOT_DIR/.env" ]]; then
  while IFS='=' read -r key value; do
    [[ -z "$key" || "$key" == \#* ]] && continue
    value="${value%$'\r'}"
    if [[ -z "${!key+x}" ]]; then
      export "$key=$value"
    fi
  done < "$ROOT_DIR/.env"
fi

if [[ ! -f "$COLLECTION" ]]; then
  echo "Colecao nao encontrada em $COLLECTION" >&2
  exit 1
fi

if [[ ! -f "$ENVIRONMENT" ]]; then
  echo "Ambiente nao encontrado em $ENVIRONMENT" >&2
  exit 1
fi

SCHEDULING_CONTAINER="${SCHEDULING_CONTAINER_NAME:-scheduling-service}"
HISTORY_CONTAINER="${HISTORY_CONTAINER_NAME:-history-service}"
NOTIFICATION_CONTAINER="${NOTIFICATION_CONTAINER_NAME:-notification-service}"

if docker inspect "$SCHEDULING_CONTAINER" >/dev/null 2>&1; then
  NETWORK_NAME="$(docker inspect -f '{{range $k,$v := .NetworkSettings.Networks}}{{println $k}}{{end}}' "$SCHEDULING_CONTAINER" | head -n1 | tr -d '[:space:]')"
  if [[ -n "$NETWORK_NAME" ]]; then
    NETWORK_ARG+=(--network "$NETWORK_NAME")
  fi
fi

# URLs base ---------------------------------------------------------------------
if [[ -n "${BASE_URL_SCHEDULING:-}" ]]; then
  EXTRA_ARGS+=(--env-var "base_url_scheduling=${BASE_URL_SCHEDULING}")
  EXTRA_ARGS+=(--env-var "base_url_scheduling_root=${BASE_URL_SCHEDULING_ROOT:-${BASE_URL_SCHEDULING%/api/v1}}")
else
  EXTRA_ARGS+=(--env-var "base_url_scheduling=http://${SCHEDULING_CONTAINER}:8080/api/v1")
  EXTRA_ARGS+=(--env-var "base_url_scheduling_root=http://${SCHEDULING_CONTAINER}:8080")
fi

if [[ -n "${BASE_URL_HISTORY:-}" ]]; then
  EXTRA_ARGS+=(--env-var "base_url_history=${BASE_URL_HISTORY}")
else
  EXTRA_ARGS+=(--env-var "base_url_history=http://${HISTORY_CONTAINER}:8081")
fi

if [[ -n "${BASE_URL_NOTIFICATION:-}" ]]; then
  EXTRA_ARGS+=(--env-var "base_url_notification=${BASE_URL_NOTIFICATION}")
else
  EXTRA_ARGS+=(--env-var "base_url_notification=http://${NOTIFICATION_CONTAINER}:8082")
fi

# Credenciais e IDs dos seed users (vindos do .env, se presentes) ---------------
[[ -n "${MEDICO_USERNAME:-}" ]]      && EXTRA_ARGS+=(--env-var "medico_username=${MEDICO_USERNAME}")
[[ -n "${MEDICO_PASSWORD:-}" ]]      && EXTRA_ARGS+=(--env-var "medico_password=${MEDICO_PASSWORD}")
[[ -n "${ENFERMEIRO_USERNAME:-}" ]]  && EXTRA_ARGS+=(--env-var "enfermeiro_username=${ENFERMEIRO_USERNAME}")
[[ -n "${ENFERMEIRO_PASSWORD:-}" ]]  && EXTRA_ARGS+=(--env-var "enfermeiro_password=${ENFERMEIRO_PASSWORD}")
[[ -n "${PACIENTE_USERNAME:-}" ]]    && EXTRA_ARGS+=(--env-var "paciente_username=${PACIENTE_USERNAME}")
[[ -n "${PACIENTE_PASSWORD:-}" ]]    && EXTRA_ARGS+=(--env-var "paciente_password=${PACIENTE_PASSWORD}")
[[ -n "${MEDICO_ID:-}" ]]            && EXTRA_ARGS+=(--env-var "medico_id=${MEDICO_ID}")
[[ -n "${PACIENTE_ID:-}" ]]          && EXTRA_ARGS+=(--env-var "paciente_id=${PACIENTE_ID}")
[[ -n "${OUTRO_PACIENTE_ID:-}" ]]    && EXTRA_ARGS+=(--env-var "outro_paciente_id=${OUTRO_PACIENTE_ID}")

PLATFORM_ARG=()
if [[ -n "${NEWMAN_PLATFORM:-}" ]]; then
  PLATFORM_ARG+=(--platform "${NEWMAN_PLATFORM}")
fi

echo "==> Executando Newman (collection completa em ordem: Health -> Auth -> Scheduling -> History GraphQL -> Negative Tests)"

docker run --rm ${PLATFORM_ARG+"${PLATFORM_ARG[@]}"} ${NETWORK_ARG+"${NETWORK_ARG[@]}"} \
  -v "$ROOT_DIR:/etc/newman" postman/newman:5-alpine \
  run /etc/newman/postman/tech-challenge-fase-3.postman_collection.json \
  -e /etc/newman/postman/tech-challenge-fase-3.local.postman_environment.json \
  --reporters cli \
  "$@" \
  ${EXTRA_ARGS+"${EXTRA_ARGS[@]}"}
