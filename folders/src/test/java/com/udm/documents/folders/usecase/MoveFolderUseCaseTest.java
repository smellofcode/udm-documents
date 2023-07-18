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

import static com.udm.documents.folders.domain.FolderNotFoundException.*;
import static com.udm.documents.folders.usecase.MoveFolderUseCase.MoveFolderCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.udm.documents.folders.domain.Folder;
import com.udm.documents.folders.domain.FolderNotFoundException;
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
class MoveFolderUseCaseTest {

    @InjectMocks
    private MoveFolderUseCase testedObject;

    @Mock
    private UpdateFolderPort updateFolderPort;

    @Mock
    private GetFolderPort getFolderPort;

    @Test
    void shouldMoveFolder_whenCalledWithValidIdAndParentId() {
        // given
        var folder = Folder.create(UUID.randomUUID().toString());
        var parent = Folder.create(UUID.randomUUID().toString());

        when(getFolderPort.getById(folder.id())).thenReturn(Optional.of(folder));
        when(getFolderPort.getById(parent.id())).thenReturn(Optional.of(parent));
        var command = new MoveFolderCommand(folder.id(), parent.id());
        doAnswer(invocation -> {
                    var argument = invocation.getArgument(0, Folder.class);
                    assertThat(argument.id()).isEqualTo(command.id());
                    assertThat(argument.parentId()).isEqualTo(command.newParentId());
                    return null;
                })
                .when(updateFolderPort)
                .update(any(Folder.class));

        // when
        testedObject.apply(command);

        // then
        verify(getFolderPort, times(2)).getById(any(UUID.class));
        verify(updateFolderPort).update(any(Folder.class));
    }

    @Test
    void shouldMoveFolder_whenCalledWithValidIdAndCurrentParentId() {
        // given
        var parent = Folder.create(UUID.randomUUID().toString());
        var folder = Folder.create(UUID.randomUUID().toString()).withNewParent(parent.id());

        when(getFolderPort.getById(folder.id())).thenReturn(Optional.of(folder));
        when(getFolderPort.getById(parent.id())).thenReturn(Optional.of(parent));
        var command = new MoveFolderCommand(folder.id(), parent.id());
        doAnswer(invocation -> {
                    var argument = invocation.getArgument(0, Folder.class);
                    assertThat(argument.id()).isEqualTo(command.id());
                    assertThat(argument.parentId()).isEqualTo(command.newParentId());
                    return null;
                })
                .when(updateFolderPort)
                .update(any(Folder.class));

        // when
        testedObject.apply(command);

        // then
        verify(getFolderPort, times(2)).getById(any(UUID.class));
        verify(updateFolderPort).update(any(Folder.class));
    }

    @Test
    void shouldThrowFolderNotFoundException_whenAttemptingToMoveNonexistentFolder() {
        // given
        var invalidId = UUID.randomUUID();

        when(getFolderPort.getById(invalidId)).thenReturn(Optional.empty());

        var command = new MoveFolderCommand(invalidId, UUID.randomUUID());

        // when & then
        assertThatThrownBy(() -> testedObject.apply(command))
                .isInstanceOf(FolderNotFoundException.class)
                .hasMessage(FOLDER_CANNOT_BE_FOUND, invalidId);

        verify(getFolderPort, atMostOnce()).getById(any(UUID.class));
        verify(updateFolderPort, never()).update(any(Folder.class));
    }

    @Test
    void shouldThrowFolderNotFoundException_whenAttemptingToMoveToNonexistentParentFolder() {
        // given
        var folder = Folder.create(UUID.randomUUID().toString());
        var invalidParentId = UUID.randomUUID();

        when(getFolderPort.getById(folder.id())).thenReturn(Optional.of(folder));
        when(getFolderPort.getById(invalidParentId)).thenReturn(Optional.empty());

        var command = new MoveFolderCommand(folder.id(), invalidParentId);

        // when & then
        assertThatThrownBy(() -> testedObject.apply(command))
                .isInstanceOf(FolderNotFoundException.class)
                .hasMessage(FOLDER_TYPE_CANNOT_BE_FOUND, Type.PARENT.getString(), invalidParentId);

        verify(getFolderPort, times(2)).getById(any(UUID.class));
        verify(updateFolderPort, never()).update(any(Folder.class));
    }
}
