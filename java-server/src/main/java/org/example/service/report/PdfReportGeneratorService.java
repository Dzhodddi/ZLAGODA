package org.example.service.report;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.product.StoreProductDto;

public interface PdfReportGeneratorService {

    byte[] employeeToPdf(List<EmployeeResponseDto> employees) throws DocumentException, IOException;

    byte[] productToPdf(List<ProductDto> products) throws DocumentException, IOException;

    byte[] storeProductToPdf(List<StoreProductDto> storeProducts) throws DocumentException, IOException;
}
