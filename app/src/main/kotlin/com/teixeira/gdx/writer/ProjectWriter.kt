package com.teixeira.gdx.writer

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ProjectWriter(
  val name: String,
  val packageName: String,
  val minSdk: String,
  val targetSdk: String,
) {

  companion object {
    const val ANDROIDIDEPROJECTS = "/sdcard/AndroidIDEProjects"
  }

  fun write(sendMessage: (message: String) -> Unit) {
    val projectDir = File(ANDROIDIDEPROJECTS, name)

    if (projectDir.exists()) {
      sendMessage("There is already a project with this name!")
      return
    } else {
      projectDir.mkdirs()
    }

    sendMessage("Creating project...")
    unzipTemplate(projectDir, sendMessage)
    sendMessage("Project created...")
  }

  fun unzipTemplate(projectDir: File, sendMessage: (message: String) -> Unit) {
    val resourceStream = ProjectWriter::class.java.getResourceAsStream("/template_kotlin.zip")
    if (resourceStream == null) {
      sendMessage("Template ZIP file not found!")
      return
    }

    resourceStream.use { inputStream ->
      sendMessage("Extracting template")
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

          if (file.extension == "gradle" || file.extension == "kt" || file.extension == "xml") {
            var content = zis.readBytes().toString(Charsets.UTF_8)

            file.writeText(
              content
                .replace("\$project_name", name)
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
      sendMessage("Template extracted!")
    }
  }
}
