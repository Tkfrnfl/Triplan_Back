import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"

	kotlin("plugin.jpa")version "1.7.22"
	// allOpen에서 지정한 어노테이션으로 만든 클래스에 open 키워드 적용
	kotlin("plugin.allopen") version "1.7.22"

	// 인자 없는 기본 생성자를 자동 생성
	// - Hibernate가 사용하는 Reflection API에서 Entity를 만들기 위해 인자 없는 기본 생성자가 필요함
	kotlin("plugin.noarg") version "1.7.22"
	kotlin("kapt") version "1.7.10"
}

allOpen {
	annotation("javax.persistence.Entity")
}
noArg {
	annotation("javax.persistence.Entity")
}


group = "com.Triplan"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//postgres
	implementation("org.postgresql:postgresql:42.5.0")
	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-log4j2
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.6")

	//security
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")

	//jwt
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("com.googlecode.json-simple:json-simple:1.1.1")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	//graphql
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	testImplementation("io.mockk:mockk:1.13.4")

	// querydsl 추가
	implementation("com.querydsl:querydsl-jpa:5.0.0")
	kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
	testImplementation("io.kotest:kotest-assertions-core:5.5.5")
	testImplementation("com.ninja-squad:springmockk:3.0.1")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
