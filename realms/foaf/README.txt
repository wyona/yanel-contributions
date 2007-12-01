
  README
  ------

  Download Yanel (http://yanel.wyona.org) and install this realm to run your own FOAF server resp. to be your own Open Social Network provider. Add the following resources to your local.resources.xml (please note that the order matters):

    - src/resources/foaf/ (package="org.wyona.yanel.impl.resources.foaf")
    - src/resources/redirect/ (package="org.wyona.yanel.impl.resources.foaf.redirect")
    - src/resources/findfriend/


  For a productive environment one might want to re-configure the profiles repository config within

    - realm.xml

  by modifying the foaf:profiles-data value and also the ac-identities repository config

    - config/ac-identities-repository.xml
