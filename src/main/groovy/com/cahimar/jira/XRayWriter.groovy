package com.cahimar.jira

class XRayWriter {

  def xrayJson

  def folder

  def write() {
    def folderLinuxStyle = "$folder".replace('\\', '/')
    def outputDirectory = "${folderLinuxStyle}/jira"
    new File("$outputDirectory").mkdirs()
    def xrayJsonFileName = "${outputDirectory}/xray.json"
    new File(xrayJsonFileName).withWriter{ it << xrayJson }
    xrayJsonFileName
  }

}
