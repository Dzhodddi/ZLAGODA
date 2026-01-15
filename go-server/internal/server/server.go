package app

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/Dzhodddi/ZLAGODA/internal/db"
	"github.com/labstack/echo/v4"
)

type Server struct {
	*echo.Echo
	Config *config.Config
}

func Setup(cfg *config.Config) (*Server, error) {
	e := echo.New()
	dbConfig := db.DatabaseConfig{
		Driver: "postgres",
		DSN:    cfg.PostgresDSN,
		Pool: &db.PoolConfig{
			MaxOpenConnections: cfg.MaxOpenConnections,
			MaxIdleConnections: cfg.MaxIdleConnections,
			ConnMaxLifetime:    cfg.ConnMaxLifetime,
		},
	}
	_, err := db.NewPostgresConnection(dbConfig)
	if err != nil {
		return nil, err
	}
	v1 := e.Group("/v1")

	v1.GET("/health", func(c echo.Context) error {
		return c.JSON(http.StatusOK, "ok")
	})
	return &Server{
		Echo:   e,
		Config: cfg,
	}, nil
}
