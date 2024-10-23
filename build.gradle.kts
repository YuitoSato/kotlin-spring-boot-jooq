plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.3.4"
  id("io.spring.dependency-management") version "1.1.6"
  id("nu.studer.jooq") version "8.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-jooq")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.0")
  implementation("org.springframework.boot:spring-boot-starter-aop")

  runtimeOnly("org.postgresql:postgresql")

  // JOOQ Codegen（jooqGeneratorで指定）
  jooqGenerator("org.postgresql:postgresql")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jooq {
  version.set("3.18.6")
  configurations {
    create("main") {
      generateSchemaSourceOnCompilation.set(false)
      jooqConfiguration.apply {
        jdbc.apply {
          driver = "org.postgresql.Driver"
          url = "jdbc:postgresql://localhost:5432/postgres"
          user = "postgres"
          password = "password"
        }
        generator.apply {
          name = "org.jooq.codegen.DefaultGenerator"
          database.apply {
            name = "org.jooq.meta.postgres.PostgresDatabase"
            inputSchema = "public"
          }
          generate.apply {
            isDaos = true
            isPojos = true
          }
          target.apply {
            packageName = "com.example.kotlinspringbootjooq.jooq"
            directory = "build/generated-src/jooq/main"
          }
        }
      }
    }
  }
}
//
//tasks.named<JavaCompile>("compileJava") {
//  dependsOn("generateJooq")
//}


kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
