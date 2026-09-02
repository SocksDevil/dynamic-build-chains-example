import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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

val isDynamicChain = "IS_DYNAMIC_CHAIN"
project {
    vcsRoot(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    if (!System.getProperty(isDynamicChain).toBoolean()) {
        params {
            password("agent_username", "credentialsJSON:a54bc4a8-5c6a-4d9e-94df-d55a68d6bc47")
            password("agent_password", "credentialsJSON:a54bc4a8-5c6a-4d9e-94df-d55a68d6bc47")
        }

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
        script {
            id = "Maven2"
            scriptContent =
                """
                    cd .teamcity/
                    
                    curl -sSf -u "%agent_username%:%agent_password%" -o dsl-context.zip "%DSL_CONTEXT_URL%"
                    
                    JAVA_HOME=~/.asdf/installs/java/corretto-21.0.11.10.1 mvn -Dteamcity.versionedSettings.exposeInternalParameters=true -Dteamcity.internal.dsl.IS_DYNAMIC_CHAIN=true -DserverContext=dsl-context.zip teamcity-configs:generate -f pom.xml
                    
                    rm -rf "target/generated-configs/%DSL_RELATIVE_ROOT_ID%"
                    """.trimIndent()
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
