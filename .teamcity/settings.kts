import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import java.io.File

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2026.1"

project {
    vcsRoot(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    if (!DynamicChainUtils.myParams[DynamicChainUtils.IS_DYNAMIC_CHAIN].toBoolean()) {
        vcsRoot(HttpsGithubComSocksDevilVcsSettingsDynamicBuildChainsRefsHeadsMain)
        buildType(FinalStep)
        buildType(Generator)
        buildType(Snapshot)
        buildType(DynamicBuildChain)
    } else {
        subProject(DynamicPRChecks)
    }

    template(DefaultTemplate)
}

object DynamicPRChecks : Project({
    name = "Dynamic PR Checks"
    buildType(Dynamic_Build)
    buildType(Dynamic_Build2)
})

object DynamicBuildChain : BuildType({
    name = "Dynamic Build Chain"

    params {
        param("GENERATOR_BUILD_TYPE_ID", "DynamicBuildChains_Generator")
    }



    steps {
        script {
            id = "simpleRunner"
            scriptContent = """
                ls
                rm -r *
            """.trimIndent()
        }
    }
})

object FinalStep : BuildType({
    name = "Final Step"

    vcs {
        root(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    }

    steps {
        gradle {
            id = "gradle_runner"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    dependencies {
        snapshot(DynamicBuildChain) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object Generator : BuildType({
    name = "Generator"

    artifactRules = ".teamcity/target/generated-configs/ => .teamcity/generated_settings.zip"

    params {
        param("DSL_CONTEXT_URL", "${DslContext.serverUrl}/app/dsl-context?projectExtId=${DslContext.projectId}")
        param("DSL_RELATIVE_ROOT_ID", "${DslContext.projectId}")
    }

    vcs {
        root(HttpsGithubComSocksDevilVcsSettingsDynamicBuildChainsRefsHeadsMain)
    }

    steps {
        step {
            id = "jetbrains_dynamic_build_chain_settings_generator_1_0_0"
            type = "jetbrains/dynamic-build-chain-settings-generator@1.0.0"
            param("agent_username", "credentialsJSON:a54bc4a8-5c6a-4d9e-94df-d55a68d6bc47")
            param("plugin.docker.imagePlatform", "")
            param("agent_password", "credentialsJSON:a54bc4a8-5c6a-4d9e-94df-d55a68d6bc47")
            param("plugin.docker.imageId", "")
            param("teamcity.step.phase", "")
            param("SERVER_URL", "%teamcity.serverUrl%")
            param("plugin.docker.run.parameters", "")
            param("PROJECT_ID", "DynamicBuildChains")
        }

    }
})

object Snapshot : BuildType({
    name = "Snapshot"

    vcs {
        root(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    }

    steps {
        gradle {
            id = "gradle_runner"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }
})

object DefaultTemplate : Template({
    name = "Default Template"

    params {
        param("meow", "meow")
    }
})


object Dynamic_Build : BuildType({
    name = "Build"

    artifactRules = """
        big_file.txt
        folder1/*.vsix => folder1/
    """.trimIndent()

    params {
        param("teamcity.internal.artifactUpload.webPublisher.enableRetrier", "true")
        param("teamcity.internal.dynamic", "true")
    }

    vcs {
        root(AbsoluteId("VcsRootInRoot"))
        root(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    }

    steps {
        gradle {
            id = "gradle_runner_1"
            enabled = false
            tasks = "clean build"
            gradleWrapperPath = ""
        }
        script {
            id = "gradle_runner"
            scriptContent = """
                #!/bin/bash
                mkdir folder1
                dd if=/dev/urandom bs=100 count=1 | base64 > build/test_file.vsix
                dd if=/dev/urandom bs=100 count=1 | base64 > build/test_file_2.vsix
                zip -r folder1/meow.vsix build/*
            """.trimIndent()
        }
    }

    triggers {
        vcs {
        }
    }
    dependencies {
        dependency(AbsoluteId("DynamicBuildChains_Snapshot")) {
            snapshot {
                onDependencyCancel = FailureAction.ADD_PROBLEM
                reuseBuilds = ReuseBuilds.NO
            }
        }
    }
})

object Dynamic_Build2 : BuildType({
    name = "Build2"

    artifactRules = """
        big_file.txt
        folder1/*.vsix => folder1/
        folder2/*.vsix => folder2/
    """.trimIndent()


    params {
        param("teamcity.internal.artifactUpload.webPublisher.enableRetrier", "true")
        param("teamcity.internal.dynamic", "true")
    }

    vcs {
        root(AbsoluteId("AwsLambdaPluginExample"))
    }

    steps {
        gradle {
            id = "gradle_runner_1"
            enabled = false
            tasks = "clean build"
            gradleWrapperPath = ""
        }
        script {
            id = "gradle_runner"
            scriptContent = """
                #!/bin/bash
                mkdir folder2
                dd if=/dev/urandom bs=100 count=1 | base64 > build/test_file_3.vsix
                dd if=/dev/urandom bs=100 count=1 | base64 > build/test_file_4.vsix
                zip -r folder2/meow.vsix build/*
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Dynamic_Build) {
            snapshot {
            }

            artifacts {
                cleanDestination = true
                artifactRules = """
                    folder1 => folder1
                """.trimIndent()
            }
        }

        dependency(Snapshot) {
            snapshot {
                onDependencyCancel = FailureAction.ADD_PROBLEM
                reuseBuilds = ReuseBuilds.NO
            }
        }
    }

    triggers {
        vcs {
        }
    }
})


object HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/SocksDevil/teamcity-aws-lambda-plugin-example#refs/heads/main"
    url = "https://github.com/SocksDevil/teamcity-aws-lambda-plugin-example"
    branch = "refs/heads/main"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
    }
})

object HttpsGithubComSocksDevilVcsSettingsDynamicBuildChainsRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/SocksDevil/dynamic-build-chains-example#refs/heads/main"
    url = "https://github.com/SocksDevil/dynamic-build-chains-example"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
    }
})


object DynamicChainUtils {
    const val IS_DYNAMIC_CHAIN = "IS_DYNAMIC_CHAIN"

    const val PARAMS_FILE = "params.properties"

    val myParams: Map<String, String> by lazy {
        try {
            val file = File(DslContext.baseDir, PARAMS_FILE)
            if (!file.isFile) return@lazy emptyMap()

            file.readLines()
                .filter { it.isNotBlank() && !it.startsWith("#") }
                .mapNotNull { line ->
                    val i = line.indexOf('=')
                    if (i <= 0) null else line.take(i).trim() to line.substring(i + 1).trim()
                }
                .toMap()
                .also { println("DYNAMIC_CHAIN_PARAMS " + it.entries.joinToString(",") { e -> "${e.key}=${e.value}" }) }
        } catch (e: Exception) {
            println("Unable to read $PARAMS_FILE: ${e.message}")
            emptyMap()
        }
    }
}
