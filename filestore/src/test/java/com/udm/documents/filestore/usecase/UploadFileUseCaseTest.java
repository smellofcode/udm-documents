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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.udm.documents.filestore.domain.FileDescriptor;
import com.udm.documents.filestore.domain.FileStatus;
import com.udm.documents.filestore.usecase.port.PersistFileContentPort;
import com.udm.documents.filestore.usecase.port.SaveFileDescriptorPort;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadFileUseCaseTest {

    @InjectMocks
    private UploadFileUseCase uploadFileUseCase;

    @Mock
    private PersistFileContentPort persistFileContentPort;

    @Mock
    private SaveFileDescriptorPort saveFileDescriptorPort;

    @Test
    void shouldUploadFileAndReturnUploadFileResult() {
        // Given
        final var inputStream = mock(InputStream.class);
        final var originalFileName = "test.txt";
        final var contentType = "text/plain";
        final var fileSize = 1024L;
        final var storagePath = "path/to/storage";

        final var command =
                new UploadFileUseCase.UploadFileCommand(inputStream, originalFileName, contentType, fileSize);

        when(persistFileContentPort.persistContent(any(InputStream.class))).thenReturn(storagePath);

        final var callCount = new AtomicInteger(0);

        doAnswer(invocation -> {
                    var count = callCount.incrementAndGet();
                    var fileDescriptor = invocation.getArgument(0, FileDescriptor.class);
                    switch (count) {
                        case 1 -> {
                            assertThat(fileDescriptor.getStatus()).isEqualTo(FileStatus.UPLOADING);
                        }
                        case 2 -> {
                            assertThat(fileDescriptor.getStatus()).isEqualTo(FileStatus.UPLOADED);
                            assertThat(fileDescriptor.getStoragePath()).isEqualTo(storagePath);
                        }
                        default -> fail("Illegal number of call counts of save method: %d".formatted(count));
                    }
                    assertThat(fileDescriptor.getFileName()).isEqualTo(originalFileName);
                    assertThat(fileDescriptor.getContentType()).isEqualTo(contentType);
                    return null;
                })
                .when(saveFileDescriptorPort)
                .save(any(FileDescriptor.class));

        // When
        final var result = uploadFileUseCase.apply(command);

        // Then
        assertThat(result).isNotNull();
        final var id = result.id();
        assertThat(id).isNotNull();
        verify(persistFileContentPort).persistContent(inputStream);
        verify(saveFileDescriptorPort, times(2)).save(any(FileDescriptor.class));
    }
}
