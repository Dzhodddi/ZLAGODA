package views

type CreateNewCustomerCard struct {
	CardNumber         string  `json:"card_number" validate:"required,max=13,min=1"`
	CustomerSurname    string  `json:"customer_surname" validate:"required,max=50,min=1"`
	CustomerName       string  `json:"customer_name" validate:"required,max=50,min=1"`
	CustomerPatronymic *string `json:"customer_patronymic" validate:"omitempty,max=50,min=1"`
	PhoneNumber        string  `json:"phone_number" validate:"required,max=13"`
	City               *string `json:"city" validate:"omitempty,max=50"`
	Street             *string `json:"street" validate:"omitempty,max=50"`
	Zipcode            *string `json:"zipcode" validate:"omitempty,max=9"`
	CustomerPercent    int32   `json:"customer_percent" validate:"required,max=100,min=1"`
}

type CustomerCardResponse struct {
	CardNumber         string  `json:"card_number"`
	CustomerSurname    string  `json:"customer_surname"`
	CustomerName       string  `json:"customer_name"`
	CustomerPatronymic *string `json:"customer_patronymic"`
	PhoneNumber        string  `json:"phone_number"`
	City               *string `json:"city"`
	Street             *string `json:"street"`
	Zipcode            *string `json:"zipcode"`
	CustomerPercent    int32   `json:"customer_percent"`
}

type UpdateCustomerCard struct {
	CustomerSurname    string  `json:"customer_surname" validate:"required,max=50,min=1"`
	CustomerName       string  `json:"customer_name" validate:"required,max=50,min=1"`
	CustomerPatronymic *string `json:"customer_patronymic" validate:"omitempty,max=50,min=1"`
	PhoneNumber        string  `json:"phone_number" validate:"required,max=13"`
	City               *string `json:"city" validate:"omitempty,max=50"`
	Street             *string `json:"street" validate:"omitempty,max=50"`
	Zipcode            *string `json:"zipcode" validate:"omitempty,max=9"`
	CustomerPercent    int32   `json:"customer_percent" validate:"required,max=100,min=1"`
}

type ListCustomerCardsQueryParams struct {
	Percent *int  `query:"percent" validate:"omitempty,min=1,max=100"`
	Sorted  *bool `query:"sorted"`
}
