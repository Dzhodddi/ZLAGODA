package views

type CreateNewSale struct {
	ProductNumber int32
	Upc           string
	CheckNumber   string
	SellingPrice  float64
}

type SaleResponse struct {
	ProductNumber int32
	Upc           string
	CheckNumber   string
	SellingPrice  float64
}
