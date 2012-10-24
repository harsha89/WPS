/**
 * ﻿Copyright (C) 2010
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.wps.server.r.syntax;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Describes each annotation type considering attributes, their order and behavior
 * 
 */
public enum RAnnotationType {
    INPUT(Arrays.asList(RAttribute.INPUT_START,
                        RAttribute.IDENTIFIER,
                        RAttribute.TYPE,
                        RAttribute.TITLE,
                        RAttribute.ABSTRACT,
                        // RAttribute.METADATA,
                        RAttribute.DEFAULT_VALUE,
                        RAttribute.MIN_OCCURS,
                        RAttribute.MAX_OCCURS)), 
                        
                        
    OUTPUT(Arrays.asList(RAttribute.OUTPUT_START,
                        RAttribute.IDENTIFIER,
                        RAttribute.TYPE,
                        RAttribute.TITLE,
                        RAttribute.ABSTRACT
    // RAttribute.METADATA
    )), 
    
    DESCRIPTION(Arrays.asList(RAttribute.DESCRIPTION_START,
    // TODO: Meaning of identifier???? -- doesn't have a meaning yet..
                                  RAttribute.IDENTIFIER,
                                  RAttribute.TITLE,
                                  RAttribute.ABSTRACT,
                                  RAttribute.AUTHOR)),
    
    RESOURCE(Arrays.asList(RAttribute.DESCRIPTION_START, RAttribute.SEQUENCE));

    private HashMap<String, RAttribute> attributeLut = new HashMap<String, RAttribute>();
    private HashSet<RAttribute> mandatory = new HashSet<RAttribute>();
    private RAttribute startKey;
    private List<RAttribute> attributeSequence;

    private RAnnotationType(List<RAttribute> attributeSequence) {
        this.startKey = attributeSequence.get(0);
        this.attributeSequence = attributeSequence;

        for (RAttribute attribute : attributeSequence) {
            attributeLut.put(attribute.getKey(), attribute);
            if (attribute.isMandatory()) {
                mandatory.add(attribute);
            }
        }
    }

    public RAttribute getStartKey() {
        return startKey;
    }

    public RAttribute getAttribute(String key) throws RAnnotationException {
        key = key.toLowerCase();
        if (attributeLut.containsKey(key))
            return attributeLut.get(key);
        else
            throw new RAnnotationException("Annotation for " + this + " (" + this.startKey
                    + " ...) contains no key named: " + key + ".");
    }

    public Iterable<RAttribute> getAttributeSequence() {
        return attributeSequence;

    }

    /**
     * Checks if Annotation content is valid for process description and adds attributes / standard values
     * if necessary
     * 
     * @param key
     *        / value pairs given in the annotation from RSkript
     * @return key / value pairs ready for process description
     * @throws IOException
     */
    public void validDescription(HashMap<RAttribute, String> keyValues) throws RAnnotationException {
        // check minOccurs Attribute and default value:
        try {
            if (keyValues.containsKey(RAttribute.MIN_OCCURS)) {
                Integer min = Integer.parseInt(keyValues.get(RAttribute.MIN_OCCURS));
                if (keyValues.containsKey(RAttribute.DEFAULT_VALUE) && !min.equals(0))
                    throw new RAnnotationException("");
            }
        }
        catch (NumberFormatException e) {
            throw new RAnnotationException("Syntax Error in Annotation " + this + " (" + this.startKey + " ...), "
                    + "unable to parse Integer value from attribute " + RAttribute.MIN_OCCURS.getKey()
                    + e.getMessage());
        }

        if (keyValues.containsKey(RAttribute.DEFAULT_VALUE) && !keyValues.containsKey(RAttribute.MIN_OCCURS)) {
            keyValues.put(RAttribute.MIN_OCCURS, "0");
        }

        // check maxOccurs Attribute:
        try {
            if (keyValues.containsKey(RAttribute.MAX_OCCURS)) {
                Integer.parseInt(keyValues.get(RAttribute.MAX_OCCURS));
            }
        }
        catch (NumberFormatException e) {
            throw new RAnnotationException("Syntax Error in Annotation " + this + " (" + this.startKey + " ...), "
                    + "unable to parse Integer value from attribute " + RAttribute.MAX_OCCURS.getKey());
        }
    }

}