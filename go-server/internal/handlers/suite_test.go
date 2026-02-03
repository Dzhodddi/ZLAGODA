package handlers_test

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	app "github.com/Dzhodddi/ZLAGODA/internal/server"
	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/Dzhodddi/ZLAGODA/tests/mocks"
)

type HandlerSuite struct {
	testutils.IntegrationSuite
	router http.Handler
	Server *app.Server

	CategoryRepo repository.CategoryRepository
	CardRepo     repository.CardRepository
}

func (s *HandlerSuite) SetupSuite() {
	s.IntegrationSuite.SetupSuite()
	mockAuth := mocks.NewMockAuth()

	s.Server = app.Setup().
		SetConfig(&config.Config{
			Env: config.Test,
		}).
		SetDB(s.DB).
		SetAuth(mockAuth).
		SetupAllRoutes().
		SetupMiddlewares()
	s.CategoryRepo = repository.NewCategoryRepository(s.DB)
	s.CardRepo = repository.NewCardRepository(s.DB)
}
