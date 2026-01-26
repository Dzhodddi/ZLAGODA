.PHONY: gen-docs sqlc-generate

gen-docs:
	swag init -g internal/server/server.go

sqlc-generate:
	sqlc generate

test-unit:
	@echo "Running Unit Tests..."
	go test -v -short -race ./...

test-integration:
	@echo "Running Integration Tests..."
	go test -v -race -tags=integration ./internal/repositories/...

test-all: test-unit test-integration