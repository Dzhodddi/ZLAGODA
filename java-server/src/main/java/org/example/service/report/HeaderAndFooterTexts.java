package org.example.service.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeaderAndFooterTexts extends PdfPageEventHelper {
    private static final String HEADER = "ZLAGODA";
    private static final String ICON_PATH = "logos/icon-blue.png";
    private static final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    private final Font FONT;
    private final String MANAGER_NAME;

    public HeaderAndFooterTexts(Font font, String manager) {
        FONT = font;
        MANAGER_NAME = manager;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        float x = document.getPageSize().getWidth() / 2;
        float y = document.getPageSize().getTop() - 25;
        try {
            ClassPathResource iconResource = new ClassPathResource(ICON_PATH);
            Image icon = Image.getInstance(iconResource.getURL());
            icon.scaleToFit(14, 14);
            icon.setAbsolutePosition(x - 20, y);
            writer.getDirectContent().addImage(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_LEFT,
                new Phrase(HEADER, FONT),
                x,
                y + 3,
                0
        );
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                new Phrase("Звіт сформований менеджером(-кою) " +
                        MANAGER_NAME + " о " + LocalDateTime.now().plusHours(2).format(formatter)
                        + " р.", FONT),
                document.getPageSize().getWidth() / 2,
                document.getPageSize().getBottom() + 25,
                0
        );
        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                new Phrase("Сторінка " + writer.getPageNumber(), FONT),
                document.getPageSize().getWidth() / 2,
                document.getPageSize().getBottom() + 15,
                0
        );
    }
}
