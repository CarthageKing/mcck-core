package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.util.AppCustomConstants;
import org.carthageking.mc.mcck.core.json.McckJsonUtil;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

// this is the class that will get the username context from the HTTP request.
// it will then populate the MDC context with the username so the username
// can be retrieved from the audit listeners
@Component
// the annotation below ensures that this filter is setup by Spring as one
// of the first filters to be executed
@Order(1)
public class OAuthFilter implements Filter {

	private static final String BEARER_TOKEN = "Bearer ";

	public OAuthFilter() {
		// noop
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		String authVal = httpReq.getHeader(HttpHeaders.AUTHORIZATION);
		String username = null;
		if (StringUtils.hasText(authVal) && authVal.startsWith(BEARER_TOKEN)) {
			String b64Str = authVal.substring(BEARER_TOKEN.length()).trim();
			String jsonStr = new String(Base64.getDecoder().decode(b64Str), StandardCharsets.UTF_8);
			JsonNode jn = McckJsonUtil.toJsonNode(jsonStr);
			// TODO: basic checks of the JWT
			JsonNode usernameN = McckJsonUtil.extractField(jn, "uid");
			if (!McckJsonUtil.isNull(usernameN)) {
				username = usernameN.asText();
			}
		}
		if (null == username) {
			chain.doFilter(request, response);
		} else {
			MDC.put(AppCustomConstants.MDC_KEY_USERNAME, username);
			try {
				chain.doFilter(request, response);
			} finally {
				MDC.remove(AppCustomConstants.MDC_KEY_USERNAME);
			}
		}
	}
}
