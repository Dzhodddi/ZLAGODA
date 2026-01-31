package app

import (
	"net/http"

	_ "github.com/Dzhodddi/ZLAGODA/docs"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/handlers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	"github.com/jmoiron/sqlx"
	echoMiddleware "github.com/labstack/echo/v4/middleware"
	swaggerDocs "github.com/swaggo/echo-swagger"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/labstack/echo/v4"
)

type Server struct {
	*echo.Echo
	Config *config.Config
	DB     *sqlx.DB
}

// @title Go server API
// @version 1.0
// @termsOfService http://swagger.io/terms/
// @host localhost:8080
// @BasePath /api/v1
func Setup(cfg *config.Config, database *sqlx.DB) (*Server, error) {
	e := echo.New()
	setupMiddlewares(e, cfg)
	v1 := e.Group("/api/v1")

	v1.GET("/health", func(c echo.Context) error {
		return c.JSON(http.StatusOK, "ok")
	})
	setupAllRoutes(database, v1)
	return &Server{
		Echo:   e,
		Config: cfg,
		DB:     database,
	}, nil
}

func setupMiddlewares(e *echo.Echo, cfg *config.Config) {
	e.GET("/swagger/*", swaggerDocs.WrapHandler)
	e.HTTPErrorHandler = errorResponse.GlobalHTTPErrorHandler(cfg.Env)
	e.Use(echoMiddleware.RequestLogger())
	e.Use(echoMiddleware.Recover())
	e.Debug = true
}

func setupAllRoutes(db *sqlx.DB, router *echo.Group) {
	setupCategoryRouts(db, router)
	setupCustomerCardRouts(db, router)
	setupChecksRouts(db, router)
}

func setupCategoryRouts(db *sqlx.DB, router *echo.Group) {
	repo := repository.NewCategoryRepository(db)
	service := services.NewCategoryService(repo)
	handler := handlers.NewCategoryHandler(service)
	handler.RegisterRouts(router)
}

func setupCustomerCardRouts(db *sqlx.DB, router *echo.Group) {
	repo := repository.NewCardRepository(db)
	service := services.NewCardService(repo)
	handler := handlers.NewCardHandler(service)
	handler.RegisterRouts(router)
}

func setupChecksRouts(db *sqlx.DB, router *echo.Group) {
	repo := repository.NewCheckRepository(db)
	service := services.NewCheckService(repo)
	handler := handlers.NewCheckHandler(service)
	handler.RegisterRouts(router)
}
