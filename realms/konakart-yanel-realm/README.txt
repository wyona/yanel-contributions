
  README
  ------

Quick Upgrade:
  - Ports: conf/server.xml
  - SOAP: webapps/konakart/WEB-INF/server-config.wsdd
  - Database: webapps/konakart/WEB-INF/classes/konakart.properties
              webapps/konakartadmin/WEB-INF/classes/konakartadmin.properties
  - Templates: templates/store1 (also make sure to set Velocity template path within konakartadmin and to configure e-mail SMTP, etc.)
  - Admin messages: webapps/konakartadmin/WEB-INF/classes
  - Images path: Make sure to configured images path within konakartadmin and also within your realm (res-configs/shop-product-image_yanel-rc.xml)

Make sure to configure:

- SOAP access configuration
    - Domain and Port
      Edit config/konakart_axis_client.properties or possibly 
      webapps/yanel/WEB-INF/classes/konakart_axis_client.properties
    - Set parameter 'allowedMethods' ('*' instead 'none') in
      /home/USER/konakart-4.2.0.1/webapps/konakart/WEB-INF/server-config.wsdd

- Images Base Path configuration
    - Resource config property: konakart-images-base-path (according to 
      res-types/image/resource.xml)

Optional configuration:

- Datebase configuration (for direct access)
  -> Edit config/konakart-repository.xml

- SMTP E-Mail configuration: In order to have KonaKart sending emails one needs to configure within konakartadmin (--> Konfiguration --> E-Mail-Optionen):
    - SMTP Server, e.g. mail.wyona.com
    - E-Mail Antwort an, e.g. contact@wyona.com
    - E-Mail-Eigenschaften-Dateiname, e.g. /Users/michaelwechner/src/konakart/konakart-4.2.0.1/conf/konakart_mail.properties

- Sender E-Mail Address:
   - Configure within konakartadmin: Konfiguration --> Konfiguration Shop --> "E-Mail-Adresse" and "E-Mail von"

- E-Mail address of Administrator re orders:
   - res-configs/de/shop/overview.html.yanel-rc
   - res-configs/fr/shop/overview.html.yanel-rc

- The directory konakart-data includes velocity templates that can Konakart
  needs, e.g. for sending out email (e.g. konakart-data/EmailNewPassword_de.vm, konakart-data/EmailNewPassword_fr.vm). Put them in webapps/konakart/WEB-INF/classes 
  in your Konakart installation so that Konakart can find them (and make sure to restart KonaKart).

- The konakart-data directory also includes a backup script for postgres
  databases. Change the settings in the script itself, pass username/table
  for database as parameters.

- There's an UTF-8 encoded i18n file for konakartadmin available too, in case
  you run into problems with umlauts. When upgrading, one can also convert the
  i18n files konakart ships using those commands:

    cp webapps/konakartadmin/WEB-INF/classes/AdminMessages_de.properties AdminMessage_de.properties.backup
    iconv -f iso-8859-1 -t utf-8 AdminMessage_de.properties.backup > AdminMessages_de.properties

  That should convert the file from iso-8859-1 encoding to utf-8 encoding.
  Repeat for other files if necessary, e.g. the French translation files.

- Zone (Kanton) configuration: As default the Kanton Zug (ZG) is configured (also see Standorte/Steuern within konakartadmin)
  (TODO: Add sample configuration to SVN)
    res-configs/fr/shop/registration.html.yanel-rc
    res-configs/de/shop/registration.html.yanel-rc

More information at: http://127.0.0.1:8080/yanel/yanel-website/en/documentation/misc/e-commerce/konakart-integration.html

Test order with sample credit card numbers: https://www.paypalobjects.com/en_US/vhelp/paypalmanager_help/credit_card_numbers.htm
    - For example MasterCard: 5105105105105100

Multi-Store Single DB (Mode 2) Configuration
--------------------------------------------

- webapps/konakartadmin/WEB-INF/classes/konakartadmin.properties
   konakart.ws.mode = 2
   konakart.ws.customersShared = true
   konakart.ws.productsShared = true

- webapps/konakartadmin/WEB-INF/classes/konakartadmin_gwt.properties
    konakartadmin.gwt.mode = 2
    konakartadmin.gwt.customersShared = true
    konakartadmin.gwt.productsShared = true

- webapps/konakart/WEB-INF/classes/konakart.properties
    konakart.ws.mode = 2
    konakart.ws.customersShared = true
    konakart.ws.productsShared = true

Note: A previous version of this document said not to configure the konakart
webapp in multistore mode, but since SOAP goes through the konakart webapp
it's necessary to do so or you will run into problems.

- webapps/konakart/WEB-INF/struts-config.xml
    <plug-in className="com.konakart.plugins.KKEngPlugin">
      <set-property property="mode" value="2"/>
      <set-property property="customersShared" value="true"/>
      <set-property property="productsShared" value="true"/>
    <plug-in className="com.konakart.plugins.KKAppEngPlugin">
      <set-property property="mode" value="2"/>
      <set-property property="customersShared" value="true"/>
      <set-property property="productsShared" value="true"/>

- Run once and only once: 'sh create-enterprise-db.sh'
      Note: You may need to edit paths in the script before running
      (which creates an additional sample store called 'store2' within the table 'kk_store', 
      whereas one can add more stores within konakartadmin webapp, see left hand menu 
      item 'Verwaltung Shop' ...)

- Map ZIP codes onto store IDs:
    data-repo/data/app2/branch-emails.xml
    e.g. <branch start="1000" storeid="store2">contact@wyona.com</branch>

TODO: http://www.konakart.com/forum/index.php/topic,1354.0.html


BIRTViewer/Reporting Setup
--------------------------

The reporting doesn't work out of the box. You need to go to the Konakart
directory and edit the file (update the variable "dbPropsFile"):

- webapps/birtviewer/reports/lib/konakart.rptlibrary

in order to enable the database access. Refer to the Konakart docs for info.
