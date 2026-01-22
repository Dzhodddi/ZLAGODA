package views

type CreateNewCheck struct {
	CheckNumber string         `json:"check_number" validate:"required,min=1,max=10"`
	IDEmployee  string         `json:"id_employee" validate:"required,min=1,max=10"`
	CardNumber  string         `json:"card_number" validate:"required,min=1,max=13"`
	PrintDate   string         `json:"print_date" validate:"required,datetime=2006-01-02T15:04:05Z07:00"`
	Products    []StoreProduct `json:"products" validate:"required,min=1"`
	VAT         float64        `json:"vat" validate:"required,gte=0,lte=999999999.9999"`
}

type CheckResponse struct {
	CheckNumber string  `json:"check_number"`
	IDEmployee  string  `json:"id_employee"`
	CardNumber  string  `json:"card_number"`
	PrintDate   string  `json:"print_date"`
	SumTotal    float64 `json:"sum_total"`
	VAT         float64 `json:"vat"`
}
