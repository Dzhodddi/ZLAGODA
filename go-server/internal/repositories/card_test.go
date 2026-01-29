// // //go:build integration
package repository_test

//
//import (
//	"context"
//	"testing"
//
//	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
//	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
//	"github.com/Dzhodddi/ZLAGODA/internal/views"
//	testutils "github.com/Dzhodddi/ZLAGODA/tests"
//	"github.com/stretchr/testify/assert"
//	"github.com/stretchr/testify/suite"
//)
//
//type CardRepositorySuite struct {
//	testutils.IntegrationSuite
//	repo repository.CardRepository
//}
//
//func (s *CardRepositorySuite) SetupTest() {
//	s.repo = repository.NewCardRepository(s.DB)
//}
//
//func TestCardRepositorySuite(t *testing.T) {
//	suite.Run(t, new(CardRepositorySuite))
//}
//
//func (s *CardRepositorySuite) TestCreateNewCard() {
//	validCard := s.newValidCreateCardPayload()
//	duplicateCard := validCard
//
//	cases := []testutils.TestCase[views.CreateNewCustomerCard, *generated.CustomerCard]{
//		{
//			Name:  "Success: Create new customer card",
//			Input: validCard,
//			AssertResult: func(t *testing.T, result *generated.CustomerCard) {
//				assert.NotNil(t, result)
//				assert.Equal(t, validCard.CardNumber, result.CardNumber)
//			},
//		},
//		{
//			Name:  "Fail: Duplicate card number",
//			Input: duplicateCard,
//			Setup: func() {
//				_, err := s.repo.CreateNewCard(context.Background(), validCard)
//				assert.NoError(s.T(), err)
//			},
//			ExpectedError: repository.ErrConflict,
//		},
//	}
//
//	testutils.RunRepositoryTest(
//		&s.IntegrationSuite,
//		cases,
//		func(ctx context.Context, input views.CreateNewCustomerCard) (*generated.CustomerCard, error) {
//			return s.repo.CreateNewCard(ctx, input)
//		},
//	)
//}
//
//func (s *CardRepositorySuite) TestGetCustomerCard() {
//	validCard := s.newValidCreateCardPayload()
//	validNumber := validCard.CardNumber
//	invalidNumber := "invalid-card-number"
//
//	cases := []testutils.TestCase[string, *generated.CustomerCard]{
//		{
//			Name:  "Success: Get existing card",
//			Input: validNumber,
//			Setup: func() {
//				_, err := s.repo.CreateNewCard(context.Background(), validCard)
//				assert.NoError(s.T(), err)
//			},
//			AssertResult: func(t *testing.T, result *generated.CustomerCard) {
//				assert.NotNil(t, result)
//				assert.Equal(t, validNumber, result.CardNumber)
//			},
//		},
//		{
//			Name:          "Fail: Card not found with empty database",
//			Input:         invalidNumber,
//			ExpectedError: repository.ErrNotFound,
//		},
//		{
//			Name: "Fail: Card not found with row in database",
//			Setup: func() {
//				_, err := s.repo.CreateNewCard(context.Background(), validCard)
//				assert.NoError(s.T(), err)
//			},
//			Input:         invalidNumber,
//			ExpectedError: repository.ErrNotFound,
//		},
//	}
//
//	testutils.RunRepositoryTest(
//		&s.IntegrationSuite,
//		cases,
//		func(ctx context.Context, input string) (*generated.CustomerCard, error) {
//			return s.repo.GetCustomerCard(ctx, input)
//		},
//	)
//}
//
//func (s *CardRepositorySuite) TestUpdateCustomerCard() {
//	type args struct {
//		card       views.UpdateCustomerCard
//		cardNumber string
//	}
//
//	validCreate := s.newValidCreateCardPayload()
//	validUpdate := s.newValidUpdateCardPayload()
//	validNumber := validCreate.CardNumber
//	invalidNumber := "invalid-card-number"
//
//	cases := []testutils.TestCase[args, *generated.CustomerCard]{
//		{
//			Name: "Success: Update existing card",
//			Input: args{
//				card:       validUpdate,
//				cardNumber: validNumber,
//			},
//			Setup: func() {
//				_, err := s.repo.CreateNewCard(context.Background(), validCreate)
//				assert.NoError(s.T(), err)
//			},
//			AssertResult: func(t *testing.T, result *generated.CustomerCard) {
//				assert.NotNil(t, result)
//				assert.Equal(t, validUpdate.CustomerName, result.CustomerName)
//				assert.Equal(t, validUpdate.CustomerSurname, result.CustomerSurname)
//			},
//		},
//		{
//			Name: "Fail: Card not found",
//			Input: args{
//				card:       validUpdate,
//				cardNumber: invalidNumber,
//			},
//			ExpectedError: repository.ErrNotFound,
//		},
//	}
//
//	testutils.RunRepositoryTest(
//		&s.IntegrationSuite,
//		cases,
//		func(ctx context.Context, input args) (*generated.CustomerCard, error) {
//			return s.repo.UpdateCustomerCard(ctx, input.card, input.cardNumber)
//		},
//	)
//}
//
//func (s *CardRepositorySuite) TestDeleteCustomerCard() {
//	validCard := s.newValidCreateCardPayload()
//	validNumber := validCard.CardNumber
//	invalidNumber := "invalid-card-number"
//
//	cases := []testutils.TestCase[string, any]{
//		{
//			Name:  "Success: Delete existing card",
//			Input: validNumber,
//			Setup: func() {
//				_, err := s.repo.CreateNewCard(context.Background(), validCard)
//				assert.NoError(s.T(), err)
//			},
//			AssertResult: func(t *testing.T, _ any) {
//				_, err := s.repo.GetCustomerCard(context.Background(), validNumber)
//				assert.ErrorIs(t, err, repository.ErrNotFound)
//			},
//		},
//		{
//			Name:          "Fail: Card not found",
//			Input:         invalidNumber,
//			ExpectedError: repository.ErrNotFound,
//		},
//	}
//
//	testutils.RunRepositoryTest(
//		&s.IntegrationSuite,
//		cases,
//		func(ctx context.Context, input string) (any, error) {
//			return nil, s.repo.DeleteCustomerCard(ctx, input)
//		},
//	)
//}
//
//func (s *CardRepositorySuite) TestListCustomerCards() {
//	cases := []testutils.TestCase[struct{}, []generated.CustomerCard]{
//		{
//			Name: "Success: List customer cards",
//			Setup: func() {
//				_, err := s.repo.CreateNewCard(context.Background(), s.newValidCreateCardPayload())
//				assert.NoError(s.T(), err)
//			},
//			AssertResult: func(t *testing.T, result []generated.CustomerCard) {
//				assert.NotEmpty(t, result)
//			},
//		},
//	}
//
//	testutils.RunRepositoryTest(
//		&s.IntegrationSuite,
//		cases,
//		func(ctx context.Context, _ struct{}) ([]generated.CustomerCard, error) {
//			return s.repo.ListCustomerCards(ctx)
//		},
//	)
//}
//
//func (s *CardRepositorySuite) newValidCreateCardPayload() views.CreateNewCustomerCard {
//	return views.CreateNewCustomerCard{
//		CardNumber:      "foo",
//		CustomerSurname: "foo",
//		CustomerName:    "foo",
//		PhoneNumber:     "380991112233",
//		CustomerPercent: 5,
//	}
//}
//
//func (s *CardRepositorySuite) newValidUpdateCardPayload() views.UpdateCustomerCard {
//	return views.UpdateCustomerCard{
//		CustomerSurname: "updated_foo",
//		CustomerName:    "updated_foo",
//		PhoneNumber:     "380991112244",
//		CustomerPercent: 7,
//	}
//}
