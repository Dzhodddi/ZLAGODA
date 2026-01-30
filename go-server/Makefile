.PHONY: gen-docs sqlc-generate
COVERAGE_DIR ?= /coverage
gen-docs:
	swag init -g internal/server/server.go

sqlc-generate:
	sqlc generate

test-unit:
	@echo "Running Unit Tests..."
	go test -cover -short -race ./...

test-integration:
	@echo "Running Integration Tests..."
	go test -race -tags=integration \
		-count=1 \
		-parallel=1 \
		-covermode=atomic \
		-coverpkg=./internal/repositories/...,./internal/handlers/... \
		-coverprofile=$(COVERAGE_DIR)/coverage.integration.out \
		./internal/repositories/... \
		./internal/handlers/...

test-all: test-unit test-integration