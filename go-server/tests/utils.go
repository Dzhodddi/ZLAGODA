package testutils

import (
	"errors"
	"fmt"
	"os"
	"path/filepath"
	"runtime"
	"strings"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
	"github.com/stretchr/testify/suite"
)

type IntegrationSuite struct {
	suite.Suite
	DB *sqlx.DB
}

func (s *IntegrationSuite) SetupSuite() {
	dbHost := os.Getenv("DB_HOST")
	if dbHost == "" {
		dbHost = "localhost"
	}

	dsn := fmt.Sprintf(
		"postgres://postgres:postgres@%s:5432/zlagoda_test?sslmode=disable",
		dbHost,
	)
	var err error
	s.DB, err = sqlx.Connect("postgres", dsn)
	s.Require().NoError(err, "Could not connect to test DB")

	_, filename, _, _ := runtime.Caller(0)
	projectRoot := filepath.Join(filepath.Dir(filename), "..")
	migrationPath := "file://" + filepath.Join(projectRoot, "migrations")
	driver, _ := postgres.WithInstance(s.DB.DB, &postgres.Config{})
	m, err := migrate.NewWithDatabaseInstance(migrationPath, "postgres", driver)
	s.Require().NoError(err, "Could not create migrator")

	err = m.Up()
	if err != nil && !errors.Is(err, migrate.ErrNoChange) {
		s.Require().NoError(err, "Migration failed")
	}
}

func (s *IntegrationSuite) TearDownTest() {
	s.TruncateAllTables()
}

func (s *IntegrationSuite) TearDownSuite() {
	if s.DB != nil {
		_ = s.DB.Close()
	}
}

func (s *IntegrationSuite) TruncateAllTables() {
	query := `
        SELECT tablename 
        FROM pg_tables 
        WHERE schemaname = 'public' 
        AND tablename NOT IN ('schema_migrations')
    `
	var tables []string
	err := s.DB.Select(&tables, query)
	s.Require().NoError(err)

	if len(tables) > 0 {
		quoted := make([]string, len(tables))
		for i, t := range tables {
			quoted[i] = fmt.Sprintf("%q", t)
		}
		q := fmt.Sprintf("TRUNCATE TABLE %s RESTART IDENTITY CASCADE", strings.Join(quoted, ", "))
		_, err = s.DB.Exec(q)
		s.Require().NoError(err)
	}
}
