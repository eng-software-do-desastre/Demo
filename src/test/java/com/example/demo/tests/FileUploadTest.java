
package com.example.demo.tests;

import com.example.demo.pages.FileUploadPage;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest extends BaseTest {

    @Test
    void uploadDeFicheiroSimples() throws IOException {
        // 1) Criar ficheiro temporário (conteúdo irrelevante para o teste)
        Path temp = Files.createTempFile("demo-upload-", ".txt");
        Files.writeString(temp, "conteudo de teste");

        // 2) Abrir página e escolher o ficheiro
        FileUploadPage page = new FileUploadPage(driver).open();
        page.chooseFile(temp.toAbsolutePath().toString());

        // 3) Submeter e validar o nome apresentado
        page.submit();
        String shown = page.uploadedFileName();

        assertEquals(temp.getFileName().toString(), shown,
                "O nome do ficheiro apresentado deve coincidir com o enviado");
    }
}
