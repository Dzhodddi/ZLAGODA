package main

import (
	"log"

	"github.com/Dzhodddi/ZLAGODA/internal/auth"
	"github.com/Dzhodddi/ZLAGODA/internal/config"
	app "github.com/Dzhodddi/ZLAGODA/internal/server"
	"github.com/jmoiron/sqlx"
)

func main() {
	cfg, db, authenticator, err := getAllDependencies()
	if err != nil {
		panic(err)
	}
	server := app.Setup().
		SetConfig(cfg).
		SetDB(db).
		SetAuth(authenticator).
		SetupMiddlewares().
		SetupAllRoutes()
	if err = server.Start(":" + server.Config.Port); err != nil {
		log.Fatal(err)
	}
}

func getAllDependencies() (*config.Config, *sqlx.DB, auth.Authenticator, error) {
	cfg, err := config.Load()
	if err != nil {
		panic(err)
	}
	db, err := app.GetDatabase(cfg)
	if err != nil {
		panic(err)
	}
	return cfg, db, auth.NewJWTAuth(cfg), err
}
