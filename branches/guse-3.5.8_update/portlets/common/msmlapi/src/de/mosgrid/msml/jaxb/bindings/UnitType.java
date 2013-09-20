//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.27 at 11:01:57 AM CEST 
//


package de.mosgrid.msml.jaxb.bindings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for unitType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="unitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}multiplierToData"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}isSI"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}parentSI"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}power"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}units"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}unitType"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}title"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}constantToSI"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}symbol"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}multiplierToSI"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}name"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unitType")
public class UnitType {

    @XmlAttribute
    protected Double multiplierToData;
    @XmlAttribute
    protected Boolean isSI;
    @XmlAttribute
    protected String parentSI;
    @XmlAttribute
    protected Double power;
    @XmlAttribute
    protected String units;
    @XmlAttribute
    protected String unitType;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    protected Double constantToSI;
    @XmlAttribute
    protected String symbol;
    @XmlAttribute
    protected Double multiplierToSI;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String id;

    /**
     * Gets the value of the multiplierToData property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getMultiplierToData() {
        if (multiplierToData == null) {
            return  1.0D;
        } else {
            return multiplierToData;
        }
    }

    /**
     * Sets the value of the multiplierToData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMultiplierToData(Double value) {
        this.multiplierToData = value;
    }

    /**
     * Gets the value of the isSI property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSI() {
        return isSI;
    }

    /**
     * Sets the value of the isSI property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSI(Boolean value) {
        this.isSI = value;
    }

    /**
     * Gets the value of the parentSI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentSI() {
        return parentSI;
    }

    /**
     * Sets the value of the parentSI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentSI(String value) {
        this.parentSI = value;
    }

    /**
     * Gets the value of the power property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPower() {
        return power;
    }

    /**
     * Sets the value of the power property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPower(Double value) {
        this.power = value;
    }

    /**
     * Gets the value of the units property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnits(String value) {
        this.units = value;
    }

    /**
     * Gets the value of the unitType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitType() {
        return unitType;
    }

    /**
     * Sets the value of the unitType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitType(String value) {
        this.unitType = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the constantToSI property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getConstantToSI() {
        return constantToSI;
    }

    /**
     * Sets the value of the constantToSI property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setConstantToSI(Double value) {
        this.constantToSI = value;
    }

    /**
     * Gets the value of the symbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSymbol(String value) {
        this.symbol = value;
    }

    /**
     * Gets the value of the multiplierToSI property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMultiplierToSI() {
        return multiplierToSI;
    }

    /**
     * Sets the value of the multiplierToSI property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMultiplierToSI(Double value) {
        this.multiplierToSI = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}