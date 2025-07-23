import sbt.*
import sbt.Keys.*

object Logging {

  /**
   * See https://stackoverflow.com/a/39296583
   */
  val logbackConfig: String =
    s"""
       |if [[ "$${ENV}" == "production" ]]; then
       |  echo "[Logback][info] Using Production configuration"
       |  addJava '-Dlogback.configurationFile=logback-json-prod.xml'
       |elif [[ "$${ENV}" == "staging" ]]; then
       |  echo "[Logback][info] Using Staging configuration"
       |  addJava '-Dlogback.configurationFile=logback-json-staging.xml'
       |else
       |  echo "[Logback][error] Missing `ENV` envvar or invalid value: '$${ENV}'. Should be either 'staging' or 'production'"
       |  exit 1
       |fi
       |""".stripMargin

  /**
   * Removes the `logback.xml` file from the JAR file.
   * This `logback.xml` file is used for development purposes only.
   */
  def excludeLogbackDevConfFromJar: Def.Setting[Task[Seq[(File, String)]]] =
    Compile / packageBin / mappings ~= { mappings => mappings.filterNot(_._2.endsWith("logback.xml")) }

}
