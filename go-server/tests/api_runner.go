package testutils

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/stretchr/testify/assert"
)

// APITestCase defines a single HTTP scenario
type APITestCase[ResponseType any] struct {
	Name         string
	Method       string
	URL          string
	Body         interface{}
	Setup        func()
	ExpectedCode int
	ErrorMessage string
	AssertResult func(*testing.T, ResponseType)
}

func RunAPITest[ResponseType any](
	s *IntegrationSuite,
	router http.Handler,
	cases []APITestCase[ResponseType],
) {
	for _, tc := range cases {
		s.Run(tc.Name, func() {
			s.TruncateAllTables()

			if tc.Setup != nil {
				tc.Setup()
			}

			var reqBody []byte
			var err error
			if tc.Body != nil {
				reqBody, _ = json.Marshal(tc.Body)
			}

			req := httptest.NewRequest(tc.Method, tc.URL, bytes.NewBuffer(reqBody))
			req.Header.Set("Content-Type", "application/json")

			w := httptest.NewRecorder()
			router.ServeHTTP(w, req)

			assert.Equal(s.T(), tc.ExpectedCode, w.Code, "Status code mismatch")
			if tc.ErrorMessage != "" {
				assert.Contains(s.T(), w.Body.String(), tc.ErrorMessage, "Response body should contain expected error")
			}
			if tc.ExpectedCode >= 200 && tc.ExpectedCode < 300 && tc.AssertResult != nil {
				var response ResponseType
				err = json.Unmarshal(w.Body.Bytes(), &response)
				assert.NoError(s.T(), err, "Failed to decode response JSON")
				tc.AssertResult(s.T(), response)
			}
		})
	}
}
