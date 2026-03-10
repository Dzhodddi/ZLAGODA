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
        document.add(new Paragraph("ЗВІТ ПРО ПРАЦІВНИКІВ\n\n", font));
        for (EmployeeResponseDto e : employees) {
            document.add(new Paragraph(
                    "ID: " + e.getId_employee() + "\n"
                            + "Прізвище: " + e.getEmpl_surname() + "\n"
                            + "Ім'я: " + e.getEmpl_name() + "\n"
                            + "По батькові: " + e.getEmpl_patronymic() + "\n"
                            + "Роль: " + e.getRole() + "\n"
                            + "Заробітна плата: " + e.getSalary() + "\n"
                            + "Номер телефону: " + e.getPhone_number() + "\n"
                            + "Дата народження: " + e.getDate_of_birth() + "\n"
                            + "Дата прийому на роботу: " + e.getDate_of_start() + "\n"
                            + "Адреса: " + e.getCity() + ", "
                            + e.getStreet() + ", " + e.getZip_code() + "\n\n",
                    font
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
        document.add(new Paragraph("ЗВІТ ПРО ПРОДУКТИ\n\n", font));
        for (ProductDto p : products) {
            document.add(new Paragraph(
                            "Назва: " + p.getProduct_name() + "\n"
                            + "Характеристики: " + p.getProduct_characteristics() + "\n\n",
                    font
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
                            "UPC промо: " + sp.getUPC_prom() + "\n" +
                            "ID продукту: " + sp.getId_product() + "\n" +
                            "Ціна продажу: " + sp.getSelling_price() + "\n" +
                            "Кількість: " + sp.getProducts_number() + "\n" +
                            "Акційність: " + sp.isPromotional_product() + "\n\n",
                    font
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
