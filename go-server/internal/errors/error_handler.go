package error_response

import (
	"errors"
	"fmt"
	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"net/http"

	"github.com/labstack/echo/v4"
)

func GlobalHTTPErrorHandler(env config.Env) func(error, echo.Context) {
	return func(err error, c echo.Context) {
		code := http.StatusInternalServerError
		msg := constants.InternalServerError
		details := ""

		var appErr *HTTPErrorResponse
		if errors.As(err, &appErr) {
			code = appErr.Code
			msg = appErr.Message
			details = appErr.Details
		} else if he, ok := err.(*echo.HTTPError); ok {
			code = he.Code
			msg = fmt.Sprintf("%v", he.Message)
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
