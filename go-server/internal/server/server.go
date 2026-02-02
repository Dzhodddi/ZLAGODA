package app

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/auth"

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
	Config        *config.Config
	DB            *sqlx.DB
	authenticator auth.Authenticator
}

// Setup @title Go server API
// @version 1.0
// @termsOfService http://swagger.io/terms/
// @host localhost:8080
// @BasePath /api/v1
// @securityDefinitions.apikey ApiKeyAuth
// @in							header
// @name						Authorization
func Setup(cfg *config.Config, database *sqlx.DB) (*Server, error) {
	e := echo.New()
	jwtAuth := auth.NewJWTAuth(cfg)
	v1 := e.Group("/api/v1", jwtAuth.AuthMiddleware(repository.NewEmployeeRepository(database)))
	setupMiddlewares(e, cfg)
	v1.GET("/health", func(c echo.Context) error {
		return c.JSON(http.StatusOK, "ok")
	})
	setupAllRoutes(database, v1, jwtAuth)
	return &Server{
		Echo:          e,
		Config:        cfg,
		DB:            database,
		authenticator: jwtAuth,
	}, nil
}

func setupMiddlewares(e *echo.Echo, cfg *config.Config) {
	e.GET("/swagger/*", swaggerDocs.WrapHandler)
	e.HTTPErrorHandler = errorResponse.GlobalHTTPErrorHandler(cfg.Env)
	e.Use(echoMiddleware.RequestLogger())
	e.Use(echoMiddleware.Recover())
	e.Debug = true
}

func setupAllRoutes(db *sqlx.DB, router *echo.Group, auth auth.Authenticator) {
	setupCategoryRouts(db, router, auth)
	setupCustomerCardRouts(db, router, auth)
	setupChecksRouts(db, router, auth)
	setupSaleRouts(db, router, auth)
}

func setupCategoryRouts(db *sqlx.DB, router *echo.Group, auth auth.Authenticator) {
	repo := repository.NewCategoryRepository(db)
	service := services.NewCategoryService(repo)
	handler := handlers.NewCategoryHandler(service, auth)
	handler.RegisterRouts(router)
}

func setupCustomerCardRouts(db *sqlx.DB, router *echo.Group, auth auth.Authenticator) {
	repo := repository.NewCardRepository(db)
	service := services.NewCardService(repo)
	handler := handlers.NewCardHandler(service, auth)
	handler.RegisterRouts(router)
}

func setupChecksRouts(db *sqlx.DB, router *echo.Group, auth auth.Authenticator) {
	repo := repository.NewCheckRepository(db)
	service := services.NewCheckService(repo)
	handler := handlers.NewCheckHandler(service, auth)
	handler.RegisterRouts(router)
}

func setupSaleRouts(db *sqlx.DB, router *echo.Group, auth auth.Authenticator) {
	repo := repository.NewSaleRepository(db)
	service := services.NewSaleService(repo)
	handler := handlers.NewSaleHandler(service, auth)
	handler.RegisterRouts(router)
}
