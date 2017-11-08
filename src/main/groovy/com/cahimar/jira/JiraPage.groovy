package com.cahimar.jira

/**
 * Writes out a html page which redirects to the generated Test Execution jira ticket
 */
class JiraPage {

  /**
   * The folder we want to generate our test output into
   */
  def folder

  /**
   * The test execution jira id we just generated
   */
  def jiraId

  /**
   * The url of your jira instance
   */
  def url

  /**
   * Create a folder/jira/jira.html which redirects to your Jira Test Execution ticket
   */
  def write() {
    def jiraUrl = "$url/browse/$jiraId"
    def folderLinuxStyle = "$folder".replace('\\', '/')
    def outputDirectory = "${folderLinuxStyle}/jira"
    new File("$outputDirectory").mkdirs()
    def htmlFileName = "${outputDirectory}/jira.html"
    def html = """<!DOCTYPE HTML>
    <html lang="en-US">
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="refresh" content="1; url=$jiraUrl">
            <script type="text/javascript">
                window.location.href = "$jiraUrl"
            </script>
            <title>Page Redirection</title>
        </head>
        <body>
            If you are not redirected automatically, follow this <a href='$jiraUrl'>$jiraId</a>.
        </body>
    </html>"""
    new File(htmlFileName).withWriter{ it << html }
    println "Created output file $htmlFileName. This will redirect to the test execution jira. So can be used as a build artifact in Bamboo or Jenkins."
  }
}
