package views

type StoreProduct struct {
	UPC      string `json:"upc" validate:"required,min=1,max=12"`
	Quantity int    `json:"quantity" validate:"required,gte=1"`
}
