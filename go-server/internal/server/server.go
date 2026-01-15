package app

import (
	_ "github.com/Dzhodddi/ZLAGODA/docs"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/handlers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	"github.com/jmoiron/sqlx"
	echoMiddleware "github.com/labstack/echo/v4/middleware"
	swaggerDocs "github.com/swaggo/echo-swagger"
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/Dzhodddi/ZLAGODA/internal/db"
	"github.com/labstack/echo/v4"
)

type Server struct {
	*echo.Echo
	Config *config.Config
}

// @title Go server API
// @version 1.0
// @termsOfService http://swagger.io/terms/
// @host localhost:8080
// @BasePath /api/v1
func Setup(cfg *config.Config) (*Server, error) {
	e := echo.New()
	setupMiddlewares(e, cfg)
	database, err := setupDatabase(cfg)
	if err != nil {
		return nil, err
	}
	v1 := e.Group("/api/v1")

	v1.GET("/health", func(c echo.Context) error {
		return c.JSON(http.StatusOK, "ok")
	})
	setupAllRoutes(database, v1)
	return &Server{
		Echo:   e,
		Config: cfg,
	}, nil
}

func setupMiddlewares(e *echo.Echo, cfg *config.Config) {
	e.GET("/swagger/*", swaggerDocs.WrapHandler)
	e.HTTPErrorHandler = errorResponse.GlobalHTTPErrorHandler(cfg.Env)
	e.Use(echoMiddleware.RequestLogger())
	e.Debug = true
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

func setupAllRoutes(db *sqlx.DB, router *echo.Group) {
	setupCategoryRouts(db, router)
	setupCustomerCardRouts(db, router)
}

func setupCategoryRouts(db *sqlx.DB, router *echo.Group) {
	repo := repository.NewCategoryRepository(db)
	service := services.NewCategoryService(repo)
	handler := handlers.NewCategoryHandler(*service)
	handler.RegisterRouts(router)
}

func setupCustomerCardRouts(db *sqlx.DB, router *echo.Group) {
	repo := repository.NewCardRepository(db)
	service := services.NewCardService(repo)
	handler := handlers.NewCardHandler(*service)
	handler.RegisterRouts(router)
}
