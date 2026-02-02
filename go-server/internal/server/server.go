package app

import (
	"github.com/Dzhodddi/ZLAGODA/internal/auth"
	"github.com/Dzhodddi/ZLAGODA/internal/db"
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
func Setup(withConfig bool) (*Server, error) {
	e := echo.New()
	s := &Server{
		Echo: e,
	}
	if !withConfig {
		return s, nil
	}
	err := s.setConfig()
	if err != nil {
		return nil, err
	}
	// make sure to set up config first!
	err = s.setDB()
	if err != nil {
		return nil, err
	}
	s.setAuth()

	v1 := e.Group("/api/v1", s.authenticator.AuthMiddleware(repository.NewEmployeeRepository(s.DB)))
	v1.GET("/health", func(c echo.Context) error {
		return c.JSON(http.StatusOK, "ok")
	})
	s.setupMiddlewares()
	s.setupAllRoutes(v1)
	return s, nil
}

func (s *Server) setConfig() error {
	cfg, err := config.Load()
	if err != nil {
		return err
	}
	s.Config = cfg
	return nil
}

func (s *Server) setDB() error {
	database, err := getDatabase(s.Config)
	if err != nil {
		return err
	}
	s.DB = database
	return nil
}

func (s *Server) setAuth() {
	authenticator := auth.NewJWTAuth(s.Config)
	s.authenticator = authenticator
}

func (s *Server) setupMiddlewares() {
	s.Echo.GET("/swagger/*", swaggerDocs.WrapHandler)
	s.Echo.HTTPErrorHandler = errorResponse.GlobalHTTPErrorHandler(s.Config.Env)
	s.Echo.Use(echoMiddleware.RequestLogger())
	s.Echo.Use(echoMiddleware.Recover())
	s.Echo.Debug = true
}

func (s *Server) setupAllRoutes(router *echo.Group) {
	s.setupCategoryRouts(router)
	s.setupCustomerCardRouts(router)
	s.setupChecksRouts(router)
	s.setupSaleRouts(router)
}

func (s *Server) setupCategoryRouts(router *echo.Group) {
	repo := repository.NewCategoryRepository(s.DB)
	service := services.NewCategoryService(repo)
	handler := handlers.NewCategoryHandler(service, s.authenticator)
	handler.RegisterRouts(router)
}

func (s *Server) setupCustomerCardRouts(router *echo.Group) {
	repo := repository.NewCardRepository(s.DB)
	service := services.NewCardService(repo)
	handler := handlers.NewCardHandler(service, s.authenticator)
	handler.RegisterRouts(router)
}

func (s *Server) setupChecksRouts(router *echo.Group) {
	repo := repository.NewCheckRepository(s.DB)
	service := services.NewCheckService(repo)
	handler := handlers.NewCheckHandler(service, s.authenticator)
	handler.RegisterRouts(router)
}

func (s *Server) setupSaleRouts(router *echo.Group) {
	repo := repository.NewSaleRepository(s.DB)
	service := services.NewSaleService(repo)
	handler := handlers.NewSaleHandler(service, s.authenticator)
	handler.RegisterRouts(router)
}

func getDatabase(cfg *config.Config) (*sqlx.DB, error) {
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
