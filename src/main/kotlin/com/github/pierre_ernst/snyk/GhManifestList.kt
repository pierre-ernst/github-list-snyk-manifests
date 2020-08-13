package com.github.pierre_ernst.snyk

import org.kohsuke.github.GHContent
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import java.io.PrintStream
import kotlin.system.exitProcess

class GhManifestList(
        private val ghClient: GitHub,
        private val org: String,
        private val repo: String?) {

    private fun searchRepo(out: PrintStream, ghRepo: GHRepository) {
        val results = emptySet<GHContent>().toMutableSet()

        // List of supported manifest files
        // as per https://github.com/snyk/snyk/blob/v1.373.1/help/file.txt#L11
        arrayOf(
                "yarn.lock",
                "package-lock.json",
                "package.json",
                "Gemfile",
                "Gemfile.lock",
                "pom.xml",
                "build.gradle",
                "build.gradle.kts",
                "build.sbt",
                "Pipfile",
                "requirements.txt",
                "Gopkg.lock",
                "go.mod",
                "vendor/vendor.json",
                "project.assets.json",
                "packages.config",
                "paket.dependencies",
                "composer.lock",
                "Podfile",
                "Podfile.lock"
        ).forEach { manifest ->
            results.addAll(
                    ghClient.searchContent()
                            .q("org:$org")
                            .repo("${ghRepo.name}")
                            .q(manifest)
                            .`in`("path")
                            .list().toList()
            )
        }


        results.toSortedSet(compareBy { ghContent ->
            ghContent.path
        }).forEach() { ghContent ->
            out.println("\"$org\", \"${ghRepo.name}\", \"${ghContent.path}\"")
        }

    }

    private fun list(out: PrintStream) {
        out.println("\"org\", \"repo\", \"manifest\"")

        val ghOrg = ghClient.getOrganization(org)
        if (repo != null) {
            searchRepo(out, ghOrg.getRepository(repo))
        } else {
            ghOrg.repositories.values.forEach() { ghRepo ->
                searchRepo(out, ghRepo)
            }
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            if (System.getenv("GH_PAT").isNullOrEmpty()) {
                System.err.println("Environment variable GH_PAT not found (GitHub personal access token).")
                exitProcess(1)
            }

            if (args.isEmpty() || (args.size > 2)) {
                System.err.println("Usage:\tjava ${GhManifestList::class.simpleName} <org> [<repo>]")
                exitProcess(2)
            }

            GhManifestList(
                    GitHubBuilder().withOAuthToken(System.getenv("GH_PAT")).build(),
                    args[0],
                    if (args.size == 2) args[1] else null
            ).list(System.out)
        }

    }

}
