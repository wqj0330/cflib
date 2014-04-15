package org.cloudfoundry.client.lib.util;

import java.net.URL;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.beans.factory.FactoryBean;

public class CloudFoundryClientFactory implements FactoryBean {
	private String cloudControllerUrl;

	public void setCloudControllerUrl(String cloudControllerUrl) {
		this.cloudControllerUrl = cloudControllerUrl;
	}

	public Object getObject() throws Exception {
		CloudFoundryClient client = null;
		try {
			URL cloudfoundryUrl = new URL(cloudControllerUrl);
			client = new CloudFoundryClient(cloudfoundryUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}

	public Class getObjectType() {
		// TODO Auto-generated method stub
		return CloudFoundryClient.class;
	}

	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return true;
	}

}
