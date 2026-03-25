package org.example.service.report;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = getFont();
        Font headerFont = getHeaderFont();

        Font titleFont = getTitleFont();
        document.add(new Paragraph("ЗВІТ ПРО ПРАЦІВНИКІВ\n\n", titleFont));

        PdfPTable table = new PdfPTable(12);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 3, 3, 3, 2, 2, 3, 3, 3, 3, 3, 2});

        for (String header : new String[]{
                "ID", "Прізвище", "Ім'я", "По батькові", "Посада",
                "Зарплата", "Контактний телефон", "Дата народження", "Дата початку роботи",
                "Місто", "Вулиця", "Поштовий індекс"
        }) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(59, 130, 246));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(4);
            table.addCell(cell);
        }

        for (EmployeeResponseDto e : employees) {
            for (String value : new String[]{
                    e.getId_employee(),
                    e.getEmpl_surname(),
                    e.getEmpl_name(),
                    e.getEmpl_patronymic() != null ? e.getEmpl_patronymic() : "—",
                    Objects.equals(e.getRole(), "MANAGER") ? "Менеджер" : "Касир",
                    String.valueOf(e.getSalary()),
                    e.getPhone_number(),
                    String.valueOf(e.getDate_of_birth()),
                    String.valueOf(e.getDate_of_start()),
                    e.getCity(),
                    e.getStreet(),
                    e.getZip_code()
            }) {
                PdfPCell cell = new PdfPCell(new Phrase(value, font));
                cell.setPadding(3);
                table.addCell(cell);
            }
        }

        document.add(table);
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
        Font headerFont = getHeaderFont();

        Font titleFont = getTitleFont();
        document.add(new Paragraph("ЗВІТ ПРО ТОВАРИ\n\n", titleFont));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 2, 5});

        for (String header : new String[]{"ID товару", "Назва", "Виробник", "Характеристики"}) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(59, 130, 246));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(4);
            table.addCell(cell);
        }

        for (ProductDto p : products) {
            for (String value : new String[]{
                    String.valueOf(p.getId_product()),
                    p.getProduct_name(),
                    p.getProducer(),
                    p.getProduct_characteristics()
            }) {
                PdfPCell cell = new PdfPCell(new Phrase(value, font));
                cell.setPadding(3);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    @Override
    public byte[] storeProductToPdf(List<StoreProductDto> storeProducts)
            throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Font font = getFont();
        Font headerFont = getHeaderFont();

        Font titleFont = getTitleFont();
        document.add(new Paragraph("ЗВІТ ПРО ТОВАРИ В МАГАЗИНІ\n\n", titleFont));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        for (String header : new String[]{
                "UPC", "UPC промо", "ID товару", "Ціна продажу", "Кількість", "Акційність"
        }) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(59, 130, 246));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(4);
            table.addCell(cell);
        }

        for (StoreProductDto sp : storeProducts) {
            for (String value : new String[]{
                    sp.getUPC(),
                    sp.getUPC_prom() != null ? sp.getUPC_prom() : "—",
                    String.valueOf(sp.getId_product()),
                    String.valueOf(sp.getSelling_price()),
                    String.valueOf(sp.getProducts_number()),
                    sp.isPromotional_product() ? "Так" : "Ні"
            }) {
                PdfPCell cell = new PdfPCell(new Phrase(value, font));
                cell.setPadding(3);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private Font getFont() throws IOException, DocumentException {
        BaseFont bf = createBaseFont();
        return new Font(bf, 8);
    }

    private Font getHeaderFont() throws IOException, DocumentException {
        BaseFont bf = createBaseFont();
        Font f = new Font(bf, 8, Font.BOLD);
        f.setColor(BaseColor.WHITE);
        return f;
    }

    private BaseFont createBaseFont() throws IOException, DocumentException {
        ClassPathResource fontResource = new ClassPathResource(FONT_PATH);
        if (!fontResource.exists()) {
            throw new IOException("Font not found: " + FONT_PATH);
        }
        return BaseFont.createFont(
                fontResource.getURL().toString(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );
    }

    private Font getTitleFont() throws IOException, DocumentException {
        BaseFont bf = createBaseFont();
        Font f = new Font(bf, 14, Font.BOLD);
        f.setColor(BaseColor.BLACK);
        return f;
    }
}
