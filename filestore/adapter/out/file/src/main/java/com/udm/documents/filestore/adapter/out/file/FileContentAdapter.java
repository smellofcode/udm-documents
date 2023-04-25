/*
MIT License

Copyright (c) 2023 smellofcode

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.udm.documents.filestore.adapter.out.file;

import com.udm.documents.filestore.domain.FileDescriptor;
import com.udm.documents.filestore.domain.FileDescriptorNotFoundException;
import com.udm.documents.filestore.usecase.port.PersistFileContentPort;
import com.udm.documents.filestore.usecase.port.ReadFileContentPort;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class FileContentAdapter implements PersistFileContentPort, ReadFileContentPort {

    @Value("${udm.documents.filestore.uploadPath:/var/uploads}")
    private String uploadPath;

    @Value("${udm.documents.filestore.bufferSize:4096}")
    private int bufferSize;

    @Override
    public String persistContent(InputStream inputStream) {
        final var fileName = UUID.randomUUID().toString();
        var effectivePath = calculatePath(fileName);
        try {
            Files.createDirectories(effectivePath.getParent());
            Files.copy(new BufferedInputStream(inputStream, bufferSize), effectivePath);
            log.info("File {} has been saved", effectivePath);
            return fileName;
        } catch (IOException e) {
            throw new FileStorageException(effectivePath.toString(), e);
        }
    }

    @Override
    public InputStream read(FileDescriptor fileDescriptor) {
        var effectivePath = calculatePath(fileDescriptor.getStoragePath());
        try {
            return new BufferedInputStream(Files.newInputStream(effectivePath), bufferSize);
        } catch (IOException e) {
            throw new FileDescriptorNotFoundException(fileDescriptor);
        }
    }

    private Path calculatePath(String fileName) {
        return Paths.get(uploadPath, fileName);
    }
}
