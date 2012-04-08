import com.typesafe.startscript.StartScriptPlugin

name := "ScalaSTMDemo"

version := "0.1.0"

scalaVersion := "2.9.1"

libraryDependencies += ("org.scala-tools" %% "scala-stm" % "0.5")

seq(StartScriptPlugin.startScriptForJarSettings: _*)

mainClass in Compile := Some("me.brandonc.banking.stm.FundTransferSimulationWithSTM")