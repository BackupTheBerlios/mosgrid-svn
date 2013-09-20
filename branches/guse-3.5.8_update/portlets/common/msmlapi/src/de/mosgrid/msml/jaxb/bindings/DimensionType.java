//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.27 at 11:01:57 AM CEST 
//


package de.mosgrid.msml.jaxb.bindings;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dimensionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dimensionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="mass"/>
 *     &lt;enumeration value="length"/>
 *     &lt;enumeration value="time"/>
 *     &lt;enumeration value="current"/>
 *     &lt;enumeration value="amount"/>
 *     &lt;enumeration value="luminosity"/>
 *     &lt;enumeration value="temperature"/>
 *     &lt;enumeration value="dimensionless"/>
 *     &lt;enumeration value="angle"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dimensionType")
@XmlEnum
public enum DimensionType {

    @XmlEnumValue("mass")
    MASS("mass"),
    @XmlEnumValue("length")
    LENGTH("length"),
    @XmlEnumValue("time")
    TIME("time"),
    @XmlEnumValue("current")
    CURRENT("current"),
    @XmlEnumValue("amount")
    AMOUNT("amount"),
    @XmlEnumValue("luminosity")
    LUMINOSITY("luminosity"),
    @XmlEnumValue("temperature")
    TEMPERATURE("temperature"),
    @XmlEnumValue("dimensionless")
    DIMENSIONLESS("dimensionless"),
    @XmlEnumValue("angle")
    ANGLE("angle");
    private final String value;

    DimensionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DimensionType fromValue(String v) {
        for (DimensionType c: DimensionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}