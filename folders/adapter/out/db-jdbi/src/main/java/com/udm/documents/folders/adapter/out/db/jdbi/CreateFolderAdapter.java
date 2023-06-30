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

import com.udm.documents.folders.domain.Folder;
import com.udm.documents.folders.domain.FolderNotCreatedException;
import com.udm.documents.folders.usecase.port.CreateFolderPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Component
class CreateFolderAdapter implements CreateFolderPort {

    private final Jdbi jdbi;

    @Override
    public UUID create(final Folder folder) {
        jdbi.useTransaction(handle -> {
            final var insertCount = handle.createUpdate(
                            """
                                  INSERT INTO folder (id, name, parentId)
                                  VALUES (:id, :name, :parentId)
                                  ON CONFLICT Do NOTHING
                                  """)
                    .bindBean(folder)
                    .execute();

            if (insertCount == 0) {
                log.info("Failed to insert folder.");
                throw new FolderNotCreatedException();
            }
            log.info("Inserted folder.");
        });
        return folder.id();
    }
}
