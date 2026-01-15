FROM golang:alpine AS base
WORKDIR /app
RUN apk add --no-cache git make
COPY go-server/go.mod go-server/go.sum ./go-server/
WORKDIR /app/go-server
RUN go mod download

FROM base AS dev
RUN go install github.com/air-verse/air@latest
RUN go install github.com/swaggo/swag/cmd/swag@latest
COPY go-server /app/go-server
EXPOSE 8080
CMD ["sh", "-c", "swag init -g internal/server/server.go && air"]

FROM base AS prod-builder
COPY go-server /app/go-server
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-w -s" -o server cmd/api/main.go

FROM gcr.io/distroless/static-debian12 AS prod
WORKDIR /root/
COPY --from=prod-builder /app/go-server/server .
EXPOSE 8080
CMD ["./server"]