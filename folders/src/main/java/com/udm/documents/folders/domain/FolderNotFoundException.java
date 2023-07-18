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
package com.udm.documents.folders.domain;

import java.util.UUID;
import lombok.Getter;

public class FolderNotFoundException extends RuntimeException {

    public static final String FOLDER_CANNOT_BE_FOUND = "No folder with id=%s was found.";

    public static final String FOLDER_TYPE_CANNOT_BE_FOUND = "No %s folder with id=%s was found.";

    @Getter
    public enum Type {
        PARENT("parent");
        public final String string;

        Type(String string) {
            this.string = string;
        }
    }

    public FolderNotFoundException(UUID id) {
        super(FOLDER_CANNOT_BE_FOUND.formatted(id.toString()));
    }

    public FolderNotFoundException(UUID id, Type type) {
        super(FOLDER_TYPE_CANNOT_BE_FOUND.formatted(type.getString(), id.toString()));
    }
}
