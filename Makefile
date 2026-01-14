export COMPOSE_BAKE=true
.PHONY: up down test

up:
	docker compose up --build

down:
	docker compose down

test:
	curl http://localhost:8080