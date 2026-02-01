package error_response

import (
	"fmt"
	"net/http"
)

type HTTPErrorResponse struct {
	Code    int    `json:"-"`
	Message string `json:"message"`
	Details string `json:"details,omitempty"`
}

func (e *HTTPErrorResponse) Error() string {
	if e.Details != "" {
		return fmt.Sprintf("%s: %s", e.Message, e.Details)
	}
	return e.Message
}

func NewError(code int, message string, details error) *HTTPErrorResponse {
	det := ""
	if details != nil {
		det = details.Error()
	}
	return &HTTPErrorResponse{
		Code:    code,
		Message: message,
		Details: det,
	}
}

func BadRequest(msg string, err error) *HTTPErrorResponse {
	return NewError(http.StatusBadRequest, msg, err)
}

func ValidationError(msg string, err error) *HTTPErrorResponse {
	return NewError(http.StatusUnprocessableEntity, msg, err)
}
