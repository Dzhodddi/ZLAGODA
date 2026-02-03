#!/bin/bash
set -e

TEST_TARGET=${1:-test-all}
DB_CONTAINER=zlagoda-db-test
NETWORK=zlagoda-test-net

cleanup() {
  echo "Cleaning up..."
  docker rm -f $DB_CONTAINER >/dev/null 2>&1 || true
  docker network rm $NETWORK >/dev/null 2>&1 || true
}
trap cleanup EXIT

docker network create $NETWORK >/dev/null 2>&1 || true

docker run -d \
  --name $DB_CONTAINER \
  --network $NETWORK \
  -e POSTGRES_DB=zlagoda_test \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:17.6

echo "Database started, waiting for it to be ready..."
sleep 5

docker build \
  --target test \
  -t go-server-test \
  -f go-server/deployment/Dockerfile \
  .

docker build \
  --target test \
  -t java-server-test \
  -f java-server/docker/Dockerfile \
  java-server

echo "Running go tests..."
docker run --rm -it \
  --network $NETWORK \
  -v "$(pwd)/coverage:/coverage" \
  -e DB_HOST=$DB_CONTAINER \
  go-server-test $TEST_TARGET

echo "Running java tests..."
docker run --rm -it \
  --network $NETWORK \
  -e DB_HOST=$DB_CONTAINER \
  java-server-test $TEST_TARGET
