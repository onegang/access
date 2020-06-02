package org.onegang.access.web;

import java.util.Collection;

import org.onegang.access.entity.Request;
import org.onegang.access.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/request")
public class RequestController {

	@Autowired
	private RequestService requestService;
	
	@GetMapping
	public Collection<Request> getRequests() {
		return requestService.getRequests();
	}
	
	@GetMapping("/{id}")
	public Request getRequest(@PathVariable int id) {
		return requestService.getRequest(id);
	}
	
	@PostMapping
	public Request submitRequest(@RequestBody Request request) {
		return requestService.submitRequest(request);
	}
	
}
