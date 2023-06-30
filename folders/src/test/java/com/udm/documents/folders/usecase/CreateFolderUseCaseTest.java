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

import static com.udm.documents.folders.domain.FolderNotCreatedException.FOLDER_CANNOT_BE_CREATED;
import static com.udm.documents.folders.domain.FolderNotFoundException.FOLDER_CANNOT_BE_FOUND;
import static com.udm.documents.folders.usecase.CreateFolderUseCase.CreateFolderCommand;
import static com.udm.documents.folders.usecase.CreateFolderUseCase.CreateFolderResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.udm.documents.folders.domain.Folder;
import com.udm.documents.folders.domain.FolderNotCreatedException;
import com.udm.documents.folders.domain.FolderNotFoundException;
import com.udm.documents.folders.usecase.port.CreateFolderPort;
import com.udm.documents.folders.usecase.port.GetFolderPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateFolderUseCaseTest {

    @InjectMocks
    private CreateFolderUseCase createFolderUseCase;

    @Mock
    private CreateFolderPort createFolderPort;

    @Mock
    private GetFolderPort getFolderPort;

    @Test
    void shouldCreateFolderAndReturnUuid_whenCalledWithValidParentId() {
        // given
        String folderName = UUID.randomUUID().toString();
        Folder parentFolder = Folder.create(null);
        CreateFolderCommand command = new CreateFolderCommand(folderName, parentFolder.id());

        when(getFolderPort.getById(parentFolder.id())).thenReturn(Optional.of(parentFolder));

        doAnswer(invocation -> {
                    var folder = invocation.getArgument(0, Folder.class);
                    assertThat(folder.name()).isEqualTo(folderName);
                    assertThat(folder.parentId()).isEqualTo(parentFolder.id());
                    assertThat(folder.id()).isNotNull();
                    return folder.id();
                })
                .when(createFolderPort)
                .create(any(Folder.class));

        // when
        final var result = createFolderUseCase.apply(command);

        // then
        assertThat(result).isNotNull().extracting(CreateFolderResult::id).isNotNull();
        verify(getFolderPort).getById(parentFolder.id());
        verify(createFolderPort).create(any(Folder.class));
    }

    @Test
    void shouldCreateFolderAndReturnUuid_whenCalledWithNoParentId() {
        // given
        String folderName = UUID.randomUUID().toString();
        CreateFolderCommand command = new CreateFolderCommand(folderName, null);

        doAnswer(invocation -> {
                    var folder = invocation.getArgument(0, Folder.class);
                    assertThat(folder.name()).isEqualTo(folderName);
                    assertThat(folder.parentId()).isNull();
                    assertThat(folder.id()).isNotNull();
                    return folder.id();
                })
                .when(createFolderPort)
                .create(any(Folder.class));

        // when
        final var result = createFolderUseCase.apply(command);

        // then
        assertThat(result).isNotNull().extracting(CreateFolderResult::id).isNotNull();
        verify(getFolderPort, never()).getById(any(UUID.class));
        verify(createFolderPort).create(any(Folder.class));
    }

    @Test
    void shouldThrowException_whenCreatingFolder_andCalledWithInvalidParentId() {
        // given
        var folderName = UUID.randomUUID().toString();
        var invalidId = UUID.randomUUID();
        var command = new CreateFolderCommand(folderName, invalidId);

        when(getFolderPort.getById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> createFolderUseCase.apply(command))
                .isInstanceOf(FolderNotFoundException.class)
                .hasMessage(FOLDER_CANNOT_BE_FOUND, invalidId);
        verify(getFolderPort).getById(invalidId);
        verify(createFolderPort, never()).create(any(Folder.class));
    }

    @Test
    void shouldThrowException_whenCreatingFolder_andCreationConflicts() {
        // given
        String folderName = UUID.randomUUID().toString();
        Folder parentFolder = Folder.create(null);
        CreateFolderCommand command = new CreateFolderCommand(folderName, parentFolder.id());

        when(getFolderPort.getById(parentFolder.id())).thenReturn(Optional.of(parentFolder));

        doThrow(new FolderNotCreatedException()).when(createFolderPort).create(any(Folder.class));

        // When & Then
        assertThatThrownBy(() -> createFolderUseCase.apply(command))
                .isInstanceOf(FolderNotCreatedException.class)
                .hasMessage(FOLDER_CANNOT_BE_CREATED);
        verify(getFolderPort).getById(parentFolder.id());
        verify(createFolderPort).create(any(Folder.class));
    }
}
