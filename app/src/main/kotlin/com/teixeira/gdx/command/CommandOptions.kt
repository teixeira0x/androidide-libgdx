package com.teixeira.gdx.command

import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

fun getCommandOptions(): Options {
  return Options().apply {
    addOption(
      Option.builder().longOpt(OPT_NAME).desc(OPT_NAME_DESC).hasArg().required(true).build()
    )
    addOption(
      Option.builder().longOpt(OPT_PACKAGE).desc(OPT_PACKAGE_DESC).hasArg().required(true).build()
    )
    addOption(Option.builder().longOpt(OPT_MIN_SDK).desc(OPT_MIN_SDK_DESC).hasArg().build())
    addOption(Option.builder().longOpt(OPT_TARGET_SDK).desc(OPT_TARGET_SDK_DESC).hasArg().build())
  }
}

fun printCommandHelp() {
  HelpFormatter().printHelp("java -jar <jar-path> [OPTIONS]", getCommandOptions())
}
