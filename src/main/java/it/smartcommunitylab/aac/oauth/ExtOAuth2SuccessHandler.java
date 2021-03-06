/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package it.smartcommunitylab.aac.oauth;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Redirects to the specified URL passing all the identity attributes as parameters.
 * @author raman
 *
 */
public class ExtOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	public ExtOAuth2SuccessHandler(String defaultTargetUrl) {
		super(defaultTargetUrl);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2Authentication oauth = (OAuth2Authentication) authentication;
		@SuppressWarnings("unchecked")
		Map<String,Object> details = (Map<String,Object>)oauth.getUserAuthentication().getDetails();
		details = preprocess(details);
		try {
			URIBuilder builder = new URIBuilder(getDefaultTargetUrl());
			for (String key: details.keySet()) {
				builder.addParameter(key, details.get(key).toString());
				request.setAttribute(key, details.get(key));
			}
			request.getRequestDispatcher(builder.build().toString()).forward(request, response);
//			response.sendRedirect("forward:"+builder.build().toString());
//			getRedirectStrategy().sendRedirect(request, response, builder.build().toString());
		} catch (URISyntaxException e) {
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * @param details
	 * @return
	 */
	protected Map<String, Object> preprocess(Map<String, Object> details) {
		Map<String, Object> result = new HashMap<>();
		if (details != null) {
			flatten(details, "", result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void flatten(Map<String,Object> map, String pre, Map<String, Object> result) {
		String prefix = pre.length() > 0 ? (pre + ".") : ""; 
		map.forEach((k,v) -> {
			if (v != null && (v instanceof Map)) flatten((Map<String,Object>)v, prefix+k, result);
			else result.put(prefix + k,v);
		});
	}
	
}