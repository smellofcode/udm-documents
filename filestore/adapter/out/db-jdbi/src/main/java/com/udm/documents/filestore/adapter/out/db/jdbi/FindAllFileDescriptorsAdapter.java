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
import com.udm.documents.filestore.domain.FileStatus;
import com.udm.documents.filestore.usecase.port.FindAllFileDescriptorsPort;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FindAllFileDescriptorsAdapter implements FindAllFileDescriptorsPort {

    private final Jdbi jdbi;

    @Override
    public List<FileDescriptor> findAll(int limit, int offset) {
        return jdbi.withHandle(handle -> handle.createQuery(
                        """
                            SELECT id, storage_path, status, file_name, content_type
                            FROM file_descriptor
                            WHERE status <> :wrongStatus
                            LIMIT :limit OFFSET :offset
                            """)
                .bind("wrongStatus", FileStatus.UPLOADING)
                .bind("limit", limit)
                .bind("offset", offset)
                .registerRowMapper(ConstructorMapper.factory(FileDescriptor.class))
                .mapTo(FileDescriptor.class)
                .list());
    }
}
