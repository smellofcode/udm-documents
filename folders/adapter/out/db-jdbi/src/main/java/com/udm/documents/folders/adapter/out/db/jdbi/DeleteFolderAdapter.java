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

import com.udm.documents.folders.domain.FolderNotDeletedException;
import com.udm.documents.folders.usecase.port.DeleteFolderPort;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
class DeleteFolderAdapter implements DeleteFolderPort {

    private final Jdbi jdbi;

    @Override
    public void delete(final UUID folderId) {
        jdbi.useTransaction(handle -> {
            final var deleteCount = handle.createUpdate(
                            """
                                                  DELETE FROM folder
                                                  WHERE :id = id
                                                  """)
                    .bind("id", folderId)
                    .execute();

            if (deleteCount == 0) {
                log.info("Failed to delete folder with provided id=%s".formatted(folderId));
                throw new FolderNotDeletedException(folderId);
            }
            log.info("Deleted folder with id=%s".formatted(folderId));
        });
    }
}
