package views

type DropdownStoreProductItem struct {
	UPC          string  `json:"upc"`
	SellingPrice float64 `json:"selling_price"`
	Quantity     int32   `json:"quantity"`
	ProductName  string  `json:"product_name"`
}
