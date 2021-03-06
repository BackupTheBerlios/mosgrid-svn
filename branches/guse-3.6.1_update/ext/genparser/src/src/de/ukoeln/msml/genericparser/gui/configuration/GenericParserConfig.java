//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.17 at 02:32:49 PM CEST 
//


package de.ukoeln.msml.genericparser.gui.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Instances of this type store all configurations of all
 * 				extensions that are needed for the corresponding
 * 				property-type of the
 * 				MSML object.
 * 			
 * 
 * <p>Java class for GenericParserConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GenericParserConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extensionConfigCollection" type="{http://www.mosgrid.de/genericparser}GenericParserExtensionConfig" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="childConfig" type="{http://www.mosgrid.de/genericparser}GenericParserConfig" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="propertyName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="canonicalClassName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericParserConfig", propOrder = {
    "extensionConfigCollection",
    "childConfig"
})
@XmlSeeAlso({
    ConfigSimplType.class,
    ConfigAdvType.class
})
public class GenericParserConfig {

    protected List<GenericParserExtensionConfig> extensionConfigCollection;
    protected List<GenericParserConfig> childConfig;
    @XmlAttribute(required = true)
    protected String propertyName;
    @XmlAttribute
    protected String canonicalClassName;

    /**
     * Gets the value of the extensionConfigCollection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extensionConfigCollection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtensionConfigCollection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenericParserExtensionConfig }
     * 
     * 
     */
    public List<GenericParserExtensionConfig> getExtensionConfigCollection() {
        if (extensionConfigCollection == null) {
            extensionConfigCollection = new ArrayList<GenericParserExtensionConfig>();
        }
        return this.extensionConfigCollection;
    }

    /**
     * Gets the value of the childConfig property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childConfig property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildConfig().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenericParserConfig }
     * 
     * 
     */
    public List<GenericParserConfig> getChildConfig() {
        if (childConfig == null) {
            childConfig = new ArrayList<GenericParserConfig>();
        }
        return this.childConfig;
    }

    /**
     * Gets the value of the propertyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyName(String value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the canonicalClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    /**
     * Sets the value of the canonicalClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanonicalClassName(String value) {
        this.canonicalClassName = value;
    }

}
