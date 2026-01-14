export COMPOSE_BAKE=true
DB_URL=postgresql://postgres:postgres@localhost:5432/zlagoda?sslmode=disable
.PHONY: up down test migrate-all-up migrate-all-down create-migration

up:
	docker compose up --build

down:
	docker compose down

test:
	curl http://localhost:8080
	curl http://localhost:8081

migrate-all-up:
	migrate -path migrations -database "${DB_URL}" up

migrate-all-down:
	migrate -path migrations -database "${DB_URL}" down

create-migration:
	migrate create -ext sql -dir migrations -seq $(name)