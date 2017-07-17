//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.07.12 at 02:49:36 PM BST 
//


package org.evosuite.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalNumberOfTestableClasses" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;sequence>
 *           &lt;element name="cut" type="{}CUT" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "totalNumberOfTestableClasses",
    "cut"
})
@XmlRootElement(name = "Project")
public class Project {

    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger totalNumberOfTestableClasses;
    protected List<CUT> cut;

    /**
     * Gets the value of the totalNumberOfTestableClasses property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalNumberOfTestableClasses() {
        return totalNumberOfTestableClasses;
    }

    /**
     * Sets the value of the totalNumberOfTestableClasses property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalNumberOfTestableClasses(BigInteger value) {
        this.totalNumberOfTestableClasses = value;
    }

    /**
     * Gets the value of the cut property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cut property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCut().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CUT }
     * 
     * 
     */
    public List<CUT> getCut() {
        if (cut == null) {
            cut = new ArrayList<CUT>();
        }
        return this.cut;
    }

}
