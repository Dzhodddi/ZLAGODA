package app

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/auth"
	"github.com/Dzhodddi/ZLAGODA/internal/db"

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
func Setup() *Server {
	return &Server{
		Echo: echo.New(),
	}
}

func (s *Server) SetConfig(cfg *config.Config) *Server {
	s.Config = cfg
	return s
}

func (s *Server) SetDB(db *sqlx.DB) *Server {
	s.DB = db
	return s
}

func (s *Server) SetAuth(authenticator auth.Authenticator) *Server {
	s.authenticator = authenticator
	return s
}

func (s *Server) SetupMiddlewares() *Server {
	s.Echo.GET("/swagger/*", swaggerDocs.WrapHandler)
	s.Echo.HTTPErrorHandler = errorResponse.GlobalHTTPErrorHandler(s.Config.Env)
	s.Echo.Use(echoMiddleware.RequestLogger())
	s.Echo.Use(echoMiddleware.Recover())
	s.Echo.Debug = true
	return s
}

func (s *Server) SetupAllRoutes() *Server {
	v1 := s.Echo.Group("/api/v1", s.authenticator.AuthMiddleware(repository.NewEmployeeRepository(s.DB)))
	v1.GET("/health", func(c echo.Context) error {
		return c.JSON(http.StatusOK, "ok")
	})
	s.setupCategoryRouts(v1)
	s.setupCustomerCardRouts(v1)
	s.setupChecksRouts(v1)
	s.setupSaleRouts(v1)
	return s
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

func GetDatabase(cfg *config.Config) (*sqlx.DB, error) {
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

func GetAuth(cfg *config.Config) auth.Authenticator {
	return auth.NewJWTAuth(cfg)
}
