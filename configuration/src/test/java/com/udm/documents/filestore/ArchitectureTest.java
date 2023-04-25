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
package com.udm.documents.filestore;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = {"com.udm.documents"})
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule domainCannotImportUseCases = noClasses()
            .that()
            .resideInAPackage("..domain")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..usecase..")
            .because("domain code cannot depend on use cases");

    @ArchTest
    public static final ArchRule coreCannotImportAdapters = noClasses()
            .that()
            .resideInAnyPackage("..domain..", "..usecase..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..adapters..")
            .because("core cannot directly import any adapters (if you have such dependency in gradle, remove it)");

    @ArchTest
    public static final ArchRule useCaseCannotDependOnAnotherUseCase = noClasses()
            .that()
            .resideInAPackage("..usecase")
            .and()
            .haveSimpleNameEndingWith("UseCase")
            .should()
            .dependOnClassesThat()
            .haveSimpleNameEndingWith("UseCase")
            .andShould()
            .resideInAPackage("..usecase")
            .because("use cases are not reusable; use package protected services for reusability");

    @ArchTest
    public static final ArchRule useCasesCanOnlyBePlacedInUsecasePackage =
            classes().that().haveSimpleNameEndingWith("UseCase").should().resideInAPackage("..usecase");

    @ArchTest
    public static final ArchRule portsCanOnlyBePlacedInPortPackage =
            classes().that().haveSimpleNameEndingWith("Port").should().resideInAPackage("..port");

    @ArchTest
    public static final ArchRule portsShouldBeJavaInterfaces = classes()
            .that()
            .resideInAPackage("..usecase.port")
            .and()
            .haveSimpleNameEndingWith("Port")
            .should()
            .beInterfaces()
            .because("implementation of ports must be placed in adapter module");
}
