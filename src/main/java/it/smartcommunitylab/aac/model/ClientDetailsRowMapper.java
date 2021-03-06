/**
 *    Copyright 2015-2019 Smart Community Lab, FBK
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
 */

package it.smartcommunitylab.aac.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

import it.smartcommunitylab.aac.repository.UserRepository;

/**
 * DB mapper for the client app information
 * 
 * @author raman
 *
 */
public class ClientDetailsRowMapper implements RowMapper<ClientDetails> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

	private static ObjectMapper mapper = new ObjectMapper();

	private final UserRepository userRepository;
	
	public ClientDetailsRowMapper(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	public ClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		BaseClientDetails details = new BaseClientDetails(rs.getString("client_id"), rs.getString("resource_ids"),
				rs.getString("scope"), rs.getString("authorized_grant_types"), rs.getString("authorities"),
				rs.getString("web_server_redirect_uri"));
		details.setClientSecret(rs.getString("client_secret"));
		
		//respect db definition
		//baseClient grants [ "authorization_code", "refresh_token"] by default when nothing is set
		//also clear the list to avoid empty grant types set like [,"authorization_code"]
		//TODO fix also interface/controller
        if (StringUtils.hasText(rs.getString("authorized_grant_types"))) {
            String[] grantTypes = StringUtils.commaDelimitedListToStringArray(rs.getString("authorized_grant_types"));
            details.setAuthorizedGrantTypes(Arrays.stream(grantTypes)
                    .filter(g -> !g.isEmpty())
                    .collect(Collectors.toSet()));
        } else {
            details.setAuthorizedGrantTypes(Collections.emptyList());
        }
		if (rs.getObject("access_token_validity") != null) {
			details.setAccessTokenValiditySeconds(rs.getInt("access_token_validity"));
		}
		if (rs.getObject("refresh_token_validity") != null) {
			details.setRefreshTokenValiditySeconds(rs.getInt("refresh_token_validity"));
		}
		String json = rs.getString("additional_information");
		if (json != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> additionalInformation = mapper.readValue(json, Map.class);
				details.setAdditionalInformation(additionalInformation);
			} catch (Exception e) {
				logger.warn("Could not decode JSON for additional information: " + details, e);
			}
		} else {
			details.setAdditionalInformation(new HashMap<String, Object>());
		}
		
		// merge developer roles into authorities
		it.smartcommunitylab.aac.model.User developer = userRepository.findOne(rs.getLong("developerId"));
		if (developer != null) {
			List<GrantedAuthority> list = new LinkedList<GrantedAuthority>();
			if (details.getAuthorities() != null) list.addAll(details.getAuthorities());
			list.addAll(developer.getRoles()/*.stream().filter(r -> !StringUtils.isEmpty(r.getContext())).collect(Collectors.toList())*/);
			details.setAuthorities(list);
		}
		return details;
	}

}
