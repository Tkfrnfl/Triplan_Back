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
	kotlin("kapt") version "1.8.21"

	// 직렬화
	kotlin("plugin.serialization") version "1.8.21"
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
	maven { url = uri("https://jitpack.io") }
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

	//class 추가
	implementation("javax.xml.bind:jaxb-api:2.3.0")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.1.3")
	implementation("org.apache.httpcomponents.client5:httpclient5-fluent:5.1.3")

	//jwt
	implementation("com.googlecode.json-simple:json-simple:1.1.1")
	implementation("io.jsonwebtoken:jjwt:0.9.1")

	//graphql
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("com.graphql-java:graphql-java-extended-scalars:19.0")
	testImplementation("io.mockk:mockk:1.13.4")

	// querydsl 추가
	implementation("com.querydsl:querydsl-jpa:5.0.0")
	//kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
	//kapt kotlin java.lang.reflect.invocationtargetexception 빌드 에러발생
	kapt("org.springframework.boot:spring-boot-configuration-processor")

	//형태소분석기
	implementation("com.github.shin285:KOMORAN:3.3.4")

	testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
	testImplementation("io.kotest:kotest-assertions-core:5.5.5")
	testImplementation("com.ninja-squad:springmockk:3.0.1")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.security:spring-security-test")

	// 직렬화
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
	implementation ("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation ("com.fasterxml.jackson.core:jackson-databind")
	implementation ("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
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
