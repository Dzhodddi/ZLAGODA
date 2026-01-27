.PHONY: test-all test-unit test-integration

test-unit:
	@echo "Running Unit Tests..."
	mvn test -DskipITs

test-integration:
	@echo "Running Integration Tests..."
	mvn verify -DskipUnitTests

test-all: test-unit test-integration
