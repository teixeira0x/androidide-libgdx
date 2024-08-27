package com.teixeira.gdx

import com.teixeira.gdx.command.DEFAULT_LANGUAGE
import com.teixeira.gdx.command.DEFAULT_MIN_SDK
import com.teixeira.gdx.command.DEFAULT_TARGET_SDK
import com.teixeira.gdx.command.OPT_LANGUAGE
import com.teixeira.gdx.command.OPT_MIN_SDK
import com.teixeira.gdx.command.OPT_NAME
import com.teixeira.gdx.command.OPT_PACKAGE
import com.teixeira.gdx.command.OPT_TARGET_SDK
import com.teixeira.gdx.command.getCommandOptions
import com.teixeira.gdx.command.printCommandHelp
import com.teixeira.gdx.writer.ProjectWriter
import kotlin.system.exitProcess
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser

fun main(args: Array<String>) {
  val app = App()
  exitProcess(
    if (args.isNotEmpty()) {
      app.run(args)
    } else {
      app.runApp()
    }
  )
}

class App {

  companion object {
    val REGEX_JAVA_OR_KOTLIN = Regex("^(java|kotlin)$")
  }

  // Run command
  fun run(args: Array<String>): Int {
    val command: CommandLine? =
      try {
        DefaultParser().parse(getCommandOptions(), args)
      } catch (e: Exception) {
        println(e.localizedMessage)
        println()
        printCommandHelp()
        null
      }
    if (command == null) {
      return 1
    }

    val name = command.getOptionValue(OPT_NAME).trim()
    val packageName = command.getOptionValue(OPT_PACKAGE).trim()
    val language = command.getOptionValue(OPT_LANGUAGE, DEFAULT_LANGUAGE).trim()
    val minSdk = command.getOptionValue(OPT_MIN_SDK, DEFAULT_MIN_SDK).trim()
    val targetSdk = command.getOptionValue(OPT_TARGET_SDK, DEFAULT_TARGET_SDK).trim()

    if (!language.matches(REGEX_JAVA_OR_KOTLIN)) {
      print("Choose between kotlin or java language.")
      return 1
    }

    val writer = ProjectWriter(language, name, packageName, minSdk, targetSdk)
    writer.write { message -> println(message) }

    return 0
  }

  // Run application
  fun runApp(): Int {
    println("Welcome to the LibGDX project builder for AndroidIDE!")
    println("Let's start creating")
    println()

    print("Enter the project name:")
    val name = readLine()
    if (name?.isEmpty() ?: true) {
      println("The name cannot be empty!")
      return 1
    }

    print("Enter the app package:")
    val packageName = readLine()
    if (packageName?.isEmpty() ?: true) {
      println("The app package cannot be empty!")
      return 1
    }

    print("Enter the project language (Default $DEFAULT_LANGUAGE):")
    var language = readLine()
    if (language?.isEmpty() ?: true) {
      language = DEFAULT_LANGUAGE
    }

    if (!language!!.matches(REGEX_JAVA_OR_KOTLIN)) {
      print("Choose between kotlin or java language.")
      return 1
    }

    print("Enter the project minSdk (Default $DEFAULT_MIN_SDK):")
    var minSdk = readLine()
    if (minSdk?.isEmpty() ?: true) {
      minSdk = DEFAULT_MIN_SDK
    }

    print("Enter the project targetSdk (Default $DEFAULT_TARGET_SDK):")
    var targetSdk = readLine()
    if (targetSdk?.isEmpty() ?: true) {
      targetSdk = DEFAULT_TARGET_SDK
    }
    println()

    val writer = ProjectWriter(language!!, name!!, packageName!!, minSdk!!, targetSdk!!)
    writer.write { message -> println(message) }

    return 0
  }
}
