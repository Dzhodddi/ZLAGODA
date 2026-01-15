package main

import (
	"log"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	app "github.com/Dzhodddi/ZLAGODA/internal/server"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		panic(err)
	}

	server, err := app.Setup(cfg)
	if err != nil {
		panic(err)
	}

	if err = server.Start(":" + cfg.Port); err != nil {
		log.Fatal(err)
	}
}
