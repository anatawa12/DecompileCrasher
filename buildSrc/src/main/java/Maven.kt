import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*;

fun PublicationContainer.creatingDecompileCrasher(project: Project) = creating(MavenPublication::class) {
    from(project.components["java"])

    pom {
        description.set("A obfuscater with invokedynamic")
        url.set("https://github.com/anatawa12/DecompileCrasher")

        scm {
            url.set("https://github.com/anatawa12/DecompileCrasher")
            connection.set("scm:git@github.com:anatawa12/DecompileCrasher.git")
            developerConnection.set("scm:git@github.com:anatawa12/DecompileCrasher.git")
        }

        issueManagement {
            system.set("github")
            url.set("https://github.com/anatawa12/DecompileCrasher/issues")
        }

        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/anatawa12/DecompileCrasher/blob/master/LICENSE")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("anatawa12")
                name.set("anatawa12")
                roles.set(setOf("developer"))
            }
        }
    }
}

fun RepositoryHandler.addOSSRH(project: Project) {
    maven {
        name = "mavenCentral"
        url = if (project.version.toString().endsWith("SNAPSHOT"))
            project.uri("https://oss.sonatype.org/content/repositories/snapshots")
        else project.uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")

        credentials {
            username = project.findProperty("com.anatawa12.sonatype.username")?.toString() ?: ""
            password = project.findProperty("com.anatawa12.sonatype.passeord")?.toString() ?: ""
        }
    }
}
