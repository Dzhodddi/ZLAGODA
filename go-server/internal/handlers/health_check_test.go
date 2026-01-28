package handlers_test

import (
	"net/http"
	"testing"

	testutils "github.com/Dzhodddi/ZLAGODA/tests"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type HealthHandlerSuite struct {
	HandlerSuite
}

func TestHealthHandlerSuite(t *testing.T) {
	suite.Run(t, new(HealthHandlerSuite))
}

func (s *HealthHandlerSuite) TestHealthCheck() {
	cases := []testutils.APITestCase[string]{
		{
			Name:         "Success: System is Healthy",
			Method:       http.MethodGet,
			URL:          "/api/v1/health",
			ExpectedCode: http.StatusOK,
			AssertResult: func(t *testing.T, res string) {
				assert.Equal(t, "ok", res)
			},
		},
	}
	testutils.RunAPITest(&s.IntegrationSuite, s.Echo, cases)
}
