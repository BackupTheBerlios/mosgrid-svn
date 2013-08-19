package de.fzj.unicore.security.etd;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
//import java.util.logging.Level;
//import java.util.logging.Logger;


import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;
import eu.unicore.security.UnicoreSecurityFactory;
import eu.unicore.security.etd.DelegationRestrictions;
import eu.unicore.security.etd.TrustDelegation;
import javax.swing.filechooser.FileFilter;

/**
 * The ETDApplet to generate trusted delegation. 
 * The trusted delegation object will be stored to the local file.
 * 
 * @author v.huber (Valentina Huber, FZ Juelich)
 * @author T. Schlemmer (Tobias Schlemmer, TU Dresden/Uni Tübingen)
 * 
 */
public class ETDApplet extends JApplet {


    public class ETDFilter extends FileFilter {
	public String extension = null;
	public String extension2 = null;
	public String description = null;

	public ETDFilter() {
	    this("PKCS12 file (*.p12)",".p12");  //default file type extension.
	}

	public ETDFilter(String desc, String ext) {
	    extension = ext;
	    description = desc;
	}

	public ETDFilter(String desc, String ext, String ext2) {
	    extension = ext;
	    extension2 = ext2;
	    description = desc;
	}

	@Override 
	public boolean accept(File f) {
	    if (f.isDirectory())
		return true;
	    if (extension == null) {
		return true;
	    } else if (extension2 == null) {
		return  (f.getName().toLowerCase().endsWith(extension)); 
	    } else {
		return  (f.getName().toLowerCase().endsWith(extension)) 
		    ||  (f.getName().toLowerCase().endsWith(extension2));
	    }
	}

	public String getDescription() {
	    return description;
	}
    }

    private static final long serialVersionUID = 5062917987774003386L;
    public static final String DEFAULT_SUBJECT = "ETDApplet";
    public static final int DEFAULT_MAX_TD_EXTENTIONS = 10; //the "10" is important because otherwise the registry cannot send it further to other proxies
    private Logger logger = Logger.getLogger(ETDApplet.class);

    /** Server Session ID */
    private WebParam jsessionid = new WebParam("JSESSIONID", "Java Session ID");
    /** the URL where the assertion shall be submitted to */
    private WebParam actionParam = new WebParam("action", "Submittion URL");
    /** the URL to redirect the browser after upload */
    private WebParam redirectParam = new WebParam("redirect", "Redirect to this url after submission");

    /** the name of the local keystore file, containing a private key */
    private WebParam keystoreParam = new WebParam("keystore", "User Certificate (.p12)");////////////!!!!!!
    /** password to the keystore */
    private WebParam passwdParam = new WebParam("password", "Passphrase");
    /** alias of the private key saved in the keystore */
    private WebParam aliasParam = new WebParam("alias", "Alias");

    /** grid resources */
    private WebParam resources = new WebParam("resources", "Grid resource");
    /** distinguished name of the TD subject */
    private WebParam subjectParam = new WebParam("subject", "Generate for");
    /** output file for issued TD */
    private WebParam targetParam = new WebParam("target", "Target File");
    /** duration of the TD in days (default = certificate's validity ) */
    private WebParam validityParam = new WebParam("validity", "Validity (in days)");
    /** number of the possible TD prolongations (default=1) */
    private WebParam maxExtensionsParam = new WebParam("maxExtensions", "Max. Extensions");

    /** hide certain information from end users */
    private WebParam hideValidityParam = new WebParam("hideValidity", "Hide validity");
    private WebParam hideResourcesParam = new WebParam("hideResources", "Hide resources");
    private WebParam hideDownloadParam = new WebParam("hideDownload", "Hide download button");

    /** debug switches */
    private WebParam generateNegativeValidityParam = new WebParam("generateNegativeValidity", "Debug only! Do not prevent users from generating expired assertions.");

    /** List of required applet parameters.
     *  If one of them is missing in the parameter list of the applet
     *  an appropriated graphical component will be displayed.
     */
    private WebParam[] requiredParams = {
	keystoreParam, 
	passwdParam, 
	actionParam,
	jsessionid,
	redirectParam
    };
    private WebParam[] optionalParams = {
	targetParam, 
	aliasParam, 
	validityParam, 
	maxExtensionsParam, 
	subjectParam,
	hideValidityParam,
	hideResourcesParam,
	hideDownloadParam,
	generateNegativeValidityParam
    };
    private WebParam[] booleanParams = {
	hideValidityParam,
	hideResourcesParam,
	hideDownloadParam,
	generateNegativeValidityParam
    };
    /** The list of missing parameters (used internally to display an error message) */
    private ArrayList<WebParam> missingParams = new ArrayList<WebParam>();
    /** GUI panel if some required parameters are missing or invalid */
    private ETDPanel etdPanel;

    private Object[] optiondns;
    private Object[] optionalias;    

    /* (non-Javadoc)
     * @see java.applet.Applet#init()
     */
    /**
     * @see java.applet.Applet#init()
     */
    @Override
	public void init() {
	BasicConfigurator.configure();
	logger.trace("init()");
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch(Exception ex) {
	    logger.error("Cannot change look and feel",ex);
	}
	try {
            readParameters();
	    
            if (!validInput()) {
                createAndShowGUI();
		logger.trace("Created GUI");
            } else {
		logger.trace("Generating Assertion");
                generateTD();
            }
        } catch (Exception e) {
            createAndShowGUI();
	    logger.trace("Finished second try to create GUI");
            error("Failure generating assertion:", e);
        }
    }

    public String getCertsDirectory() {
	logger.trace("getCertsDirectory()");
        String docPath = getDocumentBase().getPath();
        logger.info("Document path: " + docPath);
        String basePath = docPath.substring(0, docPath.lastIndexOf("/"));
        return basePath + File.separator + "../certs";
    }

    /**
     *  Issue trusted delegation
     * @throws Exception
     */
    public TrustDelegation generateTD() throws Exception {
	logger.trace("generateTD()");
        File keystoreFile = getFile(keystoreParam.getValue());
        KeyStore keystore = KeyStoreLoader.loadKeyStore(keystoreFile, passwdParam.getValue());

        if (aliasParam.getValue() == null) {
            // extract alias from the keystore if it is not specified
            String alias = getAliasFromKeyStore(keystore);

            // if a user clicked a cancel button do nothing
            if (alias == null) {
                return null;
            }
            aliasParam.setValue(alias);
        }

        SecurityProperties securityProps = new SecurityProperties(keystore, passwdParam.getValue(), aliasParam.getValue());

        // set validity of the TD if not specified equals to the certificate validity
        if (validityParam.getValue() == null) {
            //getCertificateValidity function isn't working properly!!!
            validityParam.setValue(securityProps.getCertificateValidity());
        }

        if (maxExtensionsParam.getValue() == null) {
            maxExtensionsParam.setValue(DEFAULT_MAX_TD_EXTENTIONS);
        }

        // set issuer parameter empty if not specified
        if (subjectParam.getValue() == null) {
            subjectParam.setValue("");
        }

	return doIssueTD(subjectParam.getValue(), securityProps);
    }

    /**
     *  Save a newly generated trusted delegation
     * @throws Exception
     */
    public void saveTD() throws Exception {
	logger.trace("saveTD");
        TrustDelegation td = generateTD();
	if (td == null) return;
        File targetFile = getFile(targetParam.getValue());
        saveTD(td, targetFile);

        info("Assertion successfully generated", "Assertion for '" + aliasParam.getValue()
	     + "' saved to file " + targetParam.getValue() + " \nUsed dn:" + subjectParam.getValue());
    }

    /**
     *  Submit a newly generated trusted delegation
     * @throws Exception
     */
    public void submitTD() throws Exception {
	logger.trace("submitTD");
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary =  "***gUSE+MoSGrid-assertion***";

	try {
	    TrustDelegation td = generateTD(); 
	    if (td==null) {
                return;
	    }

	    
	    URL url = new URL(actionParam.getValue());
	    //	    URL url = new URL("http://schlemmersoft.de/info.php");
	    HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
	

	    // @todo: see http://docs.oracle.com/javase/tutorial/deployment/doingMoreWithRIA/accessingCookies.html
	    // for more information about cookies

	    connection.setRequestMethod  ("POST");
	    connection.setRequestProperty("Cookie",       "JSESSIONID=" + jsessionid.getValue());
	    connection.setRequestProperty("Connection",   "Keep-Alive"); //??
	    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
	    
	    connection.setDoInput(true);
	    connection.setDoOutput(true);
	    connection.setUseCaches(false);
	    connection.setFollowRedirects(true);

	    // @todo: Check against browser redirect example
	    //	    connection.setDoInput(true);
	    

	    connection.connect();

	    DataOutputStream stream = new DataOutputStream( connection.getOutputStream() );
	    stream.writeBytes(twoHyphens + boundary + lineEnd);
	    stream.writeBytes("content-disposition: form-data; name=\"sresource\"" + lineEnd);
	    stream.writeBytes(lineEnd);
	    stream.writeBytes(URLEncoder.encode("","UTF-8") + lineEnd);
	    
	   
	    stream.writeBytes(twoHyphens + boundary + lineEnd);
	    stream.writeBytes("content-disposition: form-data; name=\"samlFile\";"
			      + " filename=\"" + keystoreParam.getValue() +".ass\"" + lineEnd);
	    stream.writeBytes("Content-Type: application/samlassertion+xml" + lineEnd);
	    stream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
	    stream.writeBytes(lineEnd);
	    sendTD(td, stream);

	    stream.writeBytes(lineEnd);
	    stream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd + lineEnd);

	    // close streams
	
	    stream.flush();
	    stream.close();

	    String answer = null;
	    try {
		BufferedReader reader
		    = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		answer = reader.readLine();
		StringBuilder sb = new StringBuilder();
		while (answer != null) {
		    sb.append(answer).append("\n");
		    answer = reader.readLine();
		}
		answer = sb.toString();
		logger.info(answer);
	    } catch (Exception e) {
		e.printStackTrace();
		logger.info("Character encoding not matched.");
	    }
	    

	    connection.disconnect();

	    info("Assertion successfully generated", "The assertion for '" + aliasParam.getValue() +  
		 "' has been successfully generated." +
		 "\n\nThe server replied:\n" + answer +
		 "\n\nUsed dn:" + subjectParam.getValue());
	    // there is no point attempting to show
	    // a document if the URL was malformed..

	    url = new URL(redirectParam.getValue());
	    getAppletContext().showDocument(url);

        } catch (MalformedURLException ex) {
            error("Malformed action URL “"+actionParam.getValue()+"”",ex);
        } catch (IOException ioe) {
            error("IO Exception during submission of the assertion",ioe);
        }
    }


    /**
     * Generates the assertion object.
     *
     * @param subject
     *            distinguished name of the TD subject
     * @param secProps
     *            security properties containing private key and public key of the TD issuer
     * @return
     * @throws Exception
     */
    protected TrustDelegation doIssueTD(String subject,
					SecurityProperties secProps) throws Exception {

        logger.info("Starting generation of assertion ...");

        //DelegationRestrictions restr = new DelegationRestrictions(new Date(),validityParam.getD(), maxExtensionsParam.getIntValue());
        DelegationRestrictions restr = new DelegationRestrictions(new Date(),
								  validityParam.getIntValue(), 
								  maxExtensionsParam.getIntValue());
	/*validityParam.getIntValue(), maxExtensionsParam.getIntValue());*/

        TrustDelegation td = UnicoreSecurityFactory.getETDEngine()
	    .generateTD(secProps.getPublicKey().getSubjectX500Principal().getName(),
			secProps.getCertChain(), secProps.getPrivateKey(), subject,
			restr);

        return td;
    }

    /**
     * Reads the applet parameters for trusted delegetion settings.
     */
    protected void readParameters() {

        logger.info("Reading applet parameters ...");

        for (WebParam param : requiredParams) {
            param.setValue(getParameter(param.getKeyword()));
        }

        for (WebParam param : optionalParams) {
            param.setValue(getParameter(param.getKeyword()));
        }

	String s;
	for (WebParam param : booleanParams) {
	    s = param.getValue();
	    if (s != null) {
		if ("FALSE".equalsIgnoreCase(s))
		    param.setValue(null);
		else if ("TRUE".equalsIgnoreCase(s))
		    param.setValue("true");
		else try {
			if (param.getIntValue() != 0) 
			    param.setValue("true");
			else
			    param.setValue(null);
		    } catch (NumberFormatException e) {
			param.setValue(null);
		    }
	    }
	}
    }

    /**
     * Prints the logger information about parameter settings
     */
    public void printParams() {
	logger.trace("printParams");
	if(passwdParam != null) {
	    passwdParam.setHideValue(true);
	}

	StringBuilder sb = new StringBuilder("Parameter settings:\n");
	ArrayList<WebParam> allParams = new ArrayList<WebParam>(Arrays.asList(requiredParams));
	allParams.addAll(Arrays.asList(optionalParams));

        for (WebParam param : allParams) {
            sb.append(param.toString()).append("\n");
        }

        logger.info(sb.toString());
    }

    /**
     *  Get certificate validity of a given file name
     * @param filename
     * @return
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateExpiredException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     */
    public int getCertificateValidity(String filename) throws IOException, 
							      KeyStoreException, 
							      CertificateExpiredException,
							      NoSuchAlgorithmException,
							      CertificateException,
							      UnrecoverableKeyException {
	logger.trace("getCertificateValidity("+filename+")");
        File keystoreFile = getFile(filename);
        KeyStore keystore = KeyStoreLoader.loadKeyStore(keystoreFile, passwdParam.getValue());

        if (aliasParam.getValue() == null) {
            // extract alias from the keystore if it is not specified
            String alias = getAliasFromKeyStore(keystore);

            // if a user clicked a cancel button do nothing
            if (alias == null) {
                throw new UnrecoverableKeyException("No alias found in certificate.");
            }
            aliasParam.setValue(alias);
        }

        SecurityProperties securityProps = new SecurityProperties(keystore, passwdParam.getValue(), aliasParam.getValue());
	return securityProps.getCertificateValidity();

        // set validity of the TD if not specified equals to the certificate validity
	/*
	  validityParam.setD(securityProps.getPublicKey().getNotAfter());
	*/
    }


    /**
     * Checks the value of the required parameters and if some of them are
     * not specified, then they will be stored in the list of missing parameters.
     *
     * @return true if all required parameters have been specified.
     * @throws KeyStoreException
     */
    public boolean validInput() throws KeyStoreException {
	logger.trace("validating input");
        missingParams.clear();
        for (WebParam par : requiredParams) {
            if (par.getValue() == null) {
                missingParams.add(par); // undefined parameter value
            }
        }
        return (missingParams.size() == 0);
    }

    public ArrayList<WebParam> getMissingParams() {
	logger.trace("return missing parameters");
        return missingParams;
    }

    public void setMissingParams(ArrayList<WebParam> missingParams) {
	logger.trace("setting missing parameters");
        this.missingParams = missingParams;
    }

    /**
     * Extracts the alias from the keystore.
     * If the keystore contains several certificates, then a combobox with aliases
     * will be displayed to select one of them.
     *
     * @param keystore containing the alias(es)
     * @return the alias. If the keystore contains more then one, then it has to be specified by a user)
     * @throws KeyStoreException
     */
    public String getAliasFromKeyStore(KeyStore keystore) throws KeyStoreException {
	logger.trace("getAliasFromKeyStore");
        ArrayList<String> list = new ArrayList<String>();
        for (Enumeration<String> e = keystore.aliases(); e.hasMoreElements();) {
            list.add(e.nextElement());
        }
        String[] aliases = new String[list.size()];
        list.toArray(aliases);

        /*
         * If the keystore contains only one certificate set its alias as a default value.
         */
        if (aliases.length == 1) {
            return aliases[0];
        } else if (aliases.length > 1) {
            return chooseAlias(aliases);
        } else {
            throw new KeyStoreException("No aliases in the certificate");
        }
    }

    /**
     * Displays the list of aliases in the popup window and ask user to choose
     * one of them
     *
     * @param aliases
     * @return null if a user click on CANCEL button.
     */
    protected String chooseAlias(String[] aliases) {
	logger.trace("chooseAlias");
        Object result = JOptionPane.showInputDialog(this, "Aliases",
						    "Select alias", JOptionPane.OK_CANCEL_OPTION, null, aliases,
						    aliases[0]);

        return (result != null) ? (String) result : null;
    }

    /**
     * Writes the trusted delegation to the local file.
     *
     * @param td
     *            issued trusted delegation object
     * @param target
     *            the name of the output file to store TD
     * @throws IOException
     */
    public void saveTD(TrustDelegation td, File target) throws IOException {

        logger.info("Saving assertion to file " + target.getCanonicalPath() + " ...");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            AssertionDocument doc = td.getXML();
            doc.save(fos);
            fos.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
    /**
     * Writes the trusted delegation to the local file.
     *
     * @param td
     *            issued trusted delegation object
     * @param s
     *            stream which is used to transmit the TD
     * @throws IOException
     */
    public void sendTD(TrustDelegation td, DataOutputStream s) throws IOException {

        logger.info("Sending assertion ...");

	AssertionDocument doc = td.getXML();
	doc.save(s);
    }

    /**
     *  create GUI components
     */
    protected synchronized void createAndShowGUI() {
	logger.trace("createAndShowGUI");
        if (etdPanel != null) {
            return;
        }

        optiondns = getParameter("subjectdns").split("##");
        optionalias = getParameter("aliases").split("##");
        
	try {
	    //	Though it is recommended to use invokeAndWait,
	    //  here, that function leads to hanging browsers on
	    // Mac OS X
	    SwingUtilities.invokeLater(new Runnable() {

		    public void run() {
			logger.trace("Creating ETDPanel");
			etdPanel = new ETDPanel();
			//	
			logger.trace("Setting content pane");

			if (true) {
			    setContentPane(etdPanel);
			} else {
			    add(etdPanel);
			}
			logger.trace("Content pane set");
		    }
		});
	} catch  (Exception e) {
	    logger.error("GUI could not be generated",e);
	}
	logger.trace("returning from createAndShowGUI()");
    }

    /**
     * Displays the list of the missing parameters in the popup window.
     */
    public void displayMissingParams() {
	logger.trace("displayMissingParams");
        StringBuilder msg = new StringBuilder("Please specify the following parameters:\n");
        for (WebParam param : getMissingParams()) {
            msg.append(param.getDescription()).append("\n");
        }
        info("Missing parameters", msg.toString());
    }

    /**
     * Creates the file for the specified path, which could be null.
     * The path could be specified as an absolute or relative.
     * In the case of the relative path or null, as base directory will
     * be used the certs-directory of the applet.
     * @param path (absolute or relative)
     * @return File object or null if the path was not specified
     */
    protected File getFile(String path) {
	logger.trace("getFile("+path+")");
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            File f = new File(getCertsDirectory(), path);
            if (f.exists()) {
                file = f;
            }
        }
        return file;
    }

    /**
     * prints a log message and display an info in a popup window.
     * @param title
     * @param message
     */
    public void info(String title, String message) {
        logger.info(title + ": " + message);
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * prints a debug log message.
     * @param title
     * @param message
     */
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * prints a log message and display an error in a popup window.
     * @param title
     * @param message
     */
    public void error(String title, String message) {
        printParams();
        logger.log(Level.ERROR, title + ": " + message);
        //JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * prints a log message and display an error in a popup window.
     * In addition, it prints an exception's stack trace.
     *  and .
     * @param title
     * @param e
     */
    public void error(String title, Exception e) {
        error(title, e.toString());
        e.printStackTrace();
    }

    /**
     * GUI panel for the missing or invalid parameters
     */
    class ETDPanel extends JPanel {
	
        private static final long serialVersionUID = -1072113992578098857L;
        private JTextField keystoreTf = null;
        private JButton keystoreBtn = null;
        private JFileChooser fileChooser = null;
        private JPasswordField passwdTf = null;
	//        private JTextField targetTf = null;
        private JButton targetBtn = null;
        private JButton generateBtn = null;
        private JButton submitBtn = null;
        private JComboBox subjectAliasJCB = null;
        private JSpinner validitySf = null;

        public ETDPanel() {
	    logger.trace("ETDPanel()");
            setLayout(new GridBagLayout());
	    setBackground(Color.white);
	    setOpaque(true);

            GridBagConstraints c1 = new GridBagConstraints();
            c1.anchor = GridBagConstraints.WEST;
            c1.fill = GridBagConstraints.HORIZONTAL;
            c1.insets = new Insets(2, 2, 2, 2);

            GridBagConstraints c2 = new GridBagConstraints();
            c2.gridwidth = GridBagConstraints.REMAINDER;
            c2.fill = GridBagConstraints.HORIZONTAL;
            c2.insets = new Insets(2, 2, 2, 2);
	    

	    logger.trace("ETDPanel: Keystore field and button");
            add(new JLabel(keystoreParam.getDescription() + ":"), c1);
            add(keystoreTf = new JTextField(30), c1);
            if (keystoreParam.getValue() != null) {
                try {
                    keystoreTf.setText(getFile(keystoreParam.getValue()).getCanonicalPath());
                } catch (IOException e) {
                    logger.error("Cannot set File Name",e);
                }
            }
	    keystoreTf.setEnabled(false);
            add(keystoreBtn = new JButton("Select Certificate..."), c2);
            keystoreBtn.addActionListener(new ActionListener() {

		    public void actionPerformed(ActionEvent arg0) {
			String oldpassword = passwdParam.getValue();
			String oldalias = aliasParam.getValue();
			String oldkeystore = keystoreTf.getText();
			int retVal = chooseFile(keystoreParam, keystoreTf, true);
			if (retVal == JFileChooser.APPROVE_OPTION) {
			    retVal = setValidity(validityParam, validitySf, keystoreParam.getValue());
			    if (retVal == JOptionPane.OK_OPTION) {
				submitBtn.setDefaultCapable(true);
				keystoreBtn.setDefaultCapable(false);
				validitySf.setEnabled(true);
				submitBtn.setEnabled(true);
				if (generateBtn != null) {
				    generateBtn.setEnabled(true);
				}
			    } else {
				passwdParam.setValue(oldpassword);
				aliasParam.setValue(oldalias);
				keystoreParam.setValue(oldkeystore);
				keystoreTf.setText(oldkeystore);
			    }
			} else {
			    passwdParam.setValue(oldpassword);
			    aliasParam.setValue(oldalias);
			    keystoreParam.setValue(oldkeystore);
			    keystoreTf.setText(oldkeystore);
			}
		    }
		});
	    keystoreBtn.setDefaultCapable(true);


	    /*
            // add the textfield to specify the password to the keystore
            add(new JLabel(passwdParam.getDescription() + ":"), c1);
            add(passwdTf = new JPasswordField(30), c1);
            if (passwdParam.getValue() != null) {
	    passwdTf.setText(passwdParam.getValue());
            }
            // place holder
            add(new JLabel(), c2);
	    */

	    logger.trace("ETDPanel: validity field");
            //specify validity
            add(new JLabel(validityParam.getDescription() + ":"), c1);
	    logger.trace("ETDPanel: validity field: label done");
            add(validitySf = new JSpinner(), c1);
	    logger.trace("ETDPanel: validity field: JSpinner done");
	    try {
		validitySf.setValue(0);
		logger.trace("ETDPanel: validity field: value set");
		int minimum = 0;
		if (generateNegativeValidityParam.getValue() != null) {
		    minimum = -2;
		}
		validitySf.setModel(new SpinnerNumberModel(0,minimum,0,1));
		logger.trace("ETDPanel: validity field: model set");
	    } catch (Exception e) {
		logger.error("Error creating validity field",e);
	    }
	    validitySf.setEnabled(false);
	    logger.trace("ETDPanel: validity field disabled");
	   

	    logger.trace("ETDPanel: submit button");
	    //	    c2.anchor = GridBagConstraints.CENTER;
	    //            c2.fill = GridBagConstraints.NONE;
	    //	    targetTf = new  JTextField(30);
            add(submitBtn = new JButton("Generate assertion"), c2);
	    logger.trace("ETDPanel: generate button created");
            submitBtn.addActionListener(new ActionListener() {

		    public void actionPerformed(ActionEvent arg0) {
			try {
			    updateParams();
			    if (validInput()) {
				submitTD();
			    } else {
				displayMissingParams();
			    }
			} catch (Exception e) {
			    printParams();
			    error("Failure generating assertion", e);
			}
		    }
		});
	    logger.trace("ETDPanel: generate action created");
	    submitBtn.setDefaultCapable(false);
	    logger.trace("ETDPanel: generate cannot be default");
	    submitBtn.setEnabled(false);
	    logger.trace("ETDPanel: generate button disabled");
	    
	    logger.trace("ETDPanel: alias field");
	    if (optionalias.length > 1) {
		// add the options to specify the subject dn
		add(new JLabel(subjectParam.getDescription() + ":"), c1);

		subjectAliasJCB= new JComboBox(optionalias);
		add(subjectAliasJCB , c1);
		//            if (subjectParam.getValue() != null) {
		//                subjectDNs.setText(subjectParam.getValue());
		//            }
		// place holder
		add(new JLabel(), c2);
	    }

	    /*
	    // @todo Remove Target Parameter
	    add(new JLabel(targetParam.getDescription() + ":"), c1);
	    add(targetTf = new JTextField(30), c1);
	    if (targetParam.getValue() != null) {
		try {
		    targetTf.setText(getFile(targetParam.getValue()).getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    add(targetBtn = new JButton("Browse..."), c2);
	    targetBtn.addActionListener(new ActionListener() {

		    public void actionPerformed(ActionEvent arg0) {
			chooseFile(targetParam, targetTf, false);
		    }
		});
	    */



	    logger.trace("ETDPanel: download button");
	    if (hideDownloadParam.getValue() == null) {
		//		targetTf = new JTextField(30);
		add(generateBtn = new JButton("Download assertion"), c1);
		generateBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
			    try {
				int retVal = chooseFile(targetParam, null, false);
				if (retVal == JFileChooser.APPROVE_OPTION) {
				    updateParams();
				    boolean valid = validInput();
				    if (targetParam.getValue() == null) {
					valid = false;
					missingParams.add(targetParam);
				    }
				    if (valid) {
					saveTD();
				    } else {
					displayMissingParams();
				    }
				}
			    } catch (Exception e) {
				printParams();
				error("Failure generating assertion", e);
			    }
			}
		    });
		generateBtn.setEnabled(false);
		// place holder
		add(new JLabel(), c2);
	    }

	    logger.trace("ETDPanel() exit");
        }

        /**
         * In response to a button click to choose the file
         */
        protected int chooseFile(WebParam param, JTextField textField, boolean open) {
	    logger.trace("chooseFile");
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

	    if (open) {
		File file = null;
		if (param.getValue() != null) {
		    file = getFile(param.getValue());
		}
		if (file != null) {
		    logger.info(param.getKeyword() + ": " + file.getPath());
		}
		if (file != null && file.exists()) {
		    try {
			logger.info("Select " + param.getKeyword() + ": " + file.getCanonicalPath());
		    } catch (IOException e) {
		    }
		    fileChooser.setSelectedFile(file);
		} else {
		    fileChooser.setCurrentDirectory(new File(getCertsDirectory()));
		}
		try {
		    fileChooser.setFileFilter(new ETDFilter("PKCS12 file (*.p12)", ".p12"));
		    fileChooser.addChoosableFileFilter(new ETDFilter("Java keystore (*.ks, *.jks)", ".ks", ".jks"));
		    fileChooser.addChoosableFileFilter(new ETDFilter("SunJCE keystore (*.jceks)", ".jceks"));
		} catch (Exception e) {
		    logger.error("Could not create file extension filters",e);
		}
	    } else {
		    fileChooser.setCurrentDirectory(new File(getCertsDirectory()));
		    try {
			fileChooser.setFileFilter(new ETDFilter("Assertion files (*.ass)", ".ass"));
		    } catch (Exception e) {
			logger.error("Could not create file extension filters",e);
		    }
	    }

	    
            int retVal;
	    if (open) retVal = fileChooser.showOpenDialog(this);
	    else retVal = fileChooser.showSaveDialog(this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = fileChooser.getSelectedFile().getCanonicalPath();
                    param.setValue(path);
		    if (textField != null) {
			textField.setText(path);
		    }
                } catch (IOException e) {
                    error("Unable to specify " + param.getKeyword(), e);
                }
            }
	    return retVal;
        }

        /**
         * In response to a button click to choose the file
         */
        protected int setValidity(WebParam param, JSpinner spinnerField, String filename) {
	    logger.trace("setValidity");
	    String oldpassword = passwdParam.getValue();
	    String oldalias = aliasParam.getValue();
	    try {
		boolean again = true;
		int retval = JOptionPane.CANCEL_OPTION;
		while (again) {
		    try {
			int value = getCertificateValidity(filename);
			param.setValue(value);
			// SpinnerNumberModel model = (SpinnerNumberModel) validitySf.getModel();
			// model.setMaximum(value);
			spinnerField.setValue(value);
			int minimum = 1;
			if (generateNegativeValidityParam.getValue() != null) {
			    minimum = -2;
			}
			validitySf.setModel(new SpinnerNumberModel(value,minimum,value,1));
			again = false;
			retval = JOptionPane.OK_OPTION;
		    } catch (IOException ex) {
			JPasswordField passwordField = new JPasswordField(20);
			passwordField.addHierarchyListener( new RequestFocusListener() );
			retval = JOptionPane.showConfirmDialog(this,
						      passwordField,
						      "Enter certificate password, please!",
						      JOptionPane.OK_CANCEL_OPTION);
			if (retval == JOptionPane.OK_OPTION) {
			    passwdParam.setValue(new String(passwordField.getPassword()));
			    again = true;
			} else {
			    again = false;
			    passwdParam.setValue(oldpassword);
			    aliasParam.setValue(oldalias);
			}
		    }
		} 
		return retval;
	    } catch (CertificateExpiredException ex) {
		error("Certificate expired", 
		      "The certificate “" + filename + "” has expired.\n" +
		      "\n" +
		      "Please ensure that the validity of your certificate is extended.\n" +
		      "You can find help in the menu: Help ⇒ Certificates and Security.\n\n" +
		      "Validity of your certificate:\n" +
		      ex.getMessage());
            } catch (NoSuchAlgorithmException ex) {
		error("Wrong algorithm", 
		      "The certificate “" + filename + "” cannot be used.\n" +
		      "It uses an unknown cryptographic method.\n" + 
		      "Please ensure that your certificate matches the current security standards.\n" +
		      "If you are in daubt, contact the support, please.\n\n" +
		      "Data from your certificate:\n" +
		      ex.getMessage() + "\nCause: " + ex.getCause());
		debug(ex.toString());
		ex.printStackTrace();
            } catch (UnrecoverableKeyException ex) {
		error("Unrecoverable Key Exception", 
		      "A key from the certificate “" + filename + "” cannot be used.\n" +
		      "Please ensure that your certificate matches the current security standards\n" +
		      "and is not encrypted using more than one password.\n" +
		      "If you are in daubt, contact the support, please.\n\n" +
		      "Data from your certificate:\n" +
		      ex.getMessage() + "\nCause: " + ex.getCause());
		debug(ex.toString());
		ex.printStackTrace();
            } catch (KeyStoreException ex) {
		error("Problem with certificate file", 
		      "The certificate file “" + filename + "” cannot be opened.\n" +
		      "Please ensure that your certificate matches the current security standards\n" +
		      "and is not encrypted using more than one password.\n" +
		      "If you are in daubt, contact the support, please.\n\n" +
		      "Data from your certificate:\n" +
		      ex.getMessage() + "\nCause: " + ex.getCause());
		debug(ex.toString());
		ex.printStackTrace();
	    } catch (CertificateException ex) {
		error("Generic problem with certificate", 
		      "The certificate “" + filename + "” cannot be opened.\n" +
		      "Please ensure that your certificate matches the current security standards\n" +
		      "and is not encrypted using more than one password.\n" +
		      "If you are in daubt, contact the support, please.\n\n" +
		      "Data from your certificate:\n" +
		      ex.getMessage() + "\nCause: " + ex.getCause());
		debug(ex.toString());
		ex.printStackTrace();
            }
	    aliasParam.setValue(oldalias);
	    passwdParam.setValue(oldpassword);
	    return JOptionPane.CANCEL_OPTION;
	}


        /**
         * Updates the parameter values from the user input into the GUI elements.
         */
        protected void updateParams() {
	    logger.trace("updateParams");
            keystoreParam.setValue(keystoreTf.getText().trim());
	    debug("Keystore has been set to " + keystoreParam.getValue());
	    // Password has been set during keystore selection
	    validityParam.setValue((Integer) validitySf.getValue());
	    debug("Validity has been set to " + validityParam.getValue());
	    if (optiondns.length > 1) {
		subjectParam.setValue(optiondns[subjectAliasJCB.getSelectedIndex()].toString());
	    } else {
		subjectParam.setValue(optiondns[0].toString());
	    }
	    debug("Subject DN has been set to " + subjectParam.getValue());
	    /*
	    if (targetTf != null) {
		targetParam.setValue(targetTf.getText().trim());
	    }
	    */
	    debug("Target has been set to " + targetParam.getValue());
	    aliasParam.setValue(null); // read the alias from the specified keystore
	    debug("Alias has been set to " + aliasParam.getValue());
        }
    }
}
