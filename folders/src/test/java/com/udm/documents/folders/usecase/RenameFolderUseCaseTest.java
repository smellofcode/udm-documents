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

import static com.udm.documents.folders.domain.FolderNotFoundException.FOLDER_CANNOT_BE_FOUND;
import static com.udm.documents.folders.domain.FolderNotUpdatedException.FOLDER_NOT_UPDATED;
import static com.udm.documents.folders.usecase.RenameFolderUseCase.RenameFolderCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.udm.documents.folders.domain.Folder;
import com.udm.documents.folders.domain.FolderNotFoundException;
import com.udm.documents.folders.domain.FolderNotUpdatedException;
import com.udm.documents.folders.usecase.port.GetFolderPort;
import com.udm.documents.folders.usecase.port.UpdateFolderPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RenameFolderUseCaseTest {

    @InjectMocks
    private RenameFolderUseCase renameFolderUseCase;

    @Mock
    private UpdateFolderPort updateFolderPort;

    @Mock
    private GetFolderPort getFolderPort;

    @Test
    void shouldRenameFolder_whenCalledWithValidNameAndId() {
        // given
        var folder = Folder.create(UUID.randomUUID().toString());
        when(getFolderPort.getById(folder.id())).thenReturn(Optional.of(folder));
        var command = new RenameFolderCommand(folder.id(), UUID.randomUUID().toString());
        doAnswer(invocation -> {
                    var argument = invocation.getArgument(0, Folder.class);
                    assertThat(argument.id()).isEqualTo(command.folderId());
                    assertThat(argument.name()).isEqualTo(command.newName());
                    return null;
                })
                .when(updateFolderPort)
                .update(any(Folder.class));

        // when
        renameFolderUseCase.apply(command);

        // then
        verify(getFolderPort).getById(command.folderId());
        verify(updateFolderPort).update(any(Folder.class));
    }

    @Test
    void shouldThrowException_whenRenamingFolder_andCalledWithInvalidId() {
        // given
        var invalidId = UUID.randomUUID();
        var command = new RenameFolderCommand(invalidId, UUID.randomUUID().toString());
        // When & Then
        assertThatThrownBy(() -> renameFolderUseCase.apply(command))
                .isInstanceOf(FolderNotFoundException.class)
                .hasMessage(FOLDER_CANNOT_BE_FOUND, invalidId);
        verify(getFolderPort).getById(invalidId);
        verify(updateFolderPort, never()).update(any(Folder.class));
    }

    @Test
    void shouldThrowException_whenRenamingFolder_andRenameConflicts() {
        // given
        var folder = Folder.create(UUID.randomUUID().toString());
        when(getFolderPort.getById(folder.id())).thenReturn(Optional.of(folder));
        var command = new RenameFolderCommand(folder.id(), UUID.randomUUID().toString());
        doThrow(new FolderNotUpdatedException(folder.id()))
                .when(updateFolderPort)
                .update(any(Folder.class));

        // When & Then
        assertThatThrownBy(() -> renameFolderUseCase.apply(command))
                .isInstanceOf(FolderNotUpdatedException.class)
                .hasMessage(FOLDER_NOT_UPDATED, folder.id());
        verify(getFolderPort).getById(folder.id());
        verify(updateFolderPort).update(any(Folder.class));
    }
}
