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
package com.udm.documents.filestore.adapter.out.db.jdbi;

import static org.assertj.core.api.Assertions.assertThat;

import com.udm.documents.filestore.domain.FileDescriptor;
import java.util.Optional;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SaveFileDescriptorAdapterTest {

    private static final String TEST_FILE_NAME = "file.txt";

    private static final String TEST_CONTENT_TYPE = "application/text";

    private static final String TEST_STORAGE_PATH = "test-storage-path";

    @Autowired
    private Jdbi jdbi;

    private SaveFileDescriptorAdapter testedObject;

    @BeforeEach
    void beforeEach() {
        testedObject = new SaveFileDescriptorAdapter(jdbi);
    }

    @Test
    void shouldInsertNewFileDescriptorToTheDB() {
        // Given
        final var fileDescriptor = FileDescriptor.create(TEST_FILE_NAME, TEST_CONTENT_TYPE);
        // When
        testedObject.save(fileDescriptor);
        // Then
        var storedDescriptor = queryForFileDescriptor(fileDescriptor);
        assertThat(storedDescriptor).isPresent();
        assertThat(storedDescriptor).hasValueSatisfying(present -> {
            assertThat(present.getStatus()).isEqualTo(fileDescriptor.getStatus());
            assertThat(present.getFileName()).isEqualTo(fileDescriptor.getFileName());
            assertThat(present.getStoragePath()).isEqualTo(fileDescriptor.getStoragePath());
            assertThat(present.getContentType()).isEqualTo(fileDescriptor.getContentType());
        });
    }

    @Test
    void shouldUpdateFileDescriptorInTheDB() {
        // Given
        final var fileDescriptor = FileDescriptor.create(TEST_FILE_NAME, TEST_CONTENT_TYPE);
        testedObject.save(fileDescriptor);
        // When
        fileDescriptor.confirmUploaded(TEST_STORAGE_PATH);
        testedObject.save(fileDescriptor);
        // then
        final var storedDescriptor = queryForFileDescriptor(fileDescriptor);
        assertThat(storedDescriptor).hasValueSatisfying(present -> {
            assertThat(present.getStatus()).isEqualTo(fileDescriptor.getStatus());
            assertThat(present.getFileName()).isEqualTo(fileDescriptor.getFileName());
            assertThat(present.getStoragePath()).isEqualTo(fileDescriptor.getStoragePath());
            assertThat(present.getContentType()).isEqualTo(fileDescriptor.getContentType());
        });
        assertThat(queryForFileDescriptorCount()).isEqualTo(1);
    }

    private Optional<FileDescriptor> queryForFileDescriptor(FileDescriptor fileDescriptor) {
        return jdbi.withHandle(handle -> handle.registerRowMapper(ConstructorMapper.factory(FileDescriptor.class))
                .createQuery("SELECT * FROM file_descriptor WHERE id=:id")
                .bind("id", fileDescriptor.getId())
                .mapTo(FileDescriptor.class)
                .findFirst());
    }

    private int queryForFileDescriptorCount() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM file_descriptor")
                .mapTo(Integer.class)
                .one());
    }
}
