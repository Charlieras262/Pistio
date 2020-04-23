package com.javier.pistio.controllers;

import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.Util.changeView;

public class TicketViewController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXComboBox<Label> tipo, pref;

    @FXML
    void back(MouseEvent event) {
        changeView(root, rootPane, "../ui/soporte_menu.fxml", true);
    }

    @FXML
    void generarTicket(MouseEvent event) {
        String type = (tipo.getValue().getText().equals("Caja") ? "C" : tipo.getValue().getText().equals("Atención al Cliente") ? "S" : "R");
        boolean isPref = !pref.getValue().getText().equals("Sin Preferencias");
        tipo.getSelectionModel().clearSelection();
        pref.getSelectionModel().clearSelection();
        SOCKET.emit("createTransaction", type, isPref);
    }

    public void generatePDF(String fileName) {
        try {
            Document document = new Document();
            File file = new File(fileName + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);

            PdfWriter.getInstance(document, fos);

            if (!document.isOpen()) {
                document.open();
            }

            PdfDiv pdfTable = createContent(fileName);
            document.add(pdfTable);
            document.close();
            fos.close();

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

    private PdfDiv createContent(String string) {
        PdfDiv doc = new PdfDiv();
        BaseFont helvetica = null;
        try {
            helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            System.err.println("Error " + e);
        }
        Font fontTitle = new Font(helvetica, 24, Font.BOLD);
        Font fontSubTitle = new Font(helvetica, 16, Font.NORMAL);
        Font fontBody = new Font(helvetica, 10, Font.NORMAL);
        Paragraph title = new Paragraph("Bienvenido a", fontSubTitle);
        Paragraph branch = new Paragraph(" Banco Pistio", fontTitle);
        Paragraph body1 = new Paragraph("Turno: ", fontBody);
        Paragraph code = new Paragraph(string, fontTitle);
        Paragraph body2 = new Paragraph(new Date().toLocaleString(), fontBody);
        code.setAlignment(Element.ALIGN_CENTER);
        branch.setAlignment(Element.ALIGN_CENTER);
        title.setAlignment(Element.ALIGN_CENTER);
        body1.setAlignment(Element.ALIGN_CENTER);
        body2.setAlignment(Element.ALIGN_CENTER);
        body1.setPaddingTop(5);
        doc.addElement(title);
        doc.addElement(branch);
        doc.addElement(body1);
        doc.addElement(code);
        doc.addElement(body2);
        return doc;
    }

    private Label createLable(String text){
        return new Label(text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Label> tipos = FXCollections.observableArrayList(createLable("Caja"), createLable("Atención al Cliente"), createLable("Créditos"));
        ObservableList<Label> prefs = FXCollections.observableArrayList(createLable("Sin Preferencias"), createLable("Adulto Mayor"), createLable("Discapacidad"), createLable("Embarazo"));

        tipo.setItems(tipos);
        pref.setItems(prefs);

        SOCKET.on("newTransaction", args -> {
            generatePDF( args[0].toString());
        });
    }
}
