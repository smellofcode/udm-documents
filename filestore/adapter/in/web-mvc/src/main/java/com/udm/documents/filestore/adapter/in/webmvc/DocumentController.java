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
package com.udm.documents.filestore.adapter.in.webmvc;

import com.udm.documents.filestore.adapters.in.web.mvc.generated.api.DocumentsResourceApi;
import com.udm.documents.filestore.adapters.in.web.mvc.generated.api.model.DocumentResource;
import com.udm.documents.filestore.domain.FileDescriptor;
import com.udm.documents.filestore.usecase.DownloadFileUseCase;
import com.udm.documents.filestore.usecase.GetAllFileDescriptorsUseCase;
import com.udm.documents.filestore.usecase.GetFileDescriptorUseCase;
import com.udm.documents.filestore.usecase.UploadFileUseCase;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
class DocumentController implements DocumentsResourceApi {

    private final UploadFileUseCase uploadFileUseCase;

    private final DownloadFileUseCase downloadFileUseCase;

    private final GetAllFileDescriptorsUseCase getAllFileDescriptorsUseCase;

    private final GetFileDescriptorUseCase getFileDescriptorUseCase;

    @Override
    public ResponseEntity<DocumentResource> uploadFile(MultipartFile file) {
        try {
            final var command = new UploadFileUseCase.UploadFileCommand(
                    file.getInputStream(), file.getOriginalFilename(), file.getContentType(), file.getSize());
            final var response = uploadFileUseCase.apply(command);
            final var resultResource = new DocumentResource();
            resultResource.setId(response.id().toString());
            return ResponseEntity.of(Optional.of(resultResource));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String id) {
        return downloadFileUseCase
                .download(new DownloadFileUseCase.DownloadFileQuery(UUID.fromString(id)))
                .map(result -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(result.contentType()))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"%s\"".formatted(result.fileName()))
                        .body((Resource) new InputStreamResource(result.inputStream())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<DocumentResource>> getAllFiles() {
        return ResponseEntity.ok(
                getAllFileDescriptorsUseCase
                        .apply(new GetAllFileDescriptorsUseCase.GetAllFileDescriptorsQuery(100, 0))
                        .stream()
                        .map(DocumentController::toResource)
                        .toList());
    }

    @Override
    public ResponseEntity<DocumentResource> getFileDescriptor(String id) {
        return ResponseEntity.of(getFileDescriptorUseCase
                .apply(new GetFileDescriptorUseCase.GetFileDescriptorQuery(UUID.fromString(id)))
                .map(DocumentController::toResource));
    }

    private static DocumentResource toResource(FileDescriptor descriptor) {
        final var result = new DocumentResource();
        result.setId(descriptor.getId().toString());
        result.setName(descriptor.getFileName());
        result.setContentType(descriptor.getContentType());
        return result;
    }
}
