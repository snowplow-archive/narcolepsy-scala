import sbt._
class OrderlyScalaClientProject(info: ProjectInfo) extends DefaultProject(info)
{
	val squeryl = "org.squeryl" %% "squeryl" % "0.9.4"
	val posgresDriver = "postgresql" % "postgresql" % "8.4-701.jdbc4"
}
