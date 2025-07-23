import sbt.*

object Libraries {

  val zioVersion            = "2.1.20"
  val slf4jVersion          = "2.0.17"
  val zioConfigVersion      = "4.0.4"
  val zioHttpVersion        = "3.3.3"
  val flywayVersion         = "11.10.3"
  val zioSchemaVersion      = "1.7.3"
  val zioJsonVersion        = "0.7.44"
  val magnumVersion         = "2.0.0-M2"
  val preludeVersion        = "1.0.0-RC41"
  val testContainersVersion = "0.43.0"

  val zio                             = "dev.zio"                      %% "zio"                                        % zioVersion
  val prelude                         = "dev.zio"                      %% "zio-prelude"                                % preludeVersion
  val zioHttpTestkit                  = "dev.zio"                      %% "zio-http-testkit"                           % zioHttpVersion
  val zioLogging                      = "dev.zio"                      %% "zio-logging-slf4j2"                         % "2.5.1"
  val logback                         = "ch.qos.logback"                % "logback-classic"                            % "1.5.18"
  val zioJson                         = "dev.zio"                      %% "zio-json"                                   % zioJsonVersion
  val zioJsonGolden                   = "dev.zio"                      %% "zio-json-golden"                            % zioJsonVersion
  val zioRedis                        = "dev.zio"                      %% "zio-redis"                                  % "1.1.5"

  val zioHttp = Seq(
    "dev.zio" %% "zio-http-shaded"                 % zioHttpVersion,
    // See https://zio.dev/zio-http/reference/server/#optional-netty-transport-types-uring
    // Needs to be kept in-sync with the versio of Netty used in zio-http
    "io.netty" % "netty-transport-native-io_uring" % "4.2.1.Final"
  )

  val zioConfig = Seq(
    "dev.zio" %% "zio-config"          % zioConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  )

  val zioSchema = Seq(
    "dev.zio" %% "zio-schema"            % zioSchemaVersion,
    "dev.zio" %% "zio-schema-json"       % zioSchemaVersion,
    "dev.zio" %% "zio-schema-derivation" % zioSchemaVersion,
  )

  val tests = Seq(
    "dev.zio" %% "zio-test"            % zioVersion,
    "dev.zio" %% "zio-test-sbt"        % zioVersion,
    "dev.zio" %% "zio-test-magnolia"   % zioVersion,
    "dev.zio" %% "zio-mock"            % "1.0.0-RC12",
    "dev.zio" %% "zio-schema-zio-test" % zioSchemaVersion,
    zioHttpTestkit,
    logback,
  )

  val loggingRuntime = Seq(
    logback,
    "net.logstash.logback" % "logstash-logback-encoder" % "8.1",
    "org.slf4j"            % "jul-to-slf4j"             % slf4jVersion,
    "org.slf4j"            % "log4j-over-slf4j"         % slf4jVersion,
    "org.slf4j"            % "jcl-over-slf4j"           % slf4jVersion,
    "org.slf4j"            % "slf4j-api"                % slf4jVersion,
  )

  val flyway = Seq(
    "org.flywaydb" % "flyway-core"                % flywayVersion,
    "org.flywaydb" % "flyway-database-postgresql" % flywayVersion,
  )

  val postgresql = {
    val osArch =
      System.getProperty("os.arch") match {
        case "aarch64" => "arm64v8"
        case _         => "amd64"
      }

    /**
     * See https://github.com/zonkyio/embedded-postgres/issues/41
     */
    val osVersion =
      System.getProperty("os.name").toLowerCase match {
        case osName if osName.contains("mac")   =>
          "embedded-postgres-binaries-darwin"
        case osName if osName.contains("win")   =>
          "embedded-postgres-binaries-windows"
        case osName if osName.contains("linux") =>
          "embedded-postgres-binaries-linux"
        case osName                             => throw new RuntimeException(s"Unknown operating system $osName")
      }

    Seq(
      "org.postgresql"         % "postgresql"          % "42.7.7",
      "com.zaxxer"             % "HikariCP"            % "6.3.1",
      "io.zonky.test"          % "embedded-postgres"   % "2.1.0"  % Test,
      "io.zonky.test.postgres" % s"$osVersion-$osArch" % "17.5.0" % Test,
    )
  }

  val testContainers = Seq(
    "com.dimafeng"            %% "testcontainers-scala"               % testContainersVersion % Test,
    "com.dimafeng"            %% "testcontainers-scala-localstack-v2" % testContainersVersion % Test,
    "com.github.sideeffffect" %% "zio-testcontainers"                 % "0.6.0"               % Test,
    "com.dimafeng"            %% "testcontainers-scala-redis"         % testContainersVersion % Test,
  )

  val magnum = Seq(
    "com.augustnagro" %% "magnumzio" % magnumVersion,
    "com.augustnagro" %% "magnumpg"  % magnumVersion,
  )
}
