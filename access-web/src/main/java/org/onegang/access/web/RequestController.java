package org.onegang.access.web;

import java.util.Collection;

import org.onegang.access.ActionDeniedException;
import org.onegang.access.dto.Action;
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
	
	@GetMapping("/filter/{filter}")
	public Collection<Request> getRequests(@PathVariable String filter) {
		return requestService.getRequests(filter);
	}
	
	@GetMapping("/{id}")
	public Request getRequest(@PathVariable int id) {
		return requestService.getRequest(id);
	}
	
	@GetMapping("/{id}/actions")
	public Collection<Action> getRequestActions(@PathVariable int id) {
		return requestService.getRequestActions(id);
	}
	
	@PostMapping("/{id}/action/{action}")
	public Request doAction(@PathVariable int id, @PathVariable Action action) throws ActionDeniedException {
		return requestService.doAction(id, action);
	}
	
	@PostMapping
	public Request submitRequest(@RequestBody Request request) {
		return requestService.submitRequest(request);
	}
	
}
