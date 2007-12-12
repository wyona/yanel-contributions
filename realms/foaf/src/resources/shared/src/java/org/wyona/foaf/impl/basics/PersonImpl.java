/**
 *
 */
package org.wyona.foaf.impl.basics;

import org.wyona.foaf.api.basics.Person;

import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

import org.apache.log4j.Category;

import com.ldodds.foaf.*;
import com.ldodds.foaf.jena.*;

/**
 *
 */
public class PersonImpl implements Person {

    private static Category log = Category.getInstance(PersonImpl.class);

    private static String foafNamespace = "http://xmlns.com/foaf/0.1/";

    private String name;

    /**
     *
     */
    public PersonImpl(InputStream in) throws Exception {
        FOAFGraphFactory gf = new JenaFOAFGraphFactory();
        FOAFGraph g = gf.getGraph(in, "");
        //FOAFGraph g = gf.getGraph(intercept(in), "");
        com.ldodds.foaf.model.Person p = g.findPrimaryPerson("");
        if (p != null) {
            if (log.isDebugEnabled()) log.debug("Name of primary person: " + p.getName());
            name = p.getName();
        } else {
            log.error("No primary person found!");
        }

/*
        Model model = ModelFactory.createDefaultModel();
        model.read(in, "");
        Property person = model.getProperty(foafNamespace, "Person");
        //Resource person = model.getResource("foaf:Person");
        log.error("DEBUG: Resource: " + person);
        //String name = person.getProperty(new PropertyImpl(foafNamespace, "name")).toString();
        //log.error("DEBUG: Name: " + name);
        //String givenName = person.getProperty(new PropertyImpl(foafNamespace, "givenname")).toString();
        //String familyName = person.getProperty(new PropertyImpl(foafNamespace, "family_name")).toString();
*/

/*
        edu.thu.keg.foaf.ontology.FOAFFileReader reader = new edu.thu.keg.foaf.ontology.FOAFFileReader();
        reader.readModel(model);
        edu.thu.keg.foaf.schema.FOAFPerson[] persons = reader.getAllPersons();
        for (int i=0; i< persons.length; i++) {
            log.error("DEBUG: name:"+persons[i].getFoaf_name());
        }
*/
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Intercept InputStream and log content ...
     */
    public InputStream intercept(InputStream in) throws java.io.IOException {
        java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int bytesR;
        while ((bytesR = in.read(buf)) != -1) {
            baos.write(buf, 0, bytesR);
        }

        // Buffer within memory (TODO: Maybe replace with File-buffering ...)
        // http://www-128.ibm.com/developerworks/java/library/j-io1/
        byte[] memBuffer = baos.toByteArray();

        log.error("DEBUG: InputStream: " + baos);

        return new java.io.ByteArrayInputStream(memBuffer);
    }
}
