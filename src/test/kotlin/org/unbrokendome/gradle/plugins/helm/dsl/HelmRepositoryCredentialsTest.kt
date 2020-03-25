package org.unbrokendome.gradle.plugins.helm.dsl

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.unbrokendome.gradle.plugins.helm.HelmPlugin
import org.unbrokendome.gradle.plugins.helm.dsl.credentials.CertificateCredentials
import org.unbrokendome.gradle.plugins.helm.dsl.credentials.CredentialsContainer
import org.unbrokendome.gradle.plugins.helm.dsl.credentials.PasswordCredentials
import org.unbrokendome.gradle.plugins.helm.dsl.credentials.credentials
import org.unbrokendome.gradle.plugins.helm.spek.applyPlugin
import org.unbrokendome.gradle.plugins.helm.spek.setupGradleProject
import org.unbrokendome.gradle.plugins.helm.testutil.assertions.containsItem
import org.unbrokendome.gradle.plugins.helm.testutil.assertions.fileValue
import org.unbrokendome.gradle.plugins.helm.testutil.assertions.isPresent
import java.io.File


object HelmRepositoryCredentialsTest : Spek({

    val project by setupGradleProject { applyPlugin<HelmPlugin>() }


    describe("repository with password credentials") {

        beforeEachTest {
            with(project.helm.repositories) {
                create("myRepo") { repo ->
                    repo.url.set(project.uri("http://repository.example.com"))
                    repo.credentials {
                        username.set("username")
                        password.set("password")
                    }
                }
            }
        }

        it("should create a PasswordCredentials object") {
            assertThat(project.helm.repositories, name = "repositories")
                .containsItem("myRepo")
                .prop(CredentialsContainer::configuredCredentials)
                .isPresent().isInstanceOf(PasswordCredentials::class)
                .all {
                    prop(PasswordCredentials::username).isPresent().isEqualTo("username")
                    prop(PasswordCredentials::password).isPresent().isEqualTo("password")
                }
        }
    }


    describe("repository with certificate credentials") {

        beforeEachTest {
            with(project.helm.repositories) {
                create("myRepo") { repo ->
                    repo.url.set(project.uri("http://repository.example.com"))
                    repo.credentials(CertificateCredentials::class) {
                        certificateFile.set(project.file("/path/to/certificate"))
                        keyFile.set(project.file("/path/to/key"))
                    }
                }
            }
        }

        it("should create a CertificateCredentials object") {
            assertThat(project.helm.repositories, name = "repositories")
                .containsItem("myRepo")
                .prop(CredentialsContainer::configuredCredentials)
                .isPresent().isInstanceOf(CertificateCredentials::class)
                .all {
                    prop(CertificateCredentials::certificateFile).fileValue()
                        .isEqualTo(File("/path/to/certificate"))
                    prop(CertificateCredentials::keyFile).fileValue()
                        .isEqualTo(File("/path/to/key"))
                }
        }
    }
})
