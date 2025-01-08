plugins {
    id("module-config")
    id("com.adarshr.test-logger") version "4.0.0"
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudAzureVersion"] = "5.18.0"

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-amqp") // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA
    implementation("org.springframework.boot:spring-boot-starter-jdbc") // JDBC
    implementation("org.springframework.boot:spring-boot-starter-web") // Web MVC
    implementation("org.springframework.boot:spring-boot-starter-websocket") // WebSocket
    implementation("org.springframework.boot:spring-boot-starter-security") // Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server") // OAuth2 Resource Server
    implementation("org.springframework.boot:spring-boot-starter-actuator") // Actuator
    implementation("org.springframework.boot:spring-boot-starter-webflux") // Reactive Web (WebFlux)
    implementation("com.azure.spring:spring-cloud-azure-starter-actuator")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test") // Includes JUnit 5
    testImplementation("org.springframework.boot:spring-boot-testcontainers") // For integration tests with Testcontainers
}

dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:${property("springCloudAzureVersion")}")
    }
}
subprojects {
    apply {
        plugin("com.adarshr.test-logger")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Unit Tests
val unitTest by tasks.registering(Test::class) {
    description = "Runs unit tests."
    group = "verification"
    useJUnitPlatform()
    include("**/unit/**")
}

// Integration Tests
val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()
    include("**/integration/**")
}

// Add unitTest and integrationTest to the check lifecycle
tasks.named("check") {
    dependsOn(unitTest, integrationTest)
}