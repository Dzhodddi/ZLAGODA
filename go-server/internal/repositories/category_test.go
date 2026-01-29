package repository_test

import (
	"context"
	"fmt"
	"testing"

	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type CategoryRepositorySuite struct {
	testutils.IntegrationSuite
	repo repository.CategoryRepository
}

func (s *CategoryRepositorySuite) SetupTest() {
	s.repo = repository.NewCategoryRepository(s.DB)
}

func TestCategoryRepositorySuite(t *testing.T) {
	suite.Run(t, new(CategoryRepositorySuite))
}

func (s *CategoryRepositorySuite) TestCreateNewCategory() {
	validCategory := s.validCreationInput()

	cases := []testutils.TestCase[views.CreateNewCategory, *generated.Category]{
		{
			Name:  "Success: Create new category",
			Input: validCategory,
			AssertResult: func(t *testing.T, result *generated.Category) {
				assert.NotNil(t, result)
				assert.Equal(t, validCategory.CategoryName, result.CategoryName)
			},
		},
	}

	testutils.RunRepositoryTest(
		&s.IntegrationSuite,
		cases,
		func(ctx context.Context, input views.CreateNewCategory) (*generated.Category, error) {
			return s.repo.CreateNewCategory(ctx, input)
		},
	)
}

func (s *CategoryRepositorySuite) TestUpdateCategory() {
	validCreate := s.validCreationInput()
	validUpdate := s.validUpdatingInput()

	var targetID int64

	cases := []testutils.TestCase[views.UpdateCategory, *generated.Category]{
		{
			Name:  "Success: Update existing category",
			Input: validUpdate,
			Setup: func() {
				created, err := s.repo.CreateNewCategory(context.Background(), validCreate)
				s.Require().NoError(err)
				targetID = created.CategoryNumber
			},
			AssertResult: func(t *testing.T, result *generated.Category) {
				assert.NotNil(t, result)
				assert.Equal(t, validUpdate.CategoryName, result.CategoryName)
				assert.Equal(t, targetID, result.CategoryNumber)
			},
		},
		{
			Name:  "Fail: Update Non-Existent Category",
			Input: validUpdate,
			Setup: func() {
				targetID = -1
			},
			ExpectedError: repository.ErrNotFound,
		},
	}

	testutils.RunRepositoryTest(
		&s.IntegrationSuite,
		cases,
		func(ctx context.Context, input views.UpdateCategory) (*generated.Category, error) {
			return s.repo.UpdateCategory(ctx, input, targetID)
		},
	)
}

func (s *CategoryRepositorySuite) TestDeleteCategory() {
	validCreate := s.validCreationInput()
	var validId int64

	cases := []testutils.TestCase[int64, any]{
		{
			Name:  "Success: Delete existing category",
			Input: validId,
			Setup: func() {
				category, err := s.repo.CreateNewCategory(context.Background(), validCreate)
				assert.NoError(s.T(), err)
				validId = category.CategoryNumber
			},
			AssertResult: func(t *testing.T, _ any) {
				_, err := s.repo.GetCategoryByID(context.Background(), validId)
				assert.ErrorIs(t, err, repository.ErrNotFound)
			},
		},
		{
			Name:  "Failed: Delete non-existing category",
			Input: validId,
			Setup: func() {
				category, err := s.repo.CreateNewCategory(context.Background(), validCreate)
				assert.NoError(s.T(), err)
				validId = category.CategoryNumber + 1
			},
			ExpectedError: repository.ErrNotFound,
		},
		{
			Name:  "Failed: Delete existing category with dependency on product",
			Input: validId,
			Setup: func() {
				category, err := s.repo.CreateNewCategory(context.Background(), validCreate)
				assert.NoError(s.T(), err)
				_, err = s.DB.Exec(
					fmt.Sprintf(
						"INSERT INTO product "+
							"(category_number, product_name, product_characteristics) "+
							"VALUES (%v, 'test', 'test')",
						category.CategoryNumber),
				)
				assert.NoError(s.T(), err)
				validId = category.CategoryNumber
			},
			ExpectedError: repository.ErrForeignKey,
		},
	}

	testutils.RunRepositoryTest(
		&s.IntegrationSuite,
		cases,
		func(ctx context.Context, _ int64) (any, error) {
			return nil, s.repo.DeleteCategory(ctx, validId)
		},
	)
}

func (s *CategoryRepositorySuite) TestGetCategoryByID() {
	validCreate := s.validCreationInput()
	var targetID int64
	cases := []testutils.TestCase[int64, *generated.Category]{
		{
			Name:  "Success: Get existing category by ID",
			Input: 0,
			Setup: func() {
				category, err := s.repo.CreateNewCategory(context.Background(), validCreate)
				assert.NoError(s.T(), err)
				targetID = category.CategoryNumber
			},
			AssertResult: func(t *testing.T, result *generated.Category) {
				assert.NotNil(t, result)
				assert.Equal(t, targetID, result.CategoryNumber)
			},
		},
	}

	testutils.RunRepositoryTest(
		&s.IntegrationSuite,
		cases,
		func(ctx context.Context, input int64) (*generated.Category, error) {
			return s.repo.GetCategoryByID(ctx, targetID)
		},
	)
}

func (s *CategoryRepositorySuite) TestGetAllCategories() {
	validCreate := s.validCreationInput()
	cases := []testutils.TestCase[struct{}, []generated.Category]{
		{
			Name: "Success: Get all categories",
			Setup: func() {
				_, err := s.repo.CreateNewCategory(context.Background(), validCreate)
				assert.NoError(s.T(), err)
			},
			AssertResult: func(t *testing.T, result []generated.Category) {
				assert.NotEmpty(t, result)
				assert.Equal(t, 1, len(result))
			},
		},
	}

	testutils.RunRepositoryTest(
		&s.IntegrationSuite,
		cases,
		func(ctx context.Context, _ struct{}) ([]generated.Category, error) {
			return s.repo.GetAllCategories(ctx)
		},
	)
}

func (s *CategoryRepositorySuite) validCreationInput() views.CreateNewCategory {
	return views.CreateNewCategory{
		CategoryName: "Test",
	}
}

func (s *CategoryRepositorySuite) validUpdatingInput() views.UpdateCategory {
	return views.UpdateCategory{
		CategoryName: "Updated Test",
	}
}
