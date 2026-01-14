export COMPOSE_BAKE=true
.PHONY: up down test-go test-java

up:
	docker compose up --build

down:
	docker compose down

test:
	curl http://localhost:8080

test-java:
  curl http://localhost:8081
