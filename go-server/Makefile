.PHONY: gen-docs sqlc-generate

gen-docs:
	swag init -g internal/server/server.go

sqlc-generate:
	sqlc generate

test-unit:
	@echo "Running Unit Tests..."
	go test -short -race ./...

test-integration:
	@echo "Running Integration Tests..."
	go test -race -tags=integration ./internal/...

test-all: test-unit test-integration