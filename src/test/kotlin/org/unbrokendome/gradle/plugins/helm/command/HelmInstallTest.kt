package org.unbrokendome.gradle.plugins.helm.command

import org.spekframework.spek2.style.specification.describe
import org.unbrokendome.gradle.plugins.helm.command.tasks.HelmInstall
import org.unbrokendome.gradle.plugins.helm.spek.ExecutionResultAwareSpek
import org.unbrokendome.gradle.plugins.helm.spek.applyPlugin
import org.unbrokendome.gradle.plugins.helm.spek.gradleExecMock
import org.unbrokendome.gradle.plugins.helm.spek.gradleTask
import org.unbrokendome.gradle.plugins.helm.spek.setupGradleProject
import org.unbrokendome.gradle.plugins.helm.testutil.exec.singleInvocation
import org.unbrokendome.gradle.plugins.helm.testutil.execute


object HelmInstallTest : ExecutionResultAwareSpek({

    setupGradleProject { applyPlugin<HelmCommandsPlugin>() }

    val execMock by gradleExecMock()

    val task by gradleTask<HelmInstall> {
        releaseName.set("awesome-release")
        chart.set("custom/awesome")
    }


    withOptionsTesting(
        GlobalOptionsTests,
        GlobalServerOptionsTests,
        ServerOperationOptionsTests("install"),
        InstallFromRepositoryOptionsTests("install")
    ) {

        describe("executing a HelmInstall task") {

            it("should execute helm install") {

                task.execute()

                execMock.singleInvocation {
                    expectCommand("install")
                    expectArg("awesome-release")
                    expectArg("custom/awesome")
                }
            }


            it("should use replace property") {
                task.replace.set(true)

                task.execute()


                execMock.singleInvocation {
                    expectCommand("install")
                    expectFlag("--replace")
                    expectArg("awesome-release")
                    expectArg("custom/awesome")
                }
            }
        }
    }
})
