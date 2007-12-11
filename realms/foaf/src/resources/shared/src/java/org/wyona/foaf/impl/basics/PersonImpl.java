/**
 *
 */
package org.wyona.foaf.impl.basics;

import org.wyona.foaf.api.basics.Person;

import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import org.apache.log4j.Category;

/**
 *
 */
public class PersonImpl implements Person {

    private static Category log = Category.getInstance(PersonImpl.class);

    /**
     *
     */
    public PersonImpl(InputStream in) {
        Model model = ModelFactory.createDefaultModel();
        model.read(in, "");
        Resource person = model.getResource("foaf:Person");
        log.error("DEBUG: Resource: " + person);
    }

    /**
     *
     */
    public String getName() {
        return "Michi";
    }
}
