/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javier.pistio.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PageEventHelper extends PdfPageEventHelper {

    private final Image imagen;
    private final String type;

    public PageEventHelper(Image image, String type) {
        this.imagen = image;
        this.type = type;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            if (type.equals("TK")) {
                PdfContentByte over = writer.getDirectContent();
                over.saveState();

                PdfGState state = new PdfGState();
                state.setFillOpacity(0.2f);
                over.setGState(state);

                over.addImage(imagen);

                over.restoreState();
            } else {
                document.add(imagen);
            }
        } catch (DocumentException doc) {
            doc.printStackTrace();
        }
    }
}
