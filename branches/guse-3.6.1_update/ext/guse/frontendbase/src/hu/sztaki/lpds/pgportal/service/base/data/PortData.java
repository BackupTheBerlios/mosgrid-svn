/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * Contains the configuration data of a job's port
 */

package hu.sztaki.lpds.pgportal.service.base.data;

import java.util.Hashtable;

/**
 * @author krisztian
 */
public class PortData 
{
    private Hashtable data=new Hashtable();
    private Hashtable dataDisabled=new Hashtable();
    private Hashtable label=new Hashtable(); 
    private Hashtable desc=new Hashtable(); 
/**
 * Creates a new instance of PortData
 *
 * @param pInit set values
 * @param pDisabled disabled data from the template
 * @param pInh inherited data
 * @param pDesc descriptions
 * @param pLabel labels
 */
    public PortData(Hashtable pInit,Hashtable pDisabled,Hashtable pLabel,Hashtable pDesc,Hashtable pInh) 
    {
        data=pInit;
        dataDisabled=pDisabled;
        label=pLabel;
        desc=pDesc;
//        inh=pInh;
    }

    /**
     * Setting the name of the port
     * @param value port name
     */
    public void setName(String value){data.put("name", value);}
    /**
     * Setting the port description
     * @param value longer text description of the port
     */
    public void setTxt(String value){data.put("txt", value);}
    /**
     * Setting internal file name
     * @param value internal file name
     */
    public void setInternalName(String value){data.put("internalName", value);}
    /**
     * Setting the name of the uploaded file
     * @param value file name(client side)
     */
    public void setExternalName(String value){data.put("file", value);}
    /**
     * Setting the remote file name
     * @param value file name on the storage
     */
    public void setRemoteName(String value){data.put("remoteName", value);}
    /**
     * Setting the unique internal ID of the port
     * @param value ID settings
     */
    public void setId(String value){data.put("id", value);}
    /**
     * Port dot/cross group ID
     * @param value group ID
     */
    public void setDpid(String value){data.put("dpid", value);}
    /**
     * @deprecated
     * @param value this happens if there is not enough element in the group
     */
    public void setHnt(String value){data.put("hnt", value);}
    /**
     * The number of inputs can be used during execution from the parametric inputs
     * @param value number of the input files
     */
    public void setMax(String value){data.put("max", value);}
    /**
     * In case of a channel port sets the name of the left hand side job (previous job)
     * @param value job name
     */
    public void setPreJob(String value){data.put("prejob",value);}
    /**
     * Setting the collector type
     * @param value collector
     */
    public void setWaiting(String value){data.put("waiting",value);}
    /**
     * Setting the number of the port, this number matters for the order of the cross product
     * @param value number value
     */
    public void setSeq(String value){data.put("seq",value);}
    /**
     * Getting the number of the port, this number matters for the order of the cross product
     * @return number value
     */
    public String getSeq(){return (data.get("seq")==null)?"":""+data.get("seq");}
    /**
     * Getting the name of the port
     * @return name
     */
    public String getName(){return (data.get("name")==null)?"":""+data.get("name");}
    /**
     * Getting the port description
     * @return port description
     */
    public String getTxt(){return (data.get("txt")==null)?"":""+data.get("txt");}
    /**
     * Getting the internal file name
     * @return internal file name
     */
    public String getInternalName(){return (data.get("intname")==null)?getName():""+data.get("intname");}
    /**
     * Getting the name of the uploaded file
     * @return the name of the input file, given by the uploader user
     */
    public String getExternalName(){return (data.get("file")==null)?"":""+data.get("file");}
    /**
     * Getting the name of the remote file stored on the grid storage
     * @return file name and path
     */
    public String getRemoteName(){return (data.get("remote")==null)?"":""+data.get("remote");}
    /**
     * In case of a remote file the name of the catalogue or the host
     * @return host
     */
    public String getRemoteHost(){return (data.get("remotehost")==null)?"":""+data.get("remotehost");}
    
    /**
     * Generator state
     * @return in case of "2" it is a generator
     */
    public String getMaincount(){return (data.get("maincount")==null)?"1":""+data.get("maincount");}
    /**
     * Getting the port unique internal ID
     * @return internal ID
     */
    public String getId(){return (data.get("id")==null)?"":""+data.get("id");}
    /**
     * Getting the number of the cross/dot group belongs to the port
     * @return number
     */
    public String getDpid(){return (data.get("dpid")==null)?"":""+data.get("dpid");}
    /**
     * @deprecated
     * @return port round trip type
     */
    public String getHnt(){return (data.get("hnt")==null)?"":""+data.get("hnt");}
    /**
     * The number of files can be used from the input port
     * @return number of files
     */
    public String getMax(){return (data.get("max")==null)?"":""+data.get("max");}
    /**
     * In case of a channel port, getting the name of the previous job
     * @return job name
     */
    public String getPreJob(){return (data.get("prejob")==null)?"":""+data.get("prejob");}
    /**
     * In case of an input condition check, getting the comparing value
     * @return comparing value (for example "apple")
     */
    public String getEqualValue(){return (data.get("pequalvalue")==null)?"":""+data.get("pequalvalue");}
    /**
     * In case of an input condition, getting the checking algorithm
     * @return algorithm ID
     */
    public String getEqualType(){return (data.get("pequaltype")==null)?"":""+data.get("pequaltype");}
    /**
     * In case of an input condition check, getting the name of the port to be compared (when two ports' status needed to be compared)
	 * @return port name
     */
    public String getEqualInput(){return (data.get("pequalinput")==null)?"":""+data.get("pequalinput");}
    /**
     * Getting the generator property
     * @return in case of "2" it is a generator
     */
    public String getGenerator(){return (data.get("generator")==null)?"":""+data.get("generator");}
    /**
     * @deprecated
     * @return -
     */
    public String getEparam(){return (data.get("eparam")==null)?"":""+data.get("eparam");}
    /**
     * Getting the embedded input port
     * @return embedding mapping(job/port)
     */
    public String getIinput(){return (data.get("iinput")==null)?"":""+data.get("iinput");}
    /**
     * Getting the embedded output port
     * @return embedding mapping(job/port)
     */
    public String getIoutput(){return (data.get("ioutput")==null)?"":""+data.get("ioutput");}
    /**
     * Getting the port type (local file/remote/constant/SQL/job specific)
     * @return input port type
     */
    public String getType(){return (data.get("type")==null)?"":""+data.get("type");}
    /**
     * Getting the collector property
     * @return in case of "2" it is a collector
     */
    public String getWaiting(){return (data.get("waiting")==null)?"":""+data.get("waiting");}
    /**
     * Getting the configured constant value
     * @return value
     */
    public String getValue(){return (data.get("value")==null)?"":""+data.get("value");}
    /**
     * In case of an SQL input, getting the used database URL
     * @return JDBC URL
     */
    public String getSqlurl(){return (data.get("sqlurl")==null)?"":""+data.get("sqlurl");}
    /**
     * In case of an SQL input, getting the database user name
     * @return db user name
     */
    public String getSqluser(){return (data.get("sqluser")==null)?"":""+data.get("sqluser");}
    /**
     * In case of an SQL input, getting the password of the database user
     * @return db user password
     */
    public String getSqlpass(){return (data.get("sqlpass")==null)?"":""+data.get("sqlpass");}
    /**
     * In case of an SQL input, getting the query to be done
     * @return SELECT
     */
    public String getSqlselect(){return (data.get("sqlselect")==null)?"":""+data.get("sqlselect");}
    /**
     * Defining the parametric port
     * @return "1" PS input
     */
    public String getParametric()
    {
        if((!getDpid().equals(""))||(!getHnt().equals(""))||(!getMax().equals("")))return "1";
        else return "0";
    }
    /**
     * Getting the molecule list belongs to the user
     * @deprecated
     * @return molecule list(cgrid)
     */
    public String getClist(){return (data.get("clist")==null)?"":""+data.get("clist");}
    /**
     * Getting the configuration of the job specific port
     * @return special input descriptor
     */
    public String getCprop(){return (data.get("cprop")==null)?"":""+data.get("cprop");}
    
    /**
     * Sets the user interface changeability of the internal file name belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledInternalName()
    {
        return (dataDisabled.get("intname")==null)?"":" disabled=\"true\" ";
    }

    /**
     * Sets the user interface changeability of the type belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledInputType()
    {
        boolean templatevalue=(dataDisabled.get("file")!=null)||(dataDisabled.get("remote")!=null)||(dataDisabled.get("value")!=null)||(dataDisabled.get("sqlurl")!=null)||(dataDisabled.get("sqluser")!=null)||(dataDisabled.get("sqlpass")!=null)||(dataDisabled.get("sqlselect")!=null)||(dataDisabled.get("cprop")!=null);
        boolean open=false;
        if(label.get("file")!=null)
        {
            if((!"N/A".equals(label.get("file")))&& (!"".equals(label.get("file"))))
                open=true;
        }
        if(label.get("remote")!=null)
            if(!"".equals(label.get("remote"))) open=true;
        if(label.get("value")!=null)
            if(!"".equals(label.get("value"))) open=true;

        if(label.get("sqlurl")!=null)
            if(!"".equals(label.get("sqlurl"))) open=true;
        if(label.get("sqluser")!=null)
            if(!"".equals(label.get("sqluser"))) open=true;
        if(label.get("sqlpass")!=null)
            if(!"".equals(label.get("sqlpass"))) open=true;
        if(label.get("sqlselect")!=null)
            if(!"".equals(label.get("sqlselect"))) open=true;

        if(label.get("cprop")!=null)
            if(!"".equals(label.get("cprop"))) open=true;

        return (templatevalue&&(!open))?" disabled=\"true\" ":"";
    }

    /**
     * Getting the default input for the uploaded source file
     * @return true=local input
     */
    public boolean getDefaultFile()
    {
        boolean res=true;
        if(dataDisabled.get("remote")!=null)
            if(!"".equals(dataDisabled.get("remote"))) res=false;

        if(dataDisabled.get("value")!=null)
            if(!"".equals(dataDisabled.get("value"))) res=false;

        if(dataDisabled.get("sqlurl")!=null)
            if(!"".equals(dataDisabled.get("sqlurl"))) res=false;
        if(dataDisabled.get("sqluser")!=null)
            if(!"".equals(dataDisabled.get("sqluser"))) res=false;
        if(dataDisabled.get("sqlpass")!=null)
            if(!"".equals(dataDisabled.get("sqlpass"))) res=false;
        if(dataDisabled.get("sqlselect")!=null)
            if(!"".equals(dataDisabled.get("sqlselect"))) res=false;

        if(dataDisabled.get("cprop")!=null)
            if(!"".equals(dataDisabled.get("cprop"))) res=false;

        return res;
    }

    /**
     * Sets the user interface changeability of the uploaded file belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledExternalName()
    {
        return ((dataDisabled.get("file")==null)&&(dataDisabled.get("remote")==null)&&(dataDisabled.get("value")==null)&&(dataDisabled.get("sqlurl")==null)&&(dataDisabled.get("sqluser")==null)&&(dataDisabled.get("sqlpass")!=null)&&(dataDisabled.get("sqselect")!=null))?"":" disabled=\"true\" ";
    }
    /**
     * Sets the user interface changeability of the remote file name belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledRemoteName(){return (dataDisabled.get("remote")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the dot/cross group code belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledDpid(){return (dataDisabled.get("dpid")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the input handling algorithm belongs to the port
     * @deprecated
     * @return HTML code to be used
     */
    public String getEnabledHnt(){return (dataDisabled.get("hnt")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the maximum number of input file belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledMax(){return (dataDisabled.get("max")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the dot/cross group code belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledEqualValue(){return (dataDisabled.get("pequalvalue")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the input condition type belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledEqualType()
    {
        return ((dataDisabled.get("pequalinput")==null)&&(dataDisabled.get("pequalvalue")==null))?"":"disabled=\"true\" ";
    }
    /**
     * Sets the user interface changeability of the input condition comparing port's name belongs to the port
     * @return HTML code to be used
     */
    public String getEnabledEqualInput(){return (dataDisabled.get("pequalinput")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the node number belongs to the port (binary data)
     * @deprecated
     * @return HTML code to be used
     */
    public String getEnabledEnodenumber(){return (dataDisabled.get("enodenumber")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the binary belongs to the port
     * @deprecated
     * @return HTML code to be used
     */
    public String getEnabledBinary(){return (dataDisabled.get("binary")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the parameter belongs to the port
     * @deprecated
     * @return HTML code to be used
     */
    public String getEnabledEparam(){return (dataDisabled.get("eparam")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the embedding setup belongs to the input port
     * @return HTML code to be used
     */
    public String getEnabledIinput(){return (dataDisabled.get("iinput")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the embedding setup belongs to the output port
     * @return HTML code to be used
     */
    public String getEnabledIoutput(){return (dataDisabled.get("ioutput")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the type setup belongs to the input port
     * @return HTML code to be used
     */
    public String getEnabledType(){return (dataDisabled.get("type")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the collector setup belongs to the input port
     * @return HTML code to be used
     */
    public String getEnabledWaiting(){return (dataDisabled.get("waiting")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the generator setup belongs to the output port
     * @return HTML code to be used
     */
    public String getEnabledMaincount(){return (dataDisabled.get("maincount")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the parametric setup belongs to the input port
     * @deprecated
     * @return HTML code to be used
     */
    public String getEnabledParametric()
    {
        if((!getDpid().equals(""))||(!getHnt().equals(""))||(!getMax().equals("")))return "1";
        else return "0";
    }
    /**
     * Sets the user interface changeability of the molecule list belongs to the input port (cgrid)
     * @deprecated
     * @return HTML code to be used
     */
    public String getEnabledClist(){return (dataDisabled.get("clist")==null)?"":" disabled=\"true\" ";}
    /**
     * Sets the user interface changeability of the job specific port's setup belongs to the input port
     * @return HTML code to be used
     */
    public String getEnabledCprop(){return (dataDisabled.get("cprop")==null)?"":" disabled=\"true\" ";}

    /**
     * Getting the number label of the port used on the end user interface
     * @return showed label
     */
    public String getLabelSeq(){return (label.get("seq")==null)?"":""+label.get("seq");}
    /**
     * Getting the label of the port used on the end user interface
     * @return showed label
     */
    public String getLabelName(){return (label.get("name")==null)?"":""+label.get("name");}
    /**
     * Getting the label of the port description used on the end user interface
     * @return showed label
     */
    public String getLabelTxt(){return (label.get("txt")==null)?"":""+label.get("txt");}
    /**
     * Getting the label of the internal file name used on the end user interface
     * @return showed label
     */
    public String getLabelInternalName(){return (label.get("intname")==null)?getName():""+label.get("intname");}
    /**
     * Getting the label of the file upload used on the end user interface
     * @return showed label
     */
    public String getLabelExternalName(){return (label.get("file")==null)?"":""+label.get("file");}
    /**
     * Getting the label of the grid storage used on the end user interface
     * @return showed label
     */
    public String getLabelRemoteName(){return (label.get("remote")==null)?"":""+label.get("remote");}
    /**
     * Getting the remote file host/LFC catalogue used on the end user interface
     * @return showed label
     */
    public String getLabelRemoteHost(){return (label.get("remotehost")==null)?"":""+label.get("remotehost");}
    /**
     * Getting the label of the generator configuration used on the end user interface
     * @return showed label
     */
    public String getLabelMaincount(){return (label.get("maincount")==null)?"":""+label.get("maincount");}
    /**
     * Getting the label of the internal port ID used on the end user interface
     * @return showed label
     */
    public String getLabelId(){return (label.get("id")==null)?"":""+label.get("id");}
    /**
     * Getting the label of the parametric dot/cross group used on the end user interface
     * @return showed label
     */
    public String getLabelDpid(){return (label.get("dpid")==null)?"":""+label.get("dpid");}
    /**
     * Getting the label of the input port handling strategy used on the end user interface
     * @deprecated
     * @return showed label
     */
    public String getLabelHnt(){return (label.get("hnt")==null)?"":""+label.get("hnt");}
    /**
     * Getting the label of the input file number used on the end user interface
     * @return showed label
     */
    public String getLabelMax(){return (label.get("max")==null)?"":""+label.get("max");}
    /**
     * Getting the label of the previous job used on the end user interface in case of a channel port 
     * @return showed label
     */
    public String getLabelPreJob(){return (label.get("prejob")==null)?"":""+label.get("prejob");}
    /**
     * Getting the label of the comparing value used on the end user interface in case of a condition check port
     * @return showed label
     */
    public String getLabelEqualValue(){return (label.get("pequalvalue")==null)?"":""+label.get("pequalvalue");}
    /**
     * Getting the label of the comparing algorithm used on the end user interface in case of a condition check port
     * @return showed label
     */
    public String getLabelEqualType(){return (label.get("pequaltype")==null)?"":""+label.get("pequaltype");}
    /**
     * Getting the label of the comparing port used on the end user interface in case of a condition check port
     * @return showed label
     */
    public String getLabelEqualInput(){return (label.get("pequalinput")==null)?"":""+label.get("pequalinput");}
    /**
     * Getting the label of the generator property used on the end user interface
     * @return showed label
     */
    public String getLabelGenerator(){return (label.get("generator")==null)?"":""+label.get("generator");}
    /**
     * Getting the label of the job parameter property used on the end user interface
     * @deprecated
     * @return showed label
     */
    public String getLabelEparam(){return (label.get("eparam")==null)?"":""+label.get("eparam");}
    /**
     * Getting the label of the input embedding property used on the end user interface
     * @return showed label
     */
    public String getLabelIinput(){return (label.get("iinput")==null)?"":""+label.get("iinput");}
    /**
     * Getting the label of the output embedding property used on the end user interface
     * @return showed label
     */
    public String getLabelIoutput(){return (label.get("ioutput")==null)?"":""+label.get("ioutput");}
    /**
     * Getting the label of the port type property used on the end user interface
     * @return showed label
     */
    public String getLabelType(){return (label.get("type")==null)?"":""+label.get("type");}
    /**
     * Getting the label of the collector property used on the end user interface
     * @return showed label
     */
    public String getLabelWaiting(){return (label.get("waiting")==null)?"":""+label.get("waiting");}
    /**
     * Getting the label of the constant value property used on the end user interface
     * @return showed label
     */
    public String getLabelValue(){return (label.get("value")==null)?"":""+label.get("value");}
    /**
     * Getting the label of the SQL URL property used on the end user interface
     * @return showed label
     */
    public String getLabelSqlurl(){return (label.get("sqlurl")==null)?"":""+label.get("sqlurl");}
    /**
     * Getting the label of the SQL user name property used on the end user interface
     * @return showed label
     */
    public String getLabelSqluser(){return (label.get("sqluser")==null)?"":""+label.get("sqluser");}
    /**
     * Getting the label of the SQL user password property used on the end user interface
     * @return showed label
     */
    public String getLabelSqlpass(){return (label.get("sqlpass")==null)?"":""+label.get("sqlpass");}
    /**
     * Getting the label of the SQL query property used on the end user interface
     * @return showed label
     */
    public String getLabelSqlselect(){return (label.get("sqlselect")==null)?"":""+label.get("sqlselect");}
    /**
     * Getting the label of the molecule list (cgrid) property used on the end user interface
     * @return showed label
     */
    public String getLabelClist(){return (label.get("clist")==null)?"":""+label.get("clist");}
    /**
     * Getting the label of the job specific configuration port property used on the end user interface
     * @return showed label
     */
    public String getLabelCprop(){return (label.get("cprop")==null)?"":""+label.get("cprop");}
    
    /**
     * Getting the description of the port number property used on the end user interface
     * @return showed description
     */
    public String getDescSeq(){return (desc.get("seq")==null)?"":""+desc.get("seq");}
    /**
     * Getting the description of the port name used on the end user interface
     * @return showed description
     */
    public String getDescName(){return (desc.get("name")==null)?"":""+desc.get("name");}
    /**
     * Getting the description to be used in case of port description on the end user interface
     * @return showed description
     */
    public String getDescTxt(){return (desc.get("txt")==null)?"":""+desc.get("txt");}
    /**
     * Getting the description to be used in case of internal port name on the end user interface
     * @return showed description
     */
    public String getDescInternalName(){return (desc.get("intname")==null)?getName():""+desc.get("intname");}
    /**
     * Getting the description to be used in case of port number on the end user interface
     * @return showed description
     */
    public String getDescExternalName(){return (desc.get("file")==null)?"":""+desc.get("file");}
    /**
     * Getting the description to be used in case of port grid storage file on the end user interface
     * @return showed description
     */
    public String getDescRemoteName(){return (desc.get("remote")==null)?"":""+desc.get("remote");}
    /**
     * Getting the description to be used in case of port grid storage host/LFC catalogue on the end user interface
     * @return showed description
     */
    public String getDescRemoteHost(){return (desc.get("remotehost")==null)?"":""+desc.get("remotehost");}
    /**
     * Getting the description to be used in case of a generator port on the end user interface
     * @return showed description
     */
    public String getDescMaincount(){return (desc.get("maincount")==null)?"":""+desc.get("maincount");}
    /**
     * Getting the description belongs to the port's unique internal ID on the end user interface
     * @return showed description
     */
    public String getDescId(){return (desc.get("id")==null)?"":""+desc.get("id");}
    /**
     * Getting the description to be used in case of port dot/cross group ID on the end user interface
     * @return showed description
     */
    public String getDescDpid(){return (desc.get("dpid")==null)?"":""+desc.get("dpid");}
    /**
     * Getting the description to be used in case of port file handling strategy on the end user interface
     * @return showed description
     * @deprecated
     */
    public String getDescHnt(){return (desc.get("hnt")==null)?"":""+desc.get("hnt");}
    /**
     * Getting the description to be used in case of parametric input number on the end user interface
     * @return showed description
     */
    public String getDescMax(){return (desc.get("max")==null)?"":""+desc.get("max");}
    /**
     * Getting the description of the channel job to be used on the end user interface
     * @return showed description
     */
    public String getDescPreJob(){return (desc.get("prejob")==null)?"":""+desc.get("prejob");}
    /**
     * Getting the description of the input port's condition check value to be used on the end user interface
     * @return showed description
     */
    public String getDescEqualValue(){return (desc.get("pequalvalue")==null)?"":""+desc.get("pequalvalue");}
    /**
     * Getting the description of the input port's condition check algorithm to be used on the end user interface
     * @return showed description
     */
    public String getDescEqualType(){return (desc.get("pequaltype")==null)?"":""+desc.get("pequaltype");}
    /**
     * Getting the description of the input port's condition check port to be used on the end user interface
     * @return showed description
     */
    public String getDescEqualInput(){return (desc.get("pequalinput")==null)?"":""+desc.get("pequalinput");}
    /**
     * Getting the description connected to the generator type on the end user interface
     * @return showed description
     */
    public String getDescGenerator(){return (desc.get("generator")==null)?"":""+desc.get("generator");}
    /**
     * Getting the description connected to the job parameter on the end user interface
     * @deprecated
     * @return showed description
     */
    public String getDescEparam(){return (desc.get("eparam")==null)?"":""+desc.get("eparam");}
    /**
     * Getting the description connected to the embedded input type on the end user interface
     * @return showed description
     */
    public String getDescIinput(){return (desc.get("iinput")==null)?"":""+desc.get("iinput");}
    /**
     * Getting the description connected to the embedded output type on the end user interface
     * @return showed description
     */
    public String getDescIoutput(){return (desc.get("ioutput")==null)?"":""+desc.get("ioutput");}
    /**
     * Getting the description connected to the input type on the end user interface
     * @return showed description
     */
    public String getDescType(){return (desc.get("type")==null)?"":""+desc.get("type");}
    /**
     * Getting the description connected to the collector input type on the end user interface
     * @return showed description
     */
    public String getDescWaiting(){return (desc.get("waiting")==null)?"":""+desc.get("waiting");}
    /**
     * Getting the description connected to the constant input type on the end user interface
     * @return showed description
     */
    public String getDescValue(){return (desc.get("value")==null)?"":""+desc.get("value");}
    /**
     * Getting the description connected to the SQL URL on the end user interface
     * @return showed description
     */
    public String getDescSqlurl(){return (desc.get("sqlurl")==null)?"":""+desc.get("sqlurl");}
    /**
     * Getting the description connected to the SQL user on the end user interface
     * @return showed description
     */
    public String getDescSqluser(){return (desc.get("sqluser")==null)?"":""+desc.get("sqluser");}
    /**
     * Getting the description connected to the SQL user password on the end user interface
     * @return showed description
     */
    public String getDescSqlpass(){return (desc.get("sqlpass")==null)?"":""+desc.get("sqlpass");}
    /**
     * Getting the description connected to the SQL query on the end user interface
     * @return showed description
     */
    public String getDescSqlselect(){return (desc.get("sqlselect")==null)?"":""+desc.get("sqlselect");}
    /**
     * Getting the description connected to the molecule list (cgrid) on the end user interface
     * @deprecated
     * @return showed description
     */
    public String getDescClist(){return (desc.get("clist")==null)?"":""+desc.get("clist");}
    /**
     * Getting the description connected to the job specific configuration port on the end user interface
     * @return showed description
     */
    public String getDescCprop(){return (desc.get("cprop")==null)?"":""+desc.get("cprop");}
    
    
}
