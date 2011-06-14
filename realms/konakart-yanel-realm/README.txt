
  README - How to setup Konakart, etc.
  ------------------------------------

Setup of DB content:
  - Also see http://www.yanel.org/en/documentation/misc/e-commerce/konakart-integration.html
  - Drop previous DB (e.g. /Library/PostgreSQL/8.4/bin/dropdb konakart5)
  - Create new DB (e.g. /Library/PostgreSQL/8.4/bin/createdb --encoding unicode konakart5)
  - Import sample data, e.g. database/PostgreSQL/konakart_demo.sql (whereas make sure to replace port numbers 8780) (e.g. /Library/PostgreSQL/8.4/bin/psql -d konakart5 -f /Users/alice/konakart-enterprise-5.1.0.0.5688/database/PostgreSQL/konakart_demo.sql)
  - Run create-enterprise-db.sh in order to setup for multi-store (whereas make sure multi store is configured first, see below)
  - Startup KonaKart/Yanel and test the data, e.g. can you see categories, can you see products, can you see the product images?
      - Products are sometimes not displayed because the data of only one particular language exists! (see for example category "Printers")
      - Images are sometimes not displayed because:
          - Image2, Image3 and Image4 is not set
          - Image URL (default: http://localhost:8780/konakart/images/) or Image path (default: C:/Program Files/KonaKart/webapps/konakart/images) might be wrong (see konakartadmin -> Configuration -> Images)
          - Also see res-configs/shop-product-image_yanel-rc.xml
  - Re-configure within Konakartadmin the image base path and URL (see above)
  - Create default customer (necessary for price calculation): konakartadmin -> Customers -> Edit "Order Olly" -> Select type "Standardkunde"
  - Create XX zone (necessary for registration): konakartadmin -> Locations -> Zones -> Select "Switzerland" -> New: Enter "XX" for Zone and Code and select country "Switzerland"

Quick Upgrade:
  - Ports: conf/server.xml
  - SOAP: webapps/konakart/WEB-INF/server-config.wsdd
  - Database: webapps/konakart/WEB-INF/classes/konakart.properties
              webapps/konakartadmin/WEB-INF/classes/konakartadmin.properties
  - Templates: templates/store1 (also make sure to set Velocity template path within konakartadmin and to configure e-mail SMTP, etc.)
  - Admin messages: webapps/konakartadmin/WEB-INF/classes
  - Images path: Make sure to configured images path within konakartadmin and also within your realm (res-configs/shop-product-image_yanel-rc.xml)
  - Make sure to increase MaxPermSize within bin/startkonakart.sh (e.g. 512m instead 256m)

Make sure to configure:

- SOAP access configuration
    - Domain and Port
      Edit config/konakart_axis_client.properties and rebuild/restart Yanel
      or edit directly
      webapps/yanel/WEB-INF/classes/konakart_axis_client.properties
      and restart Yanel
    - Set parameter 'allowedMethods' ('*' instead 'none') in
      /home/USER/konakart-4.2.0.1/webapps/konakart/WEB-INF/server-config.wsdd
      (OPTIONAL: webapps/konakartadmin/WEB-INF/server-config.wsdd)
    - "Connection refused": Test link http://localhost:8780/konakart/services/KKWebServiceEng and make sure that webapps/yanel/WEB-INF/classes/konakart_axis_client.properties contains the same link

- Images Base Path configuration
    - Resource config property: konakart-images-base-path (according to 
      res-types/image/resource.xml)
    - Konakartadmin --> Configuration --> Images:
        - Base URL, e.g. http://localhost:8080/yanel/globus/de/produkte/
        - Base Path, e.g. /Users/michaelwechner/src/konakart/konakart-enterprise-5.1.0.0.5688/webapps/konakart/images

- Default customer (http://www.konakart.com/configurationfaq.php#What_is_a_default_customer_)
    In order to shipping costs one needs to configure a default customer within konakartadmin (--> Customers --> Select one of the customers and click Edit --> select radio "default customer")
    Also see res-types/shopping-cart/src/java/org/wyona/yanel/resources/konakart/shoppingcart/KonakartShoppingCartSOAPInfResource.java --> setUseDefaultCustomer

- Create zone called XX within konakartadmin (--> Standorte --> Zonen --> Click "New" button at bottom --> Select "Switzerland" and enter XX for zone and XX for code and click "Save") such that the registration of users is possible.

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
Also see http://www.konakart.com/docs/manualInstallation_EE.html and http://www.konakart.com/docs/multiStoreAdministration.html

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
      Btw, the shell script above is based on http://www.konakart.com/docs/manualInstallation_EE.html

- Make sure to configure the konakart installation directory:
      - Login to konakartadmin
      - Click on "Configuration" menu (left hand side)
      - Click on "Multi-Shop Configuration" menu
      - Set SQL path
      - "New" versus "Clone": http://www.konakart.com/docs/multiStoreAdministration.html

- Make sure to configure the store configuration:
      - Login to konakartadmin
      - Click on "Configuration" menu (left hand side)
      - Click on "Konfiguration Shop" menu
      - Set "Installations-Ausgangsverzeichnis"

- Map ZIP codes onto store IDs:
    data-repo/data/app2/branch-emails.xml
    e.g. <branch start="1000" storeid="store2">contact@wyona.com</branch>

TODO: http://www.konakart.com/forum/index.php/topic,1354.0.html
      res-types/overview/src/java/org/wyona/yanel/resources/konakart/overview/KonakartOverviewSOAPInfResource.java


BIRTViewer/Reporting Setup
--------------------------

The reporting doesn't work out of the box. You need to go to the Konakart
directory and edit the file (update the variable "dbPropsFile"):

- webapps/birtviewer/reports/lib/konakart.rptlibrary

in order to enable the database access. Refer to the Konakart docs for info.

Also please make sure to configure within konakartadmin the Birtviewer URLs (see menu item "Configuration Reports").


Maintenance of KonaKart (decouple Yanel from KonaKart)
------------------------------------------------------

Set the file/flag MY_REALM/data-repo/data/go-offline (or rather the realm default data repository) in order to tell Yanel that KonaKart won't be offline,
such that new users won't be able to access KonaKart anymore and signed-in users can finish their orders before actually shuting down KonaKart.
(also see res-types/shared/src/java/org/wyona/yanel/resources/konakart/shared/SharedResource.java#isKKOnline(Realm))

Also see data-repo/data/app2/xslt/shop-navigation.xsl re JavaScript based redirect ...

Also see 503 responses:

grep -rl 503 *
res-types/category/src/java/org/wyona/yanel/resources/konakart/category/KonakartCategorySOAPInfResource.java
res-types/product/src/java/org/wyona/yanel/resources/konakart/product/KonakartProductSOAPInfResource.java
