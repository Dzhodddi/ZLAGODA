package main

import (
	"log"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/Dzhodddi/ZLAGODA/internal/db"
	app "github.com/Dzhodddi/ZLAGODA/internal/server"
	"github.com/jmoiron/sqlx"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		panic(err)
	}
	database, err := setupDatabase(cfg)
	if err != nil {
		panic(err)
	}
	server, err := app.Setup(cfg, database)
	if err != nil {
		panic(err)
	}

	if err = server.Start(":" + cfg.Port); err != nil {
		log.Fatal(err)
	}
}

func setupDatabase(cfg *config.Config) (*sqlx.DB, error) {
	dbConfig := db.DatabaseConfig{
		Driver: "postgres",
		DSN:    cfg.PostgresDSN,
		Pool: &db.PoolConfig{
			MaxOpenConnections: cfg.MaxOpenConnections,
			MaxIdleConnections: cfg.MaxIdleConnections,
			ConnMaxLifetime:    cfg.ConnMaxLifetime,
		},
	}
	return db.NewPostgresConnection(dbConfig)
}
