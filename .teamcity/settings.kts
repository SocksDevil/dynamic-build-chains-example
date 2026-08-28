import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
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

project {

    vcsRoot(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    vcsRoot(HttpsGithubComSocksDevilVcsSettingsDynamicBuildChainsRefsHeadsMain)

    buildType(FinalStep)
    buildType(Generator)
    buildType(Snapshot)
    buildType(DynamicBuildChain)

    template(DefaultTemplate)
}

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

    vcs {
        root(HttpsGithubComSocksDevilVcsSettingsDynamicBuildChainsRefsHeadsMain)
    }

    steps {
        script {
            id = "Maven2"
            scriptContent = """/Users/Andre.Rocha/Applications/IntelliJ\ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn org.jetbrains.teamcity:teamcity-configs-maven-plugin:2026.1-eap17:generate -f .teamcity/pom.xml"""
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
    name = "https://github.com/SocksDevil/vcs-settings-dynamic-build-chains#refs/heads/main"
    url = "https://github.com/SocksDevil/vcs-settings-dynamic-build-chains"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
    }
})
