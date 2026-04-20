package co.edu.com.inrf.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
    private JTextArea textArea;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public TextAreaOutputStream(JTextArea textArea) { this.textArea = textArea; }

    @Override
    public void write(int b) {
        buffer.write(b);
        if (b == '\n') {
            try {
                // Convertimos el búfer completo usando UTF-8 para no romper caracteres
                String text = buffer.toString("UTF-8");
                textArea.append(text);
                buffer.reset();
                textArea.setCaretPosition(textArea.getDocument().getLength());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
