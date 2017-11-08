package com.cahimar.jira

class XRayWriter {

  /**
   * XRay json format test output
   */
  def xrayJson

  /**
   * The folder we want to generate test output in
   */
  def folder

  /**
   * Write the json test output to a file
   * @return the file name
   */
  def write() {
    def folderLinuxStyle = "$folder".replace('\\', '/')
    def outputDirectory = "${folderLinuxStyle}/jira"
    new File("$outputDirectory").mkdirs()
    def xrayJsonFileName = "${outputDirectory}/xray.json"
    new File(xrayJsonFileName).withWriter{ it << xrayJson }
    xrayJsonFileName
  }

}
