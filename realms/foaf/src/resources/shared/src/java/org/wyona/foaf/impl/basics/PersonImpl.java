/**
 *
 */
package org.wyona.foaf.impl.basics;

import org.wyona.foaf.api.basics.Person;

import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

import org.apache.log4j.Category;

/**
 *
 */
public class PersonImpl implements Person {

    private static Category log = Category.getInstance(PersonImpl.class);

    private static String foafNamespace = "http://xmlns.com/foaf/0.1/";

    /**
     *
     */
    public PersonImpl(InputStream in) {
        Model model = ModelFactory.createDefaultModel();
        model.read(in, "");
        Resource person = model.getResource("foaf:Person");
        log.error("DEBUG: Resource: " + person);
        //String name = person.getProperty(new PropertyImpl(foafNamespace, "name")).toString();
        //log.error("DEBUG: Name: " + name);
        //String givenName = person.getProperty(new PropertyImpl(foafNamespace, "givenname")).toString();
        //String familyName = person.getProperty(new PropertyImpl(foafNamespace, "family_name")).toString();
    }

    /**
     *
     */
    public String getName() {
        return "Michi";
    }
}
