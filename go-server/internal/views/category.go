package views

type CreateNewCategory struct {
	CategoryName string `json:"category_name" validate:"required,max=50,min=1"`
}

type CategoryResponse struct {
	CategoryNumber int64  `json:"category_number"`
	CategoryName   string `json:"category_name"`
}

type UpdateCategory struct {
	CategoryName string `json:"category_name" validate:"required,max=50,min=1"`
}

type ListCategoryQueryParams struct {
	Sorted             *bool   `query:"sorted"`
	LastCategoryNumber *int64  `query:"category_number"`
	LastCategoryName   *string `query:"category_name"`
}
