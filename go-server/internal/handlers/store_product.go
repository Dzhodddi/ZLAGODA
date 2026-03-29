package handlers

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/services"
	"github.com/labstack/echo/v4"
)

type StoreProductHandler struct {
	storeProductService services.StoreProductService
}

func NewStoreProductHandler(storeProductService services.StoreProductService) *StoreProductHandler {
	return &StoreProductHandler{
		storeProductService: storeProductService,
	}
}

func (h *StoreProductHandler) RegisterRouts(e *echo.Group) {
	category := e.Group("/store-products")
	category.GET("/list", h.getAllStoreProductList)
}

func (h *StoreProductHandler) getAllStoreProductList(c echo.Context) error {
	storeProductList, err := h.storeProductService.GetStoreProductList(c.Request().Context())
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, storeProductList)
}
