package org.example.backend_fivegivechill.Config;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    // Không tính sẵn content‑length vì ta không biết trước
    @Override
    public long contentLength() throws IOException {
        return -1;
    }
}
