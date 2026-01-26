package error_response

import (
	"errors"
	"fmt"
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"

	"github.com/labstack/echo/v4"
)

func GlobalHTTPErrorHandler(env config.Env) func(error, echo.Context) {
	return func(err error, c echo.Context) {
		code := http.StatusInternalServerError
		msg := constants.InternalServerError
		details := ""

		var appErr *HTTPErrorResponse
		var echoErr *echo.HTTPError

		switch {
		case errors.Is(err, repository.ErrNotFound):
			code = http.StatusNotFound
			msg = "Entity not found"
		case errors.Is(err, repository.ErrConflict):
			code = http.StatusUnprocessableEntity
			msg = "Entity already exists"
		case errors.Is(err, repository.ErrForeignKey):
			code = http.StatusBadRequest
			msg = "Invalid reference"

		case errors.As(err, &appErr):
			code = appErr.Code
			msg = appErr.Message
			details = appErr.Details

		case errors.As(err, &echoErr):
			code = echoErr.Code
			msg = fmt.Sprintf("%v", echoErr.Message)
		}
		if env == config.Prod {
			details = ""
		}
		if !c.Response().Committed {
			if c.Request().Method == http.MethodHead {
				_ = c.NoContent(code)
			} else {
				_ = c.JSON(code, map[string]interface{}{
					"error":   msg,
					"details": details,
				})
			}
		}
	}
}
