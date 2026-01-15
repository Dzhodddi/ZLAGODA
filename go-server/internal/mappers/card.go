package mappers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/utils"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

func CardModelToResponse(card *generated.CustomerCard) *views.CustomerCardResponse {
	return &views.CustomerCardResponse{
		CardNumber:         card.CardNumber,
		CustomerSurname:    card.CustomerSurname,
		CustomerName:       card.CustomerName,
		CustomerPatronymic: utils.SqlNullToString(card.CustomerPatronymic),
		PhoneNumber:        card.PhoneNumber,
		City:               utils.SqlNullToString(card.City),
		Street:             utils.SqlNullToString(card.Street),
		Zipcode:            utils.SqlNullToString(card.ZipCode),
		CustomerPercent:    card.CustomerPercent,
	}
}
