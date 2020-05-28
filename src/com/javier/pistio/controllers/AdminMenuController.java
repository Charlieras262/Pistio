package com.javier.pistio.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.javier.pistio.modelos.Turno;
import com.javier.pistio.utils.PageEventHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.Util.addZeros;
import static com.javier.pistio.utils.Util.changeView;

public class AdminMenuController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    void createReport(MouseEvent event) {
        SOCKET.emit("getReportData");
    }

    @FXML
    void back(MouseEvent event) {
        try {
            StackPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/login.fxml"));
            rootPane.getChildren().setAll(anchorPane);
        } catch (IOException e) {
            System.err.println("Error: No se econtro el archivo.");
        }
    }

    @FXML
    void go2Users(MouseEvent event) {
        changeView(root, rootPane, "../ui/usuarios.fxml", true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SOCKET.on("reportData", args -> {
            Type type = new TypeToken<List<Turno>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<Turno> list = gson.fromJson(args[0].toString(), type);
            createPDF(list);
            System.out.println(args[0]);
        });
    }

    public void createPDF(ArrayList<Turno> turnos) {
        try {
            String fileName = "Resporte Pistio - Tickets";
            Document document = new Document();
            File file = new File(fileName + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);

            addHeader(document, fos);

            if (!document.isOpen()) {
                document.open();
            }

            PdfDiv pdfTable = exportPDFTable(turnos);
            document.add(pdfTable);
            document.close();

            Desktop.getDesktop().open(file);
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    file.delete();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            System.out.println("createPDF: " + ex);
        }
    }

    private void addHeader(Document document, FileOutputStream fos) {
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document, fos);

            if (!document.isOpen()) {
                document.open();
            }

            URL url = getClass().getResource("../media/logo.jpeg");
            Image mid = Image.getInstance(url);
            if (mid != null) {
                mid.setAbsolutePosition((document.getPageSize().getWidth() / 2) - mid.getScaledWidth() / 2, (document.getPageSize().getHeight() / 2) - mid.getScaledHeight() / 2);
                PdfGState state = new PdfGState();
                state.setFillOpacity(0.2f);
                writer.setPageEvent(new PageEventHelper(mid, "TK"));
            }
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(AdminMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private PdfDiv exportPDFTable(ArrayList<Turno> turnos) {
        PdfDiv doc = new PdfDiv();
        BaseFont helvetica = null;
        try {
            helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            System.err.println("Error " + e);
        }
        Font fontTitle = new Font(helvetica, 10, Font.BOLD);
        Font fontSubTitle = new Font(helvetica, 9, Font.NORMAL);
        Font fontBody = new Font(helvetica, 8, Font.NORMAL);
        Paragraph pa = new Paragraph("Tokens", new Font(helvetica, 14, Font.BOLD));
        pa.setAlignment(Element.ALIGN_CENTER);
        doc.addElement(pa);
        int i = 0;
        doc.addElement(new Paragraph(" "));
        doc.addElement(new LineSeparator());
        doc.addElement(new Paragraph(" "));
        for (Turno turno : turnos) {
            String state = turno.getState().equals("E") ? "Esperando" : turno.getState().equals("G") ?
                    "Atendiendo" : turno.getState().equals("X") ? "Cancelado" : turno.getState().equals("A") ?
                    "Atendido" : "Atendido con Exceso";
            i++;
            doc.addElement(new Paragraph(i + ". " + turno.get_id(), fontTitle));
            doc.addElement(new Paragraph("Turno: " + turno.getType() + " - " + addZeros(turno.getCorrel()), fontBody));
            doc.addElement(new Paragraph("Tiene preferencias: " + (turno.isPref() ? "Si Tiene" : "No Tiene"), fontSubTitle));
            doc.addElement(new Paragraph("Estado: " + state, fontSubTitle)); // E = Esperando, G: Atendiendo || X: Cancelado, A: Atendido, AE: Atendido Exeso
            if (i < turnos.size()) {
                doc.addElement(new Paragraph(" "));
                doc.addElement(new LineSeparator());
                doc.addElement(new Paragraph(" "));
            }
        }
        if (i == 0) {
            doc.addElement(new Paragraph("No se ejecutaron metodos.", new Font(helvetica, 12, Font.BOLD)));
        }
        return doc;
    }
}
