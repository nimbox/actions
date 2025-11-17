plugins {
	id("java-gradle-plugin")
	id("maven-publish")
}

gradlePlugin {
	plugins {
		create("versioning") {
			id = "com.nimbox.tools.versioning"
			implementationClass = "com.nimbox.tools.versioning.VersioningPlugin"
		}
	}
}

publishing {
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/nimbox/maven")
			credentials {
				username = providers.environmentVariable("GITHUB_ACTOR").orNull
				password = providers.environmentVariable("GITHUB_TOKEN").orNull
			}
		}
	}
}
