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
package com.udm.documents.filestore.usecase;

import com.udm.documents.filestore.domain.FileDescriptorView;
import com.udm.documents.filestore.usecase.port.FindAllFileDescriptorsPort;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetAllFileDescriptorsUseCase {

    private final FindAllFileDescriptorsPort findAllFileDescriptorsPort;

    public List<FileDescriptorView> apply(GetAllFileDescriptorsQuery query) {
        return findAllFileDescriptorsPort.findAll(query.limit(), query.offset()).stream()
                .map(FileDescriptorView::from)
                .toList();
    }

    public record GetAllFileDescriptorsQuery(int limit, int offset) {}
}
