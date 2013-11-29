//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.27 at 11:01:57 AM CEST 
//


package de.mosgrid.msml.jaxb.bindings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for atomArrayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="atomArrayType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="atom" type="{http://www.xml-cml.org/schema}atomType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema/cmlx}residueNameArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}dictRef"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}title"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema/cmlx}chainNameArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}atomIDArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema/cmlx}altLocArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}convention"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}elementTypeArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}formalChargeArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}y2Array"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}x2Array"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema/cmlx}atomNameArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}x3Array"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}z3Array"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}ref"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}countArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}id"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema/cmlx}residueNumberArray"/>
 *       &lt;attGroup ref="{http://www.xml-cml.org/schema}y3Array"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "atomArrayType", propOrder = {
    "atom"
})
public class AtomArrayType {

    protected List<AtomType> atom;
    @XmlAttribute(namespace = "http://www.xml-cml.org/schema/cmlx")
    protected List<String> residueName;
    @XmlAttribute
    protected String dictRef;
    @XmlAttribute
    protected String title;
    @XmlAttribute(namespace = "http://www.xml-cml.org/schema/cmlx")
    protected List<String> chainName;
    @XmlAttribute
    protected List<String> atomID;
    @XmlAttribute(namespace = "http://www.xml-cml.org/schema/cmlx")
    protected List<String> altLoc;
    @XmlAttribute
    protected String convention;
    @XmlAttribute
    protected List<String> elementType;
    @XmlAttribute
    protected List<BigInteger> formalCharge;
    @XmlAttribute
    protected List<Double> y2;
    @XmlAttribute
    protected List<Double> x2;
    @XmlAttribute(namespace = "http://www.xml-cml.org/schema/cmlx")
    protected List<String> atomName;
    @XmlAttribute
    protected List<Double> x3;
    @XmlAttribute
    protected List<Double> z3;
    @XmlAttribute
    protected String ref;
    @XmlAttribute
    protected List<Double> count;
    @XmlAttribute
    protected String id;
    @XmlAttribute(namespace = "http://www.xml-cml.org/schema/cmlx")
    protected List<String> residueNumber;
    @XmlAttribute
    protected List<Double> y3;

    /**
     * Gets the value of the atom property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atom property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtom().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AtomType }
     * 
     * 
     */
    public List<AtomType> getAtom() {
        if (atom == null) {
            atom = new ArrayList<AtomType>();
        }
        return this.atom;
    }

    /**
     * Gets the value of the residueName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the residueName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResidueName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getResidueName() {
        if (residueName == null) {
            residueName = new ArrayList<String>();
        }
        return this.residueName;
    }

    /**
     * Gets the value of the dictRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDictRef() {
        return dictRef;
    }

    /**
     * Sets the value of the dictRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDictRef(String value) {
        this.dictRef = value;
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
     * Gets the value of the chainName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chainName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChainName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getChainName() {
        if (chainName == null) {
            chainName = new ArrayList<String>();
        }
        return this.chainName;
    }

    /**
     * Gets the value of the atomID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atomID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtomID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAtomID() {
        if (atomID == null) {
            atomID = new ArrayList<String>();
        }
        return this.atomID;
    }

    /**
     * Gets the value of the altLoc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altLoc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltLoc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAltLoc() {
        if (altLoc == null) {
            altLoc = new ArrayList<String>();
        }
        return this.altLoc;
    }

    /**
     * Gets the value of the convention property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConvention() {
        return convention;
    }

    /**
     * Sets the value of the convention property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConvention(String value) {
        this.convention = value;
    }

    /**
     * Gets the value of the elementType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the elementType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElementType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getElementType() {
        if (elementType == null) {
            elementType = new ArrayList<String>();
        }
        return this.elementType;
    }

    /**
     * Gets the value of the formalCharge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formalCharge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormalCharge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getFormalCharge() {
        if (formalCharge == null) {
            formalCharge = new ArrayList<BigInteger>();
        }
        return this.formalCharge;
    }

    /**
     * Gets the value of the y2 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the y2 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getY2().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getY2() {
        if (y2 == null) {
            y2 = new ArrayList<Double>();
        }
        return this.y2;
    }

    /**
     * Gets the value of the x2 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the x2 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getX2().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getX2() {
        if (x2 == null) {
            x2 = new ArrayList<Double>();
        }
        return this.x2;
    }

    /**
     * Gets the value of the atomName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atomName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtomName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAtomName() {
        if (atomName == null) {
            atomName = new ArrayList<String>();
        }
        return this.atomName;
    }

    /**
     * Gets the value of the x3 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the x3 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getX3().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getX3() {
        if (x3 == null) {
            x3 = new ArrayList<Double>();
        }
        return this.x3;
    }

    /**
     * Gets the value of the z3 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the z3 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getZ3().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getZ3() {
        if (z3 == null) {
            z3 = new ArrayList<Double>();
        }
        return this.z3;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the count property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getCount() {
        if (count == null) {
            count = new ArrayList<Double>();
        }
        return this.count;
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

    /**
     * Gets the value of the residueNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the residueNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResidueNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getResidueNumber() {
        if (residueNumber == null) {
            residueNumber = new ArrayList<String>();
        }
        return this.residueNumber;
    }

    /**
     * Gets the value of the y3 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the y3 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getY3().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getY3() {
        if (y3 == null) {
            y3 = new ArrayList<Double>();
        }
        return this.y3;
    }

}