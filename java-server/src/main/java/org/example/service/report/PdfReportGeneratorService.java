package org.example.service.report;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.helper.CategoryResponseDto;
import org.example.dto.helper.CheckResponseDto;
import org.example.dto.helper.CustomerCardResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.product.StoreProductDto;

public interface PdfReportGeneratorService {

    byte[] employeeToPdf(List<EmployeeResponseDto> employees, String managerName) throws DocumentException, IOException;

    byte[] productToPdf(List<ProductDto> products, String managerName) throws DocumentException, IOException;

    byte[] storeProductToPdf(List<StoreProductDto> storeProducts, String managerName) throws DocumentException, IOException;

    byte[] cardToPdf(List<CustomerCardResponseDto> cards, String managerName) throws DocumentException, IOException;

    byte[] checkToPdf(List<CheckResponseDto> checks, String managerName) throws DocumentException, IOException;

    byte[] categoryToPdf(List<CategoryResponseDto> categories, String managerName) throws DocumentException, IOException;
}
