package handlers_test

import (
	"context"
	"fmt"
	"net/http"
	"sort"
	"strconv"
	"testing"

	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type CardHandlerSuite struct {
	HandlerSuite
}

func TestCardHandlerSuite(t *testing.T) {
	suite.Run(t, new(CardHandlerSuite))
}

func (s *CardHandlerSuite) TestCreateCard() {
	validInput := s.createCardPayload()

	cases := []testutils.APITestCase[*views.CustomerCardResponse]{
		{
			Name:         "Success: Create valid card",
			Method:       http.MethodPost,
			URL:          func() string { return "/api/v1/customer-cards" },
			Body:         validInput,
			ExpectedCode: http.StatusCreated,
			AssertResult: func(t *testing.T, res *views.CustomerCardResponse) {
				assert.NotNil(t, res)
				assert.Equal(t, res.CardNumber, validInput.CardNumber)
			},
		},
		{
			Name:   "Fail: Validation error (empty name)",
			Method: http.MethodPost,
			URL:    func() string { return "/api/v1/customer-cards" },
			Body: views.CreateNewCustomerCard{
				CardNumber: "",
			},
			ExpectedCode: http.StatusUnprocessableEntity,
		},
		{
			Name:         "Fail: Invalid JSON (bind error)",
			Method:       http.MethodPost,
			URL:          func() string { return "/api/v1/customer-cards" },
			Body:         "bad payload",
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.ValidationError,
		},
		{
			Name: "Fail: Create duplicate card",
			Setup: func() {
				ctx := context.Background()
				_, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
			},
			Method:       http.MethodPost,
			URL:          func() string { return "/api/v1/customer-cards" },
			Body:         validInput,
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: repository.ErrConflict.Error(),
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CardHandlerSuite) TestGetCardByCardNumber() {
	validInput := s.createCardPayload()

	var cardNumber string

	cases := []testutils.APITestCase[*views.CustomerCardResponse]{
		{
			Name:   "Success: Get card by ID",
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber) },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *views.CustomerCardResponse) {
				assert.NotNil(t, res)
				assert.Equal(t, validInput.CardNumber, res.CardNumber)
				assert.NotEmpty(t, res.CardNumber)
			},
		},
		{
			Name:   "Fail: Card not found",
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber+"foo") },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Get card from empty database",
			Method:       http.MethodGet,
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", "foo") },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CardHandlerSuite) TestGetCardList() {
	validInput := s.createCardPayload()
	length := 5
	trueValue := true
	invalidPercent := -10
	q := views.ListCustomerCardsQueryParams{
		Percent: nil,
		Sorted:  &trueValue,
	}
	invalidQ := views.ListCustomerCardsQueryParams{
		Percent: &invalidPercent,
		Sorted:  nil,
	}

	cases := []testutils.APITestCase[*[]views.CustomerCardResponse]{
		{
			Name:   fmt.Sprintf("Success: Get card list with %d items", length),
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				for i := range length {
					copyInput := validInput
					copyInput.CardNumber = validInput.CardNumber + strconv.Itoa(i)
					_, err := s.CardRepo.CreateNewCard(ctx, copyInput)
					s.Require().NoError(err)
				}
			},
			URL:          func() string { return "/api/v1/customer-cards" },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *[]views.CustomerCardResponse) {
				assert.NotNil(t, res)
				assert.Len(t, *res, length)
				for i, card := range *res {
					assert.Equal(t, validInput.CardNumber+strconv.Itoa(i), card.CardNumber)
					assert.NotEmpty(t, card.CardNumber)
				}
			},
		},
		{
			Name:   fmt.Sprintf("Success: Get card list with %d items and sorted", length),
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				for i := range length {
					copyInput := validInput
					copyInput.CardNumber = validInput.CardNumber + strconv.Itoa(i)
					_, err := s.CardRepo.CreateNewCard(ctx, copyInput)
					s.Require().NoError(err)
				}
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards?sorted=%v", *q.Sorted) },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *[]views.CustomerCardResponse) {
				assert.NotNil(t, res)
				assert.Len(t, *res, length)
				sorted := sort.SliceIsSorted(*res, func(i, j int) bool {
					return (*res)[i].CustomerName < (*res)[j].CustomerName
				})
				assert.True(t, sorted)
			},
		},
		{
			Name:   "Fail: Get card list with negative percent",
			Method: http.MethodGet,
			URL: func() string {
				return fmt.Sprintf("/api/v1/customer-cards?&percent=%v", *invalidQ.Percent)
			},
			ExpectedCode: http.StatusUnprocessableEntity,
			ErrorMessage: constants.ValidationError,
		},
		{
			Name:         "Success: Get empty card list",
			Method:       http.MethodGet,
			URL:          func() string { return "/api/v1/customer-cards" },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *[]views.CustomerCardResponse) {
				assert.Nil(t, res)
			},
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CardHandlerSuite) TestUpdateCard() {
	surname := "test-surname"
	validInput := s.createCardPayload()

	updatedInput := views.UpdateCustomerCard{
		CustomerSurname: surname,
		CustomerName:    "foo",
		PhoneNumber:     "foo",
		CustomerPercent: 1,
	}
	invalidInput := updatedInput
	invalidInput.CustomerName = ""

	var cardNumber string

	cases := []testutils.APITestCase[*views.CustomerCardResponse]{
		{
			Name:   "Success: Update card",
			Method: http.MethodPut,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			Body:         updatedInput,
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber) },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *views.CustomerCardResponse) {
				assert.NotNil(t, res)
				assert.Equal(t, updatedInput.CustomerSurname, res.CustomerSurname)
			},
		},
		{
			Name:   "Fail: Update card with bad payload",
			Method: http.MethodPut,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			Body:         invalidInput,
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber) },
			ExpectedCode: http.StatusUnprocessableEntity,
			ErrorMessage: constants.ValidationError,
		},
		{
			Name:   "Fail: JSON bind error",
			Method: http.MethodPut,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			Body:         "bad payload",
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber) },
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.ValidationError,
		},
		{
			Name:   "Fail: Update non-existing card",
			Method: http.MethodPut,
			Body:   updatedInput,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber+"foo") },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Update card in empty database",
			Method:       http.MethodPut,
			Body:         updatedInput,
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", "foo") },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CardHandlerSuite) TestDeleteCard() {
	validInput := s.createCardPayload()

	var cardNumber string

	cases := []testutils.APITestCase[*views.CustomerCardResponse]{
		{
			Name:   "Success: Delete card",
			Method: http.MethodDelete,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber) },
			ExpectedCode: http.StatusNoContent,
		},
		{
			Name:   "Fail: Delete non-existing card",
			Method: http.MethodDelete,
			Setup: func() {
				ctx := context.Background()
				card, err := s.CardRepo.CreateNewCard(ctx, validInput)
				s.Require().NoError(err)
				cardNumber = card.CardNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", cardNumber+"foo") },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Delete card in empty database",
			Method:       http.MethodDelete,
			URL:          func() string { return fmt.Sprintf("/api/v1/customer-cards/%s", "foo") },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CardHandlerSuite) createCardPayload() views.CreateNewCustomerCard {
	return views.CreateNewCustomerCard{
		CardNumber:         "foo",
		CustomerSurname:    "foo",
		CustomerName:       "foo",
		CustomerPatronymic: nil,
		PhoneNumber:        "foo",
		City:               nil,
		Street:             nil,
		Zipcode:            nil,
		CustomerPercent:    1,
	}
}
