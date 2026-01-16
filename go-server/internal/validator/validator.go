package validation

import (
	"reflect"

	"github.com/Dzhodddi/ZLAGODA/internal/types"
	"github.com/go-playground/validator/v10"
)

var validate *validator.Validate

func init() {
	validate = validator.New(validator.WithRequiredStructEnabled())
	validate.RegisterCustomTypeFunc(func(field reflect.Value) interface{} {
		if d, ok := field.Interface().(types.Date); ok {
			return d.Time()
		}
		return nil
	}, types.Date{})
}

func ValidateStruct(s interface{}) error {
	return validate.Struct(s)
}
