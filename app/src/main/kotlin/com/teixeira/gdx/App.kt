package com.teixeira.gdx

import com.teixeira.gdx.command.DEFAULT_MIN_SDK
import com.teixeira.gdx.command.DEFAULT_TARGET_SDK
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
  exitProcess(App().run(args))
}

class App {

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

    val name = command.getOptionValue(OPT_NAME)
    val packageName = command.getOptionValue(OPT_PACKAGE)
    val minSdk = command.getOptionValue(OPT_MIN_SDK, DEFAULT_MIN_SDK)
    val targetSdk = command.getOptionValue(OPT_TARGET_SDK, DEFAULT_TARGET_SDK)

    val writer = ProjectWriter(name, packageName, minSdk, targetSdk)
    writer.write { message -> println(message) }

    return 0
  }
}
