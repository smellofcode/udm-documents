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
package com.udm.documents.folders.usecase;

import com.udm.documents.folders.domain.Folder;
import com.udm.documents.folders.domain.FolderNotFoundException;
import com.udm.documents.folders.usecase.port.CreateFolderPort;
import com.udm.documents.folders.usecase.port.GetFolderPort;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CreateFolderUseCase {

    private final CreateFolderPort createFolderPort;

    private final GetFolderPort getFolderPort;

    @Transactional
    public CreateFolderResult apply(@NonNull CreateFolderCommand command) {
        if (command.parentId() == null) {
            return new CreateFolderResult(createFolderPort.create(Folder.create(command.name())));
        } else {
            final var parentFolder = getFolderPort
                    .getById(command.parentId())
                    .orElseThrow(() -> new FolderNotFoundException(command.parentId()));
            final var newFolder = Folder.create(command.name()).withNewParent(parentFolder.id());
            return new CreateFolderResult(createFolderPort.create(newFolder));
        }
    }

    public record CreateFolderCommand(String name, UUID parentId) {}

    public record CreateFolderResult(UUID id) {}
}
