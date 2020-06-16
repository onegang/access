package org.onegang.access.web;

import java.io.IOException;
import java.util.Collection;

import org.onegang.access.dto.SysInfo;
import org.onegang.access.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lookup")
public class LookupController {

	@Autowired
	private LookupService lookupService;
	
	@GetMapping("/roles")
	public Collection<String> getRoles() throws IOException {
		return lookupService.getRoles();
	}
	
	@GetMapping("/sysinfo")
	public SysInfo getSysinfo() throws IOException {
		return lookupService.getSysinfo();
	}
	
}
