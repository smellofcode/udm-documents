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
package com.udm.documents.folders.adapter.out.db.jdbi;

import static org.assertj.core.api.Assertions.assertThat;

import com.udm.documents.folders.domain.Folder;
import java.util.UUID;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UpdateFolderAdapterTest {

    @Autowired
    private Jdbi jdbi;

    private UpdateFolderAdapter testedObject;

    private CreateFolderAdapter createFolderAdapter;
    private GetFolderAdapter getFolderAdapter;

    @BeforeEach
    void beforeEach() {
        testedObject = new UpdateFolderAdapter(jdbi);
        createFolderAdapter = new CreateFolderAdapter(jdbi);
        getFolderAdapter = new GetFolderAdapter(jdbi);
    }

    @Test
    void shouldUpdateFolder_whenUpdatingWithNoChanges() {
        // Given
        var folder = Folder.create(UUID.randomUUID().toString()).withNewParent(UUID.randomUUID());

        createFolderAdapter.create(folder);

        // When
        testedObject.update(folder);

        // Then
        var result = getFolderAdapter.getById(folder.id());

        assertThat(result).isPresent().hasValueSatisfying(present -> {
            assertThat(present.id()).isEqualTo(folder.id());
            assertThat(present.name()).isEqualTo(folder.name());
            assertThat(present.parentId()).isEqualTo(folder.parentId());
        });
    }

    @Test
    void shouldUpdateFolderName_whenRenaming() {
        // Given
        var folder = Folder.create(UUID.randomUUID().toString());

        createFolderAdapter.create(folder);

        var renamedFolder = folder.withNewName(UUID.randomUUID().toString());

        // When
        testedObject.update(renamedFolder);

        // Then
        var result = getFolderAdapter.getById(renamedFolder.id());

        assertThat(result).isPresent().hasValueSatisfying(present -> {
            assertThat(present.id()).isEqualTo(renamedFolder.id());
            assertThat(present.name()).isEqualTo(renamedFolder.name());
            assertThat(present.parentId()).isEqualTo(renamedFolder.parentId());
        });
    }

    @Test
    void shouldUpdateFolderName_whenMoving() {
        // Given
        var folder = Folder.create(UUID.randomUUID().toString()).withNewParent(UUID.randomUUID());

        createFolderAdapter.create(folder);

        var movedFolder = folder.withNewParent(UUID.randomUUID());

        // When
        testedObject.update(movedFolder);

        // Then
        var result = getFolderAdapter.getById(movedFolder.id());

        assertThat(result).isPresent().hasValueSatisfying(present -> {
            assertThat(present.id()).isEqualTo(movedFolder.id());
            assertThat(present.name()).isEqualTo(movedFolder.name());
            assertThat(present.parentId()).isEqualTo(movedFolder.parentId());
        });
    }
}
