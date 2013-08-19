# Common housekeeping before executing LFC and SE-related stuffs: set
# environment variables, update proxy, ...

# Set LCG_GFAL_INFOSYS if $VONAME is set
if [ -n "$VONAME" ]; then
    HOST=`grep $VONAME ${PORTAL_PREFIX_DIR}/users/.grid.resources.conf -w | awk '{print $4}'`
    PORT=`grep $VONAME ${PORTAL_PREFIX_DIR}/users/.grid.resources.conf -w | awk '{print $5}'`
    [ -n "$HOST" -a -n "$PORT" ] && export LCG_GFAL_INFOSYS=$HOST:$PORT
fi

# Set proxy certificate to use if $CERTPATH and $VONAME are set
if [ -n "$CERTPATH" -a -n "$VONAME" ]; then
    [ -f $CERTPATH/x509up.$VONAME ] && export X509_USER_PROXY=$CERTPATH/x509up.$VONAME
    [ -f $CERTPATH/x509up.${VONAME}_GLITE_BROKER ] && export X509_USER_PROXY=$CERTPATH/x509up.${VONAME}_GLITE_BROKER
    [ -f $CERTPATH/x509up.${VONAME}_LCG_2_BROKER ] && export X509_USER_PROXY=$CERTPATH/x509up.${VONAME}_LCG_2_BROKER
fi

# Export LFC_HOST if it is set
[ -n "$LFC_HOST" ] && export LFC_HOST

# Set LCG_CATALOG_TYPE to "lfc"
export LCG_CATALOG_TYPE=lfc

# exports LCG_GFAL_INFOSYS
export LCG_GFAL_INFOSYS=$BDII:2170

# Update proxy with VOMS extensions
if [ -n "$CERTPATH" -a -n "$VONAME" ]; then
    voms-proxy-init --voms $VONAME --noregen -out $CERTPATH/x509up.$VONAME.voms &> /dev/null
    [ $? = 0 ] && export X509_USER_PROXY=$CERTPATH/x509up.$VONAME.voms
fi

function remove_voms_proxy()
{
    [ -n "$CERTPATH" -a -n "$VONAME" ] && rm -f $CERTPATH/x509up.$VONAME.voms
}
