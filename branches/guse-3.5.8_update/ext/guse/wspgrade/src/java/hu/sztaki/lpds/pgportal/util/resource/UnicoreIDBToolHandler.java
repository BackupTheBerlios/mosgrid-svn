package hu.sztaki.lpds.pgportal.util.resource;

import dci.data.Item;
import dci.data.Middleware;
import java.util.List;
import java.util.Vector;

import org.unigrids.x2006.x04.services.tss.ApplicationResourceType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import de.fzj.unicore.uas.TargetSystemFactory;
import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.security.ClientProperties;
import de.fzj.unicore.wsrflite.security.ISecurityProperties;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;

/**
 * Accessing the UNICORE IDB 
 *
 * @author sandra gesing, patrick schäfer
 */
public class UnicoreIDBToolHandler {

//  final static String CERTS_DIR = "/usr/local/guseuser/tomcat/temp/users";

  public static Vector<String> getIDBTools(
          String user, String resource, List<Middleware> sessionConfig) {
    System.out.println("::: UnicoreIDBToolHandler ::: getIDBTools " + user + " @ " + resource );

    Vector<String> idbTools = new Vector<String>();
    idbTools.add(" ");

    if ((resource != null) && (!resource.trim().equals(""))) {
      try {
        resource = resource.trim();
        RegistryClient registryClient = initRegistryClient(user, resource, sessionConfig);
        List<EndpointReferenceType> tsfEPRs = registryClient.listAccessibleServices(TargetSystemFactory.TSF_PORT);

        for (EndpointReferenceType epr : tsfEPRs) {
          String serverUrl = epr.getAddress().getStringValue().trim();

          ClientProperties securityProperties = makeSecurityPropertiesSAML(user, resource, sessionConfig);
          TSFClient tsf = new TSFClient(serverUrl, epr, securityProperties);

          if (tsf != null
              && tsf.getResourcePropertiesDocument() != null) {
            for (ApplicationResourceType app: tsf.getResourcePropertiesDocument().getTargetSystemFactoryProperties().getApplicationResourceArray()){
              String out = app.getApplicationName()+" "+app.getApplicationVersion();
              if (!idbTools.contains(out)) {
                idbTools.add(out);
              }
            }
          }
        }
      } catch (Exception t){
        System.out.println("::: UnicoreIDBToolHandler ::: Error: " + t.getMessage());
        t.getStackTrace();
      }
    }

    return idbTools;
  }


  public static RegistryClient initRegistryClient(
          String user, String jobResource, List<Middleware> sessionConfig) throws Exception{
    try {
      String url = "https://" + jobResource + "/REGISTRY/services/Registry?res=default_registry";
      System.out.println("::: UnicoreIDBToolHandler ::: Connecting to resource: " + url + "\t : " + user);

      EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
      epr.addNewAddress().setStringValue(url);
      ClientProperties p = makeSecurityPropertiesSAML(user, jobResource, sessionConfig);
      return new RegistryClient(url, epr, p);
    } catch (Throwable t){
      System.out.println("::: UnicoreIDBToolHandler ::: Error: " + t.getMessage());
      t.printStackTrace();
    }
    return null;
  }

  public static Middleware getMiddleware(List<Middleware> middleware, String pName) throws NullPointerException{
    for(Middleware t: middleware)
      if(t.getType().equals(pName)) return t;
    throw new NullPointerException(pName +" middleware type not configured");
  }

  public static Item getItem(List<Middleware> middleware, String pMiddleware, String pName) throws NullPointerException{
    Middleware tmp = getMiddleware(middleware, pMiddleware);
    for(Item t:tmp.getItem())
      if(t.getName().equals(pName)) return t;
    throw new NullPointerException(pMiddleware +" middleware not  includes "+pName);
  }

  private static ClientProperties makeSecurityPropertiesSAML(
          String user, String resource, List<Middleware> sessionConfig) {
//    String jobdir = Base.getI().getJobDirectory(pJob.getId());
//    String resource = pJob.getConfiguredResource().getVo();

    Item.Unicore config = getItem(sessionConfig, "unicore", resource).getUnicore();

    //System.out.println("::: UnicoreIDBToolHandler ::: Keystore: "
    //                      + config.getKeystore() + "\t Truststore " + config.getTruststore());

    ClientProperties p = new ClientProperties();

    p.setProperty(ISecurityProperties.WSRF_SSL, "true");
    p.setProperty(ISecurityProperties.WSRF_SSL_CLIENTAUTH, "true");

    p.setProperty(ISecurityProperties.WSRF_SSL_KEYSTORE, config.getKeystore());
    p.setProperty(ISecurityProperties.WSRF_SSL_KEYTYPE, "pkcs12");
    p.setProperty(ISecurityProperties.WSRF_SSL_KEYPASS, config.getKeypass());
    p.setProperty(ISecurityProperties.WSRF_SSL_KEYALIAS, config.getKeyalias());

    p.setProperty(ISecurityProperties.WSRF_SSL_TRUSTSTORE, config.getTruststore());
    p.setProperty(ISecurityProperties.WSRF_SSL_TRUSTPASS, config.getTrustpass());
    p.setProperty(ISecurityProperties.WSRF_SSL_TRUSTTYPE, "JKS");

    p.setSignMessage(true);
//    p.getETDSettings().setExtendTrustDelegation(true);

//    List<TrustDelegation> trustDelegations = new ArrayList<TrustDelegation>();

//    File tdFile = new File(jobdir + "/" + user + "/x509up.assertion");
//
//    AssertionDocument td = AssertionDocument.Factory.parse(tdFile);
//    trustDelegations.add(new TrustDelegation(td));
//
//    if (trustDelegations != null && !trustDelegations.isEmpty()) {
//        p.getETDSettings().setTrustDelegationTokens(trustDelegations);
//    }

    return p;
  }
}
