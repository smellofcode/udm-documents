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
package com.udm.documents.folders.adapter.in.webmvc;

import static com.udm.documents.folders.usecase.CreateFolderUseCase.CreateFolderCommand;

import com.udm.documents.folders.domain.FolderNotCreatedException;
import com.udm.documents.folders.domain.FolderNotFoundException;
import com.udm.documents.folders.usecase.CreateFolderUseCase;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
class CreateFolderRestAdapter {

    private final CreateFolderUseCase useCase;

    @PostMapping("/folders")
    public ResponseEntity<String> createFolder(@RequestBody CreateFolderRequestBody requestBody) {
        var command = new CreateFolderCommand(requestBody.name(), requestBody.parentId());
        try {
            UUID folderId = useCase.apply(command).id();
            return new ResponseEntity<>(String.valueOf(folderId), HttpStatus.CREATED);
        } catch (FolderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (FolderNotCreatedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record CreateFolderRequestBody(String name, UUID parentId) {}
}
