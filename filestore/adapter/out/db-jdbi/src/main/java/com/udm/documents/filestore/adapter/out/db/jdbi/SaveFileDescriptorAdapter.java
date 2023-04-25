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

import com.udm.documents.filestore.domain.FileDescriptor;
import com.udm.documents.filestore.usecase.port.SaveFileDescriptorPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
class SaveFileDescriptorAdapter implements SaveFileDescriptorPort {

    private final Jdbi jdbi;

    @Override
    public void save(FileDescriptor fileDescriptor) {
        jdbi.useTransaction(handle -> {
            final var insertCount = handle.createUpdate(
                            """
                                        INSERT INTO file_descriptor (id, storage_path, status, file_name, content_type)
                                        VALUES (:id, :storagePath, :status, :fileName, :contentType)
                                        ON CONFLICT DO NOTHING
                                    """)
                    .bindBean(fileDescriptor)
                    .execute();

            if (insertCount == 1) {
                log.info("Inserted file_descriptor.");
                return;
            }

            final var updateCount = handle.createUpdate(
                            """
                                    UPDATE file_descriptor
                                    SET storage_path = :storagePath, status = :status, file_name = :fileName, content_type = :contentType
                                    WHERE id=:id
                            """)
                    .bindBean(fileDescriptor)
                    .execute();

            if (updateCount == 0) {
                throw new IllegalStateException("FileDescriptor %s has apparently be deleted."
                        .formatted(fileDescriptor.getId().toString()));
            }

            log.info("Updated file_descriptor; count={}", updateCount);
        });
    }
}
