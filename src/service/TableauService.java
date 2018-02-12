package service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import tableau.api.rest.bindings.ObjectFactory;
import tableau.api.rest.bindings.SiteRoleType;
import tableau.api.rest.bindings.SiteType;
import tableau.api.rest.bindings.TableauCredentialsType;
import tableau.api.rest.bindings.TsRequest;
import tableau.api.rest.bindings.TsResponse;
import tableau.api.rest.bindings.UserType;
import util.ReadConfig;

public class TableauService {

	/*
	 * Initialize Tableau REST API
	 * 
	 */
	 private static TableauService INSTANCE;
	private static Unmarshaller s_jaxbUnmarshaller;
	private static Marshaller s_jaxbMarshaller;
	private final static String TABLEAU_AUTH_HEADER = "X-Tableau-Auth";
	private enum Operation {
        SIGN_IN(getApiUriBuilder().path("auth/signin")),
        QUERY_USERS_ON_SITE(getApiUriBuilder().path("sites/{siteId}/users")),
        QUERY_ADD_USER_TO_SITE(getApiUriBuilder().path("sites/{siteId}/users")),
        SIGN_OUT(getApiUriBuilder().path("auth/signout"));
        private final UriBuilder m_builder;

        Operation(UriBuilder builder) {
            m_builder = builder;
        }

        UriBuilder getUriBuilder() {
            return m_builder;
        }

        String getUrl(Object... values) {
            return m_builder.build(values).toString();
        }
    	
    }
    
    private static UriBuilder getApiUriBuilder() {
        return UriBuilder.fromPath("http://"+ReadConfig.get("tableau.server") + "/api/"+ ReadConfig.get("tableau.api.version"));
    }
    
    private static ObjectFactory m_objectFactory = new ObjectFactory();

    
    public static TableauService instance(HttpServletRequest request){
    	if (INSTANCE == null) {
            INSTANCE = new TableauService();
            initialize(request);
        }
        return INSTANCE;
    }

    public static void initialize(HttpServletRequest request) {
        try {
        	JAXBContext jaxbContext = JAXBContext.newInstance(TsRequest.class, TsResponse.class);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(request.getServletContext().getResource(ReadConfig.get("tableau.server.schema.url")));
            s_jaxbMarshaller = jaxbContext.createMarshaller();
            s_jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            s_jaxbUnmarshaller.setSchema(schema);
            s_jaxbMarshaller.setSchema(schema);
        } catch (JAXBException | SAXException | MalformedURLException ex) {
            throw new IllegalStateException("Failed to initialize the REST API");
        } 
    }
    
    public static TsResponse invokeAddUser(TableauCredentialsType credential, UserType userType) {
    	String url = Operation.QUERY_ADD_USER_TO_SITE.getUrl(credential.getSite().getId());
        TsRequest payload = createPayloadUser(userType.getName(), null, null, null, userType.getSiteRole());
        TsResponse response = put(url, credential.getToken(), payload);
        return response;
    }
    
    private static TsResponse put(String url, String authToken, TsRequest requestPayload) {
	    StringWriter writer = new StringWriter();
        if (requestPayload != null) {
            try {
                s_jaxbMarshaller.marshal(requestPayload, writer);
            } catch (JAXBException ex) {
                ex.printStackTrace();
            }
        }
        String payload = writer.toString();
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse clientResponse = webResource.header(TABLEAU_AUTH_HEADER, authToken)
                .type(MediaType.TEXT_XML_TYPE).put(ClientResponse.class, payload);
        String responseXML = clientResponse.getEntity(String.class);
        return unmarshalResponse(responseXML);
    }
    
    private static TsRequest createPayloadUser(String name, String fullName, String email, String password, SiteRoleType siteRole) {
        TsRequest requestPayload = m_objectFactory.createTsRequest();
        UserType userType = m_objectFactory.createUserType();
        userType.setName(name);
        userType.setFullName(fullName);
        userType.setEmail(email);
        userType.setPassword(password);
        userType.setSiteRole(siteRole);
        requestPayload.setUser(userType);
        return requestPayload;
    }
    
    public static TsResponse invokeSignIn(String username, String password, String contentUrl) {
		String url = Operation.SIGN_IN.getUrl();
		TsRequest payload = createPayloadForSignin(username, password, contentUrl);
        TsResponse response = post(url, null, payload);
        return response;
    }
	
	public static void invokeSignOut(TableauCredentialsType credential) {
        String url = Operation.SIGN_OUT.getUrl();
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse clientResponse = webResource.header(TABLEAU_AUTH_HEADER, credential.getToken()).post(
                ClientResponse.class);
    }
	
	private static TsRequest createPayloadForSignin(String username, String password, String contentUrl) {
        TsRequest requestPayload = m_objectFactory.createTsRequest();
        TableauCredentialsType signInCredentials = m_objectFactory.createTableauCredentialsType();
        SiteType site = m_objectFactory.createSiteType();
        site.setContentUrl(contentUrl);
        signInCredentials.setSite(site);

        signInCredentials.setName(username);
        signInCredentials.setPassword(password);
        requestPayload.setCredentials(signInCredentials);

        return requestPayload;
    }
	
	private static TsResponse post(String url, String authToken, TsRequest requestPayload) {
        StringWriter writer = new StringWriter();

        if (requestPayload != null) {
            try {
                s_jaxbMarshaller.marshal(requestPayload, writer);
            } catch (JAXBException ex) {
                ex.printStackTrace();
            }
        }

        String payload = writer.toString();
        Client client = Client.create();
        WebResource webResource = client.resource(url);

        ClientResponse clientResponse = webResource.header(TABLEAU_AUTH_HEADER, authToken)
                .type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class, payload);

        String responseXML = clientResponse.getEntity(String.class);
        return unmarshalResponse(responseXML);
    }
    
    private static TsResponse unmarshalResponse(String responseXML) {
        TsResponse tsResponse = m_objectFactory.createTsResponse();
        try {
            StringReader reader = new StringReader(responseXML);
            tsResponse = s_jaxbUnmarshaller.unmarshal(new StreamSource(reader), TsResponse.class).getValue();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return tsResponse;
    }
	
    
	public static String getSourceViewURL(String clientRemoteAddress, String wgserver, String user,
			String destinationView, String params) throws Exception {

		String output = null;
		String ticket = getTrustedTicket(wgserver, user, clientRemoteAddress);

		if (!ticket.equals("-1")) {

			output = "http://" + wgserver + "/trusted/" + ticket + "/views" + destinationView + "?" + params;
		}

		else {
			output = "http://" + wgserver + "/views" + destinationView + "?" + params;
//			output = "unable to get tableau view URL";
		}

		return output;
	}

	private static String checkResponseStatusURL(String urlinput) {

		String output = null;

		try {

			HttpURLConnection con = (HttpURLConnection) new URL(urlinput).openConnection();

			con.setRequestMethod("GET");

			con.connect();

			DataInputStream dis = new DataInputStream(con.getInputStream());
			String inputLine;

			System.out.println("data response:");

			while ((inputLine = dis.readLine()) != null) {
				System.out.println(inputLine);
			}

			System.out.println("");
			System.out.println("end response:");

			int responseCode = con.getResponseCode();

			output = "" + responseCode;

			con.disconnect();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return output;
	}

	private static String getTrustedTicket(String wgserver, String user, String remoteAddr) {

		String output = null;

		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			// Encode the parameters
			StringBuffer data = new StringBuffer();
			data.append(URLEncoder.encode("username", "UTF-8"));
			data.append("=");
			data.append(URLEncoder.encode(user, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode("client_ip", "UTF-8"));
			data.append("=");
			data.append(URLEncoder.encode(remoteAddr, "UTF-8"));

			// Send the request
			URL url = new URL("http://" + wgserver + "/trusted");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data.toString());
			out.flush();

			// Read the response
			StringBuffer rsp = new StringBuffer();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;

			while ((line = in.readLine()) != null) {
				rsp.append(line);
			}

			output = rsp.toString();

		} catch (Exception e) {

			e.printStackTrace();

		}

		finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {

				e.printStackTrace();

			}
		}

		return output;

	}

	public boolean getAuth(String username, String password) {
		return false;
	}
	
	public static TsResponse invokeQueryUsersOnSite(TableauCredentialsType credential, int pageSize, int pageNumber) {
        String url = Operation.QUERY_USERS_ON_SITE.getUrl(credential.getSite().getId());
        TsResponse response = get(url, credential.getToken(), pageSize, pageNumber);
        return response;
    }
    
    public static TsResponse get(String url, String authToken, int pageSize, int pageNumber) {
        Client client = Client.create();
        WebResource webResource = client.resource(url).queryParam("pageSize", pageSize+"");
        webResource.queryParam("pageNumber", pageNumber+"");

        ClientResponse clientResponse = webResource.header(TABLEAU_AUTH_HEADER, authToken).get(ClientResponse.class);

        String responseXML = clientResponse.getEntity(String.class);
        return unmarshalResponse(responseXML);
    }

}
