//go:build integration

package repository

import (
	"context"
	"testing"

	"github.com/Dzhodddi/ZLAGODA/internal/views"
	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/stretchr/testify/suite"
)

type CardRepositorySuite struct {
	testutils.IntegrationSuite
	repo CardRepository
}

func (s *CardRepositorySuite) SetupSuite() {
	s.SetupDatabase()
	s.repo = NewCardRepository(s.DB)
}

func (s *CardRepositorySuite) TestCreateNewCard() {
	ctx := context.Background()
	newCard := views.CreateNewCustomerCard{}

	_, err := s.repo.CreateNewCard(ctx, newCard)
	s.Require().NoError(err)
}

func TestCardRepositorySuite(t *testing.T) {
	suite.Run(t, new(CardRepositorySuite))
}
