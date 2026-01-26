package org.example.service.report;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.springframework.stereotype.Service;

@Service
public class PdfReportGeneratorServiceImpl implements PdfReportGeneratorService {

    @Override
    public byte[] employeeToPdf(List<EmployeeResponseDto> employees) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Employees report\n"));
        for (EmployeeResponseDto e : employees) {
            document.add(new Paragraph(
                    "ID: " + e.getId_employee() + " | " +
                            "Surname: " + e.getEmpl_surname() + " | " +
                            "Name: " + e.getEmpl_name() + " | " +
                            "Patronymic: " + e.getEmpl_patronymic() + " | " +
                            "Role: " + e.getRole() + " | " +
                            "Salary: " + e.getSalary() + " | " +
                            "Phone: " + e.getPhone_number() + " | " +
                            "Address: " + e.getCity() + ", " +
                            e.getStreet() + ", " +
                            e.getZip_code()
            ));
        }
        document.close();
        return out.toByteArray();
    }

    @Override
    public byte[] productToPdf(List<ProductDto> products) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Products report\n"));
        for (ProductDto p : products) {
            document.add(new Paragraph(
                    "Product ID: " + p.getId_product() + " | " +
                            "Name: " + p.getProduct_name() + " | " +
                            "Characteristics: " + p.getProduct_characteristics() + " | " +
                            "Category number: " + p.getCategory_number()
            ));
        }
        document.close();
        return out.toByteArray();
    }

    @Override
    public byte[] storeProductToPdf(List<StoreProductDto> storeProducts) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Store products report\n"));
        for (StoreProductDto sp : storeProducts) {
            document.add(new Paragraph(
                    "UPC: " + sp.getUPC() + " | " +
                            "Promotional UPC: " + sp.getUPC_prom() + " | " +
                            "Product ID: " + sp.getId_product() + " | " +
                            "Selling price: " + sp.getSelling_price() + " | " +
                            "Quantity: " + sp.getProducts_number() + " | " +
                            "Promotional: " + sp.isPromotional_product()
            ));
        }
        document.close();
        return out.toByteArray();
    }
}
