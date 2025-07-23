import org.typelevel.scalacoptions.{ScalacOption, ScalacOptions}

object SbtTpolecatExtensions {

  /**
   * We're not enabling "full optimisation" (ie. '-opt:inline:**') because this provoke bugs
   *
   * See:
   *  - https://github.com/scala/bug/issues/11812
   *  - https://github.com/typelevel/sbt-tpolecat#release-mode
   *  - https://github.com/typelevel/sbt-tpolecat/blob/main/plugin/src/main/scala/io/github/davidgregory084/ScalacOptions.scala#L695-L706
   *  - https://docs.scala-lang.org/overviews/compiler-options/optimizer.html#using-and-interacting-with-the-optimizer
   */
  val optimiserConfig: Set[ScalacOption] = ScalacOptions.optimizerOptions("zio.datastar.poc.**")

}
