package views

type StoreProduct struct {
	UPC      string `json:"upc" validate:"required,min=1,max=12"`
	Quantity int    `json:"quantity" validate:"required,gte=1"`
}

type ProductResponse struct {
	Name         string  `json:"name"`
	Quantity     int     `json:"quantity"`
	SellingPrice float64 `json:"selling_price"`
}

type CustomerCardProductResponse struct {
	ProductName   string  `json:"product_name"`
	TotalQuantity int64   `json:"quantity"`
	TotalPrice    float64 `json:"total_price"`
}
