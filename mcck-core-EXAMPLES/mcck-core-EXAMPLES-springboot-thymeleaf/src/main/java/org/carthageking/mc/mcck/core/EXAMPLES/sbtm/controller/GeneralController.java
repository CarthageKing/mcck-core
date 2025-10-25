package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.controller;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-thymeleaf
 * %%
 * Copyright (C) 2024 Michael I. Calderero
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.carthageking.mc.mcck.core.EXAMPLES.sbtm.controller.model.BasicLoginInfoForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GeneralController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GeneralController.class);

	public GeneralController() {
		// noop
	}

	@GetMapping("/")
	public String goIndex(Model model) {
		return redirectLogin(model);
	}

	@GetMapping("/login")
	public String redirectLogin(Model model) {
		model.addAttribute("loginTitle", "Welcome to da Club!");
		model.addAttribute("basicLoginInfoForm", new BasicLoginInfoForm());
		return "login";
	}

	@PostMapping("/doLogin")
	public String doLogin(Model model, RedirectAttributes redirectAttrs, @ModelAttribute("basicLoginInfoForm") BasicLoginInfoForm loginForm) {
		String username = StringUtils.trimToEmpty(loginForm.getUsername());
		String password = StringUtils.trimToEmpty(loginForm.getPassword());

		String whereToRedirect = "redirect:/";

		if (username.isEmpty() || password.isEmpty()) {
			final String errMsg = "Username or password cannot be blank";
			LOG.error(errMsg);
			List<String> errMsgList = new ArrayList<>();
			redirectAttrs.addFlashAttribute("errMsgList", errMsgList);
			errMsgList.add(errMsg);
			whereToRedirect += "login";
			return whereToRedirect;
		}

		redirectAttrs.addFlashAttribute("theUser", username);
		whereToRedirect += "home";

		return whereToRedirect;
	}

	@GetMapping("/home")
	public String redirectHome(Model model) {
		return "home";
	}
}
