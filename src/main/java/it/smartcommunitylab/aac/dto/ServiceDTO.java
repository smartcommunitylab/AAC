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

package it.smartcommunitylab.aac.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.smartcommunitylab.aac.Config.AUTHORITY;
import it.smartcommunitylab.aac.model.ServiceClaim;

/**
 * @author raman
 *
 */
public class ServiceDTO {

	private String serviceId;
	private String name;
	private String description;
	private String namespace;
	private String context;
	private String claimMapping;
	private List<ServiceScopeDTO> scopes;
	private List<ServiceClaimDTO> claims;
	
	private Map<String, ServiceClaimDTO> claimMap;
	
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getClaimMapping() {
		return claimMapping;
	}

	public void setClaimMapping(String claimMapping) {
		this.claimMapping = claimMapping;
	}

	public List<ServiceScopeDTO> getScopes() {
		return scopes;
	}

	public void setScopes(List<ServiceScopeDTO> scopes) {
		this.scopes = scopes;
	}

	public List<ServiceClaimDTO> getClaims() {
		return claims;
	}

	public void setClaims(List<ServiceClaimDTO> claims) {
		this.claims = claims;
	}
	
	public Map<String, ServiceClaimDTO> claimMap() {
		if (claimMap == null) {
			claimMap = new HashMap<>();
			if (claims != null) claims.forEach(c -> claimMap.put(ServiceClaim.qualifiedName(namespace, c.claim), c));
		}
		return claimMap;
	}

	public static class ServiceScopeDTO {
		private String scope;
		private String serviceId;
		private String name;
		private String description;
		private List<String> claims;
		private List<String> roles;
		private AUTHORITY authority;
		private boolean approvalRequired = false;

		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public List<String> getClaims() {
			if (claims == null) claims = new LinkedList<String>();
			return claims;
		}
		public void setClaims(List<String> claims) {
			this.claims = claims;
		}
		public List<String> getRoles() {
			if (roles == null) roles = new LinkedList<String>();
			return roles;
		}
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}
		public AUTHORITY getAuthority() {
			return authority;
		}
		public void setAuthority(AUTHORITY authority) {
			this.authority = authority;
		}
		public boolean isApprovalRequired() {
			return approvalRequired;
		}
		public void setApprovalRequired(boolean approvalRequired) {
			this.approvalRequired = approvalRequired;
		}		
	}
	
	public static class ServiceClaimDTO {
		private Long claimId;
		private String claim;
		
		private String serviceId;
		private String name;
		private boolean multiple;
		private String type;
		
		public Long getClaimId() {
			return claimId;
		}
		public void setClaimId(Long claimId) {
			this.claimId = claimId;
		}
		public String getClaim() {
			return claim;
		}
		public void setClaim(String claim) {
			this.claim = claim;
		}
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isMultiple() {
			return multiple;
		}
		public void setMultiple(boolean multiple) {
			this.multiple = multiple;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
	}

}
