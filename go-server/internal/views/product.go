package views

type StoreProduct struct {
	UPC      string  `json:"upc" validate:"required,min=1,max=12"`
	Price    float64 `json:"price" validate:"required,gt=0"`
	Quantity int     `json:"quantity" validate:"required,gte=1"`
}
