package com.teixeira.gdx.writer

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ProjectWriter(
  val language: String,
  val projectName: String,
  val packageName: String,
  val minSdk: String,
  val targetSdk: String,
) {

  companion object {
    const val ANDROIDIDEPROJECTS = "/sdcard/AndroidIDEProjects"
    val SOURCE_FILES_REGEX = Regex("^(gradle|java|kt|xml)$")
  }

  fun write(sendMessage: (message: String) -> Unit) {
    val projectDir = File(ANDROIDIDEPROJECTS, projectName)

    if (projectDir.exists()) {
      sendMessage("There is already a project with this name!")
      return
    }

    projectDir.mkdirs()
    unzipTemplate(projectDir, sendMessage)
  }

  fun unzipTemplate(projectDir: File, sendMessage: (message: String) -> Unit) {
    val resourceStream = ProjectWriter::class.java.getResourceAsStream("/template_$language.zip")
    if (resourceStream == null) {
      sendMessage("Template ZIP file not found!")
      return
    }

    resourceStream.use { inputStream ->
      sendMessage("Creating project...")

      val buffer = ByteArray(1024)
      val zis = ZipInputStream(inputStream)

      var entry: ZipEntry? = zis.nextEntry
      while (entry != null) {
        var name = entry.name
        if (name.contains("\$package_name")) {
          name = name.replace("\$package_name", packageName.replace(".", "/"))
        }

        val file = File(projectDir, name)

        if (entry.isDirectory) {
          file.mkdirs()
        } else {
          file.parentFile?.mkdirs()

          if (file.extension.matches(SOURCE_FILES_REGEX)) {
            var content = zis.readBytes().toString(Charsets.UTF_8)

            file.writeText(
              content
                .replace("\$project_name", projectName)
                .replace("\$package_name", packageName)
                .replace("\$minSdk", minSdk)
                .replace("\$targetSdk", targetSdk),
              Charsets.UTF_8,
            )
          } else {
            val fos = FileOutputStream(file)
            var len: Int
            while (zis.read(buffer).also { len = it } > 0) {
              fos.write(buffer, 0, len)
            }
          }
        }

        entry = zis.nextEntry
      }
      zis.closeEntry()
      zis.close()

      unzipGradleWrapper(projectDir, sendMessage)
    }
  }

  private fun unzipGradleWrapper(projectDir: File, sendMessage: (message: String) -> Unit) {
    val resourceStream = ProjectWriter::class.java.getResourceAsStream("/gradle_wrapper.zip")
    if (resourceStream == null) {
      sendMessage("Gradle Wrapper ZIP file not found!")
      return
    }

    resourceStream.use { inputStream ->
      val buffer = ByteArray(1024)
      val zis = ZipInputStream(inputStream)

      var entry: ZipEntry? = zis.nextEntry
      while (entry != null) {
        val file = File(projectDir, entry.name)
        if (entry.isDirectory) {
          file.mkdirs()
        } else {
          file.parentFile?.mkdirs()
          val fos = FileOutputStream(file)
          var len: Int
          while (zis.read(buffer).also { len = it } > 0) {
            fos.write(buffer, 0, len)
          }
        }

        entry = zis.nextEntry
      }
      zis.closeEntry()
      zis.close()
    }
    sendMessage("Project created.")
  }
}
