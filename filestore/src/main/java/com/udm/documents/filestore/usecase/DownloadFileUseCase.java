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
package com.udm.documents.filestore.usecase;

import com.udm.documents.filestore.domain.FileDescriptor;
import com.udm.documents.filestore.usecase.port.FindFileDescriptorPort;
import com.udm.documents.filestore.usecase.port.ReadFileContentPort;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DownloadFileUseCase {

    private final FindFileDescriptorPort findFileDescriptorPort;
    private final ReadFileContentPort readFileContentPort;

    public Optional<DownloadFileResult> download(DownloadFileQuery query) {
        return findFileDescriptorPort
                .findById(query.fileDescriptorId())
                .filter(FileDescriptor::canBeDownloaded)
                .map(fileDescriptor -> new DownloadFileResult(
                        readFileContentPort.read(fileDescriptor),
                        fileDescriptor.getFileName(),
                        fileDescriptor.getContentType()));
    }

    public record DownloadFileQuery(UUID fileDescriptorId) {}

    public record DownloadFileResult(InputStream inputStream, String fileName, String contentType) {}
}
