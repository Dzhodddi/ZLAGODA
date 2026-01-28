package handlers_test

import (
	"context"
	"fmt"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
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
	validInput := s.createCategoryPayload()

	cases := []testutils.APITestCase[*views.CategoryResponse]{
		{
			Name:         "Success: Create Valid Category",
			Method:       http.MethodPost,
			URL:          func() string { return "/api/v1/categories" },
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
			URL:    func() string { return "/api/v1/categories" },
			Body: views.CreateNewCategory{
				CategoryName: "",
			},
			ExpectedCode: http.StatusUnprocessableEntity,
		},
		{
			Name:         "Fail: Invalid JSON (Bind Error)",
			Method:       http.MethodPost,
			URL:          func() string { return "/api/v1/categories" },
			Body:         "bad payload",
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.ValidationError,
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CategoryHandlerSuite) TestGetCategory() {
	categoryName := "foo"
	validInput := s.createCategoryPayload()

	var categoryNumber int64
	cases := []testutils.APITestCase[*views.CategoryResponse]{
		{
			Name:   "Success: Get Category",
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber) },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *views.CategoryResponse) {
				assert.NotNil(t, res)
				assert.Equal(t, validInput.CategoryName, res.CategoryName)
				assert.NotEmpty(t, res.CategoryNumber)
			},
		},
		{
			Name:   "Fail: Get Category with invalid Category Name",
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber+1) },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Get Category with valid Category Name and empty database",
			Method:       http.MethodGet,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", -1) },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Invalid Category Number",
			Method:       http.MethodGet,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%s", "dsad") },
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.EntityDoesNotExist,
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CategoryHandlerSuite) TestGetCategoryList() {
	categoryName := "foo"
	validInput := views.CreateNewCategory{
		CategoryName: categoryName,
	}
	length := 5
	cases := []testutils.APITestCase[*[]views.CategoryResponse]{
		{
			Name:   fmt.Sprintf("Success: Get Category list with %d items", length),
			Method: http.MethodGet,
			Setup: func() {
				ctx := context.Background()
				for range length {
					category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
					s.Require().NoError(err, "failed to create category")
					s.Require().Equal(categoryName, category.CategoryName)
				}
			},
			URL:          func() string { return "/api/v1/categories" },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *[]views.CategoryResponse) {
				assert.NotNil(t, res)
				assert.Len(t, *res, length)
				for _, category := range *res {
					assert.NotNil(t, res)
					assert.Equal(t, validInput.CategoryName, category.CategoryName)
					assert.NotEmpty(t, category.CategoryNumber)
				}
			},
		},
		{
			Name:         "Success: Get Category list with no items",
			Method:       http.MethodGet,
			URL:          func() string { return "/api/v1/categories" },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *[]views.CategoryResponse) {
				assert.Nil(t, res)
			},
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CategoryHandlerSuite) TestUpdateCategory() {
	categoryName := "foo"
	validInput := s.createCategoryPayload()
	updatedInput := views.UpdateCategory{
		CategoryName: categoryName + "-upd",
	}
	var categoryNumber int64
	cases := []testutils.APITestCase[*views.CategoryResponse]{
		{
			Name:   "Success: Update category",
			Method: http.MethodPut,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
			},
			Body:         updatedInput,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber) },
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res *views.CategoryResponse) {
				assert.NotNil(t, res)
				assert.Equal(t, updatedInput.CategoryName, res.CategoryName)
				assert.NotEmpty(t, res.CategoryNumber)
			},
		},
		{
			Name:   "Fail: Update Category with invalid Category Name",
			Method: http.MethodPut,
			Body:   updatedInput,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber+1) },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Update Category with valid Category Name and empty database",
			Method:       http.MethodPut,
			Body:         updatedInput,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", -1) },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Invalid Category Number",
			Method:       http.MethodPut,
			Body:         updatedInput,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%s", "dsad") },
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.EntityDoesNotExist,
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CategoryHandlerSuite) TestDeleteCategory() {
	categoryName := "foo"
	validInput := s.createCategoryPayload()
	var categoryNumber int64
	cases := []testutils.APITestCase[*views.CategoryResponse]{
		{
			Name:   "Success: Delete category",
			Method: http.MethodDelete,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber) },
			ExpectedCode: http.StatusNoContent,
		},
		{
			Name:   "Fail: Delete category when it has dependency",
			Method: http.MethodDelete,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
				_, err = s.DB.Exec(
					fmt.Sprintf(
						"INSERT INTO product "+
							"(category_number, product_name, product_characteristics) "+
							"VALUES (%v, 'test', 'test')",
						category.CategoryNumber),
				)
				s.Require().NoError(err, "failed to insert product")
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber) },
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: repository.ErrForeignKey.Error(),
		},
		{
			Name:   "Fail: Delete Category with invalid Category Name",
			Method: http.MethodDelete,
			Setup: func() {
				ctx := context.Background()
				category, err := s.CategoryRepo.CreateNewCategory(ctx, validInput)
				s.Require().NoError(err, "failed to create category")
				s.Require().Equal(categoryName, category.CategoryName)
				categoryNumber = category.CategoryNumber
			},
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", categoryNumber+1) },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Delete Category with valid Category Name and empty database",
			Method:       http.MethodDelete,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%d", -1) },
			ExpectedCode: http.StatusNotFound,
			ErrorMessage: repository.ErrNotFound.Error(),
		},
		{
			Name:         "Fail: Invalid Category Number",
			Method:       http.MethodDelete,
			URL:          func() string { return fmt.Sprintf("/api/v1/categories/%s", "dsad") },
			ExpectedCode: http.StatusBadRequest,
			ErrorMessage: constants.EntityDoesNotExist,
		},
	}

	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}

func (s *CategoryHandlerSuite) createCategoryPayload() views.CreateNewCategory {
	return views.CreateNewCategory{
		CategoryName: "foo",
	}
}
