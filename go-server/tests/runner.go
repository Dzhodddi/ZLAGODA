package testutils

import (
	"context"
	"testing"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/stretchr/testify/assert"
)

// TestCase defines the structure for a single generic test scenario
type TestCase[Input any, Output any] struct {
	Name          string
	Input         Input
	Setup         func()
	ExpectedError error
	AssertResult  func(*testing.T, Output)
}

func RunRepositoryTest[Input any, Output any](
	s *IntegrationSuite,
	cases []TestCase[Input, Output],
	execute func(context.Context, Input) (Output, error),
) {
	for _, tc := range cases {
		s.Run(tc.Name, func() {
			s.TruncateAllTables()

			if tc.Setup != nil {
				tc.Setup()
			}

			ctx, cancel := context.WithTimeout(context.Background(), constants.DatabaseTimeOut)
			defer cancel()

			result, err := execute(ctx, tc.Input)

			if tc.ExpectedError != nil {
				assert.ErrorIs(s.T(), err, tc.ExpectedError)
				return
			}
			if !assert.NoError(s.T(), err) {
				return
			}

			if tc.AssertResult != nil {
				tc.AssertResult(s.T(), result)
			}
		})
	}
}
