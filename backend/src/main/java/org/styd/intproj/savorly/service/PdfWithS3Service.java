package org.styd.intproj.savorly.service;

import com.amazonaws.HttpMethod;
import com.itextpdf.text.*;
import org.springframework.stereotype.Service;
import org.styd.intproj.savorly.service.S3Service;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PdfWithS3Service {

    private final S3Service s3Service;

    public PdfWithS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    private String getPreSigned(String pictureUrl) {
        return s3Service.generateUrl(pictureUrl, HttpMethod.GET);
    }

    public ByteArrayOutputStream generatedPdfStream(List<Map<String,Object>> queryResults) throws DocumentException {

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();


        document.add(new Paragraph("\n"));
        document.add(new Paragraph("YOUR RECIPE",new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD)));
        document.add(new Paragraph("\n"));

        //write data rows
        for (Map<String, Object> row : queryResults) {
            boolean isBold = true; //control the bold or not

            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() != null ? entry.getValue().toString() : "N/A";

                Font boldFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

                Font selectedFont = isBold ? boldFont : normalFont;

                //not to display picture link
                if(!Objects.equals(key, "picture")) {
                    if (Objects.equals(key, "name")) key = "Name of Recipe";
                    Paragraph paragraph = new Paragraph(key + " : " + value, selectedFont);
                    document.add(paragraph);
                }
                else {
                    System.out.println("The picture field is : " + value);
                    if (Objects.nonNull(value) && (value.contains("amazonaws.com")) && (value.contains("Amz-Credential")))
                    {
                        document.add(new Paragraph("\n"));

                        try {
                            Image image = Image.getInstance(new URL(value));
                            image.scaleToFit(400, 400); //limitation of img size
                            document.add(image);
                        } catch (Exception e) {
                            document.add(new Paragraph("Pleas find picture on our website.", normalFont));
                        }
                    }
                }

                isBold = false;
            }
        }


        document.close();
        return outputStream;

    }
}

