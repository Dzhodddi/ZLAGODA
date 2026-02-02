package views

type CreateNewSale struct {
	ProductNumber int32
	Upc           string
	CheckNumber   string
	SellingPrice  float64
}

type SaleResponse struct {
	ProductNumber int32   `json:"product_number"`
	Upc           string  `json:"upc"`
	CheckNumber   string  `json:"check_number"`
	SellingPrice  float64 `json:"selling_price"`
}

type SaleListQueryParams struct {
	StartDate       string  `query:"start_date" validate:"required,datetime=2006-01-02"`
	EndDate         string  `query:"end_date" validate:"required,datetime=2006-01-02"`
	LastCheckNumber *string `query:"check_number" validate:"omitempty,max=50,min=1"`
}
