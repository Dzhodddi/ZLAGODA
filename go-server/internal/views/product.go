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
