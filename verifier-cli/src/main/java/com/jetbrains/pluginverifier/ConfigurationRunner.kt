package com.jetbrains.pluginverifier

import com.jetbrains.pluginverifier.configurations.*
import com.jetbrains.pluginverifier.output.TeamCityLog
import com.jetbrains.pluginverifier.output.TeamCityPrinter
import com.jetbrains.pluginverifier.report.CheckIdeReport
import com.jetbrains.pluginverifier.utils.CmdOpts
import com.jetbrains.pluginverifier.utils.OptionsUtil
import java.io.File

/**
 * @author Sergey Patrikeev
 */
abstract class ConfigurationRunner<P : ConfigurationParams, out PP : ConfigurationParamsParser<P>, R : ConfigurationResults, out C : Configuration<P, R>> {

  abstract val commandName: String

  abstract fun getParamsParser(): PP

  abstract fun getConfiguration(): C

  abstract fun printResults(results: R, opts: CmdOpts)

}


class CheckPluginRunner : ConfigurationRunner<CheckPluginParams, CheckPluginParamsParser, CheckPluginResults, CheckPluginConfiguration>() {
  override val commandName: String = "check-plugin"

  override fun getParamsParser(): CheckPluginParamsParser = CheckPluginParamsParser()

  override fun getConfiguration(): CheckPluginConfiguration = CheckPluginConfiguration()

  override fun printResults(results: CheckPluginResults, opts: CmdOpts) {
    val printerOptions = OptionsUtil.parsePrinterOptions(opts)
    if (opts.needTeamCityLog) {
      results.printTcLog(TeamCityPrinter.GroupBy.parse(opts.group), true, printerOptions)
    } else {
      results.printOnStdout(printerOptions)
    }

    if (opts.htmlReportFile != null) {
      results.printToHtml(File(opts.htmlReportFile), printerOptions)
    }
  }

}

class CheckIdeRunner : ConfigurationRunner<CheckIdeParams, CheckIdeParamsParser, CheckIdeResults, CheckIdeConfiguration>() {
  override val commandName: String = "check-ide"

  override fun getParamsParser(): CheckIdeParamsParser = CheckIdeParamsParser()

  override fun getConfiguration(): CheckIdeConfiguration = CheckIdeConfiguration()

  override fun printResults(results: CheckIdeResults, opts: CmdOpts) {
    if (opts.saveCheckIdeReport != null) {
      CheckIdeReport.createReport(results.ideVersion, results.results).saveToFile(File(opts.saveCheckIdeReport))
    }

    val printerOptions = OptionsUtil.parsePrinterOptions(opts)
    if (opts.needTeamCityLog) {
      results.printTcLog(TeamCityPrinter.GroupBy.parse(opts.group), true, printerOptions)
    } else {
      results.printOnStdOut(printerOptions)
    }

    if (opts.htmlReportFile != null) {
      results.saveToHtmlFile(File(opts.htmlReportFile), OptionsUtil.parsePrinterOptions(opts))
    }

    if (opts.dumpBrokenPluginsFile != null) {
      results.dumbBrokenPluginsList(File(opts.dumpBrokenPluginsFile))
    }
  }

}

class CheckTrunkApiRunner : ConfigurationRunner<CheckTrunkApiParams, CheckTrunkApiParamsParser, CheckTrunkApiResults, CheckTrunkApiConfiguration>() {
  override val commandName: String = "check-trunk-api"

  override fun getParamsParser(): CheckTrunkApiParamsParser =
      CheckTrunkApiParamsParser()

  override fun getConfiguration(): CheckTrunkApiConfiguration = CheckTrunkApiConfiguration()

  override fun printResults(results: CheckTrunkApiResults, opts: CmdOpts) {
    if (opts.needTeamCityLog) {
      val compareResult = CheckTrunkApiCompareResult.create(results)
      val vPrinter = TeamCityPrinter(TeamCityLog(System.out), TeamCityPrinter.GroupBy.parse(opts.group))
      vPrinter.printIdeCompareResult(compareResult)
    }
    if (opts.saveCheckIdeReport != null) {
      val file = File(opts.saveCheckIdeReport)
      results.currentReport.saveToFile(file)
    }
  }

}