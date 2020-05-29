package com.javier.pistio.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static void changeView(StackPane root, AnchorPane rootPane, String url, boolean showDialog) {
        JFXDialog dialog = alert(root, rootPane, "Cargando", null);
        if(!showDialog) dialog.close();
        new Thread(() -> {
            try {
                Node anchorPane = FXMLLoader.load(Util.class.getResource(url));
                Platform.runLater(() -> {
                    if(rootPane != null)
                        rootPane.getChildren().setAll(anchorPane);
                    else
                        root.getChildren().clear();
                        root.getChildren().add(anchorPane);
                });
            } catch (IOException e) {
                Platform.runLater(() -> alert(root, rootPane, "Error", "Error al cargar el archivo especificado en la ruta \"" + url + "\""));
                System.err.println("Error: " + e);
            }
            Platform.runLater(dialog::close);
        }).start();
    }

    public static JFXDialog alert(StackPane context, AnchorPane fx, String textHeader, String textBody) {
        BoxBlur blur = new BoxBlur(3,3,3);

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(context, content, JFXDialog.DialogTransition.CENTER);
        content.setHeading(new Text(textHeader));
        JFXSpinner spinner = new JFXSpinner();
        spinner.setPrefSize(30, 30);
        if(textBody != null){
            Label l = new Label(textBody);
            l.getStyleClass().add("alert-txt");
            content.setBody(l);
            JFXButton actBut = new JFXButton("OK");
            actBut.setOnAction(event -> {
                dialog.close();
            });
            repeatFocus(actBut);
            actBut.getStyleClass().add("btn");
            content.setActions(actBut);
        }else{
            repeatFocus(spinner);
            content.setBody(spinner);
            dialog.setOverlayClose(false);
        }

        dialog.setOnDialogClosed(event -> {
            if(fx != null)
                fx.setEffect(null);
        });

        if(fx != null)
            fx.setEffect(blur);

        dialog.show();

        return dialog;
    }

    private static void repeatFocus(Node node) {
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                repeatFocus(node);
            }
        });
    }

    public static String addZeros(int correl){
        if(correl < 10) return "00" + correl;
        if(correl < 100) return "00" + correl;
        return String.valueOf(correl);
    }

    public static void playAudio(String filePath){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Util.class.getResourceAsStream(filePath)));
            clip.start();
        } catch(UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.out.println("Error al reproducir el sonido." + ex);
        }
    }

    public static void generatePDF(String fileName) {
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

    private static PdfDiv createContent(String string) {
        PdfDiv doc = new PdfDiv();
        BaseFont helvetica = null;
        try {
            helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            System.err.println("Error " + e);
        }
        com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(helvetica, 24, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontSubTitle = new com.itextpdf.text.Font(helvetica, 16, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Font fontBody = new com.itextpdf.text.Font(helvetica, 10, Font.NORMAL);
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

    public static Label createLabel(String text){
        return new Label(text);
    }
}
