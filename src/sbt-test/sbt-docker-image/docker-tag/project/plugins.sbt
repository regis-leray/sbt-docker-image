{
  sys.props.get("plugin.version") match {
    case Some(v) =>
      addSbtPlugin("com.github.regis-leray" % "sbt-docker-image" % v)
    case None =>
      sys.error("The system property 'plugin.version' is not defined. Specify this property using the scriptedLaunchOpts -D.")
  }
}