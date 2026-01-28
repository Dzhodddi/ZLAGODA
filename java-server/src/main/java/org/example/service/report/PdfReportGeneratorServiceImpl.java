package org.example.service.report;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PdfReportGeneratorServiceImpl implements PdfReportGeneratorService {

    private static final String FONT_PATH = "fonts/LiberationSans.ttf";

    @Override
    public byte[] employeeToPdf(List<EmployeeResponseDto> employees)
            throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = getFont();
        document.add(new Paragraph("EMPLOYEES REPORT\n\n", font));
        for (EmployeeResponseDto e : employees) {
            document.add(new Paragraph(
                    "ID: " + e.getId_employee() + "\n"
                            + "Surname: " + e.getEmpl_surname() + "\n"
                            + "Name: " + e.getEmpl_name() + "\n"
                            + "Patronymic: " + e.getEmpl_patronymic() + "\n"
                            + "Role: " + e.getRole() + "\n"
                            + "Salary: " + e.getSalary() + "\n"
                            + "Phone: " + e.getPhone_number() + "\n"
                            + "Address: " + e.getCity() + ", "
                            + e.getStreet() + ", " + e.getZip_code() + "\n\n"
            ));
        }
        document.close();
        return out.toByteArray();
    }

    @Override
    public byte[] productToPdf(List<ProductDto> products)
            throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = getFont();
        document.add(new Paragraph("PRODUCTS REPORT\n\n", font));
        for (ProductDto p : products) {
            document.add(new Paragraph(
                    "Product ID: " + p.getId_product() + "\n"
                            + "Name: " + p.getProduct_name() + "\n"
                            + "Characteristics: " + p.getProduct_characteristics() + "\n"
                            + "Category number: " + p.getCategory_number() + "\n\n"
            ));
        }
        document.close();
        return out.toByteArray();
    }

    @Override
    public byte[] storeProductToPdf(List<StoreProductDto> storeProducts)
            throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = getFont();
        document.add(new Paragraph("STORE PRODUCTS REPORT\n\n", font));
        for (StoreProductDto sp : storeProducts) {
            document.add(new Paragraph(
                    "UPC: " + sp.getUPC() + "\n" +
                            "Promotional UPC: " + sp.getUPC_prom() + "\n" +
                            "Product ID: " + sp.getId_product() + "\n" +
                            "Selling price: " + sp.getSelling_price() + "\n" +
                            "Quantity: " + sp.getProducts_number() + "\n" +
                            "Promotional: " + sp.isPromotional_product() + "\n\n"
            ));
        }
        document.close();
        return out.toByteArray();
    }

    private Font getFont() throws IOException, DocumentException {
        ClassPathResource fontResource = new ClassPathResource(FONT_PATH);
        if (!fontResource.exists()) {
            throw new IOException("Font not found: " + FONT_PATH);
        }
        BaseFont bf = BaseFont.createFont(
                fontResource.getURL().toString(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );
        return new Font(bf, 12);
    }
}
