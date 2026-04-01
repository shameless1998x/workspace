package com.example.orderservice;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HelloDocxGenerator {

    // 生成一份示例 hello.docx 文件，方便直接打开查看效果
    public static void main(String[] args) throws IOException {
        Path output = Path.of("docs/generated/hello.docx");
        Files.createDirectories(output.getParent());

        try (XWPFDocument document = new XWPFDocument();
             OutputStream outputStream = Files.newOutputStream(output)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("hello");
            document.write(outputStream);
        }
    }
}
