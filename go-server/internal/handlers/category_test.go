package handlers_test

import (
	"net/http"
	"testing"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type CategoryHandlerSuite struct {
	HandlerSuite
}

func TestCategoryHandlerSuite(t *testing.T) {
	suite.Run(t, new(CategoryHandlerSuite))
}

func (s *CategoryHandlerSuite) TestCreateCategory() {
	validInput := views.CreateNewCategory{
		CategoryName: "foo",
	}

	cases := []testutils.APITestCase[*views.CategoryResponse]{
		{
			Name:         "Success: Create Valid Category",
			Method:       http.MethodPost,
			URL:          "/api/v1/categories",
			Body:         validInput,
			ExpectedCode: http.StatusCreated,
			AssertResult: func(t *testing.T, res *views.CategoryResponse) {
				assert.NotNil(t, res)
				assert.Equal(t, validInput.CategoryName, res.CategoryName)
				assert.NotEmpty(t, res.CategoryNumber)
			},
		},
		{
			Name:   "Fail: Validation Error (Empty Name)",
			Method: http.MethodPost,
			URL:    "/api/v1/categories",
			Body: views.CreateNewCategory{
				CategoryName: "",
			},
			ExpectedCode: http.StatusUnprocessableEntity,
		},
		{
			Name:         "Fail: Invalid JSON (Bind Error)",
			Method:       http.MethodPost,
			URL:          "/api/v1/categories",
			Body:         "bad payload",
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.ValidationError,
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}
