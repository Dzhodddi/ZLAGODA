package main

import (
	app "github.com/Dzhodddi/ZLAGODA/internal/server"
	"log"
)

func main() {
	server, err := app.Setup(true)
	if err != nil {
		panic(err)
	}
	if err = server.Start(":" + server.Config.Port); err != nil {
		log.Fatal(err)
	}
}
