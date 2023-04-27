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
package com.udm.documents.filestore.adapter.in.webmvc;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.udm.documents.filestore.usecase.DownloadFileUseCase;
import com.udm.documents.filestore.usecase.GetAllFileDescriptorsUseCase;
import com.udm.documents.filestore.usecase.GetFileDescriptorUseCase;
import com.udm.documents.filestore.usecase.UploadFileUseCase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    private static final String BASE_URL = "/documents";

    @MockBean
    private UploadFileUseCase uploadFileUseCase;

    @MockBean
    private DownloadFileUseCase downloadFileUseCase;

    @MockBean
    private GetAllFileDescriptorsUseCase getAllFileDescriptorsUseCase;

    @MockBean
    private GetFileDescriptorUseCase getFileDescriptorUseCase;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldReturn200IfNoDocumentsAreFound() throws Exception {
        // Given
        when(getAllFileDescriptorsUseCase.apply(new GetAllFileDescriptorsUseCase.GetAllFileDescriptorsQuery(100, 0)))
                .thenReturn(List.of());
        // When
        final var resultActions = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL));
        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnDocumentResourcesIfAnyAreFound() throws Exception {
        // Given
        final var resources = List.of(
                TestDataFactory.createFileDescriptor("foo.txt", "application/text"),
                TestDataFactory.createFileDescriptor("bar.json", "application/json"),
                TestDataFactory.createFileDescriptor("bar2.png", "application/png"));
        when(getAllFileDescriptorsUseCase.apply(new GetAllFileDescriptorsUseCase.GetAllFileDescriptorsQuery(100, 0)))
                .thenReturn(resources);
        // When
        final var resultActions = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL));
        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(content()
                        .json(
                                """
                        [
                            {
                                "id": %s,
                                "name": "foo.txt",
                                "contentType": "application/text"
                            },
                            {
                                "id": %s,
                                "name": "bar.json",
                                "contentType": "application/json"
                            },
                            {
                                "id": %s,
                                "name": "bar2.png",
                                "contentType": "application/png"
                            }
                        ]
                        """
                                        .formatted(
                                                resources.get(0).id().toString(),
                                                resources.get(1).id().toString(),
                                                resources.get(2).id().toString())));
    }
}
