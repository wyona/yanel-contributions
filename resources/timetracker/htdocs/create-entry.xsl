<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>

<xsl:template match="/">
<html>
  <head>
    <title>Create time entry</title>
  </head>
  <body>
    <h1>Create Time Entry</h1>
    <form>
      <table>
        <tr>
          <td>Your name:</td><td>TODO</td>
        </tr>
        <tr><td>*Date:</td><td><input type="text" name="date" value="TODO"/></td></tr>
        <tr><td>*Client/Project:</td><td><select name="client-project"><option value="todo1">TODO 1</option><option value="todo2">TODO 2</option></select></td></tr>
        <tr><td>*Task/Description:</td><td><input type="text" name="task-description" value="TODO"/></td></tr>
        <tr><td>*Effort/Duration (h):</td><td><input type="text" name="effort" value="0.25"/></td></tr>
        <tr><td>Comment:</td><td><input type="text" name="comment" value=""/></td></tr>
        <tr><td colspan="2"><input type="submit" name="submit" value="Save"/> <input type="submit" name="cancel" value="Cancel"/></td></tr>
      </table>
    </form>

    <h1>Your existing time entries</h1>
    <p>TODO</p>
  </body>
</html>
</xsl:template>

</xsl:stylesheet>
