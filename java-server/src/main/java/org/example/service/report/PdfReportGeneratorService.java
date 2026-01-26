package org.example.service.report;

import com.itextpdf.text.DocumentException;
import java.util.List;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.product.StoreProductDto;

public interface PdfReportGeneratorService {

    byte[] employeeToPdf(List<EmployeeResponseDto> employees) throws DocumentException;

    byte[] productToPdf(List<ProductDto> products) throws DocumentException;

    byte[] storeProductToPdf(List<StoreProductDto> storeProducts) throws DocumentException;
}
