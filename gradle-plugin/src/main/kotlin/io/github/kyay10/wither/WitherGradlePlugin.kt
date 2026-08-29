package io.github.kyay10.wither

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused") // Used via reflection.
class WitherGradlePlugin : DevKitSupportPlugin(PluginInfo.PLUGIN_INFO) {
  override fun apply(target: Project) {
    target.extensions.create("wither", WitherGradleExtension::class.java)
  }

  override fun Project.applyToCompilation(
    kotlinCompilation: KotlinCompilation<*>
  ): Provider<List<SubpluginOption>> = provider {
    val extension = project.extensions.getByType(WitherGradleExtension::class.java)
    emptyList()
  }
}
