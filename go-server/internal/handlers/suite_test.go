package handlers_test

import (
	"net/http"

	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	app "github.com/Dzhodddi/ZLAGODA/internal/server"
	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/labstack/echo/v4"
)

type HandlerSuite struct {
	testutils.IntegrationSuite
	router http.Handler
	Echo   *echo.Echo

	CategoryRepo repository.CategoryRepository
	CardRepo     repository.CardRepository
}

func (s *HandlerSuite) SetupSuite() {
	s.IntegrationSuite.SetupSuite()
	server, err := app.Setup(false)
	s.Require().NoError(err)
	s.Echo = server.Echo
	s.CategoryRepo = repository.NewCategoryRepository(s.DB)
	s.CardRepo = repository.NewCardRepository(s.DB)
}
