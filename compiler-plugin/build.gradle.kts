plugins {
  pluginDevKit("compiler-plugin")
  id("module.publication")
}

pluginDevKit {
  componentRegistrar = "io.github.kyay10.wither.WitherPluginComponentRegistrar"
  commandLineProcessor = "io.github.kyay10.wither.WitherCommandLineProcessor"
}
