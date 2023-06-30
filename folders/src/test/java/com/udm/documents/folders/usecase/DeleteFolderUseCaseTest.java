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

import static com.udm.documents.folders.usecase.DeleteFolderUseCase.DeleteFolderCommand;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.udm.documents.folders.domain.FolderNotDeletedException;
import com.udm.documents.folders.usecase.port.DeleteFolderPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteFolderUseCaseTest {

    @InjectMocks
    private DeleteFolderUseCase deleteFolderUseCase;

    @Mock
    private DeleteFolderPort deleteFolderPort;

    @Test
    void shouldDeleteFolder_whenCalledWithValidId() {
        // given
        var command = new DeleteFolderCommand(UUID.randomUUID());
        doNothing().when(deleteFolderPort).delete(command.folderId());
        // when
        deleteFolderUseCase.apply(command);

        // then
        verify(deleteFolderPort).delete(command.folderId());
    }

    @Test
    void shouldThrowException_whenCalledWithInvalidId() {
        // given
        var command = new DeleteFolderCommand(UUID.randomUUID());
        doThrow(new FolderNotDeletedException(command.folderId()))
                .when(deleteFolderPort)
                .delete(command.folderId());

        // When & Then
        assertThatThrownBy(() -> deleteFolderUseCase.apply(command))
                .isInstanceOf(FolderNotDeletedException.class)
                .hasMessage(FolderNotDeletedException.FOLDER_NOT_DELETED.formatted(command.folderId()));
        verify(deleteFolderPort).delete(command.folderId());
    }
}
