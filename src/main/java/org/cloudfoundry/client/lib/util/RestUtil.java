package org.cloudfoundry.client.lib.util;

import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.rest.LoggingRestTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Some helper utilities for creating classes used for the REST support.
 *
 * @author Thomas Risberg
 */
public class RestUtil {

	public RestTemplate createRestTemplate(HttpProxyConfiguration httpProxyConfiguration) {
		RestTemplate restTemplate = new LoggingRestTemplate();
		restTemplate.setRequestFactory(createRequestFactory(httpProxyConfiguration));
		return restTemplate;
	}

	public ClientHttpRequestFactory createRequestFactory(HttpProxyConfiguration httpProxyConfiguration) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		if (httpProxyConfiguration != null) {
			HttpHost proxy = new HttpHost(httpProxyConfiguration.getProxyHost(), httpProxyConfiguration.getProxyPort());
			requestFactory.getHttpClient().getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			
		}
		requestFactory.setHttpClient(getSecuredHttpClient(requestFactory.getHttpClient()));
		return requestFactory;
	}
	private static DefaultHttpClient getSecuredHttpClient(HttpClient httpClient) {
		final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return _AcceptedIssuers;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, new SecureRandom());
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
			return new DefaultHttpClient(ccm, httpClient.getParams());
		} catch (Exception e) {
			//System.out.println("=====:=====");
			e.printStackTrace();
		}
		return null;
	}
	public OauthClient createOauthClient(URL authorizationUrl, HttpProxyConfiguration httpProxyConfiguration) {
		return new OauthClient(authorizationUrl, createRestTemplate(httpProxyConfiguration));
	}
}
