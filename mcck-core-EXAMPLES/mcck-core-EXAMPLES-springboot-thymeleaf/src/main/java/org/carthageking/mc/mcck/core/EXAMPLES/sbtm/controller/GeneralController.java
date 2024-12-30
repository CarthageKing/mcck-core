package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.controller;

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
