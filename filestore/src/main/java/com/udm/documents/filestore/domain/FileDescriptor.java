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
package com.udm.documents.filestore.domain;

import java.util.UUID;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PACKAGE)
@ToString
public class FileDescriptor {
    private UUID id;

    private String fileName;
    private FileStatus status;
    private String storagePath;

    private String contentType;

    public static FileDescriptor create(String fileName, String contentType) {
        return FileDescriptor.builder()
                .id(UUID.randomUUID())
                .status(FileStatus.UPLOADING)
                .fileName(fileName)
                .contentType(contentType)
                .build();
    }

    public void confirmUploaded(String storagePath) {
        if (status != FileStatus.UPLOADING) {
            throw new IllegalStateException();
        }
        this.status = FileStatus.UPLOADED;
        this.storagePath = storagePath;
    }

    public boolean canBeDownloaded() {
        return status != FileStatus.UPLOADING;
    }
}
